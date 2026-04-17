package com.vietnam.pji.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vietnam.pji.config.integration.RabbitMQConfig;
import com.vietnam.pji.constant.*;
import com.vietnam.pji.dto.response.RabbitMQRecommendationResultMessage;
import com.vietnam.pji.model.agentic.*;
import com.vietnam.pji.repository.*;
import com.vietnam.pji.services.PendingLabTaskService;
import com.vietnam.pji.services.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Consumes AI processing results from the RabbitMQ result queue.
 * The Python RAG worker publishes results here after processing.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitMQConsumer {

    private final AiRecommendationRunRepository runRepository;
    private final AiRecommendationItemRepository itemRepository;
    private final AiRagCitationRepository citationRepository;
    private final ObjectMapper objectMapper;
    private final PendingLabTaskService pendingLabTaskService;
    private final RedisService redisService;

    @RabbitListener(queues = RabbitMQConfig.RECOMMENDATION_RESULT_QUEUE)
    @Transactional
    public void handleRecommendationResult(RabbitMQRecommendationResultMessage result) {
        String requestId = result.getRequestId();
        Long runId = result.getRunId();

        log.info("Received AI result from queue: requestId={}, runId={}, status={}",
                requestId, runId, result.getStatus());

        // Find the run — by runId first, fallback to requestId
        AiRecommendationRun run = null;
        if (runId != null) {
            run = runRepository.findById(runId).orElse(null);
        }
        if (run == null && requestId != null) {
            run = runRepository.findByRequestId(requestId).orElse(null);
        }
        if (run == null) {
            log.error("Cannot find run for result: runId={}, requestId={}", runId, requestId);
            return;
        }

        // Idempotency: skip if already succeeded
        if (run.getStatus() == RunStatus.SUCCESS) {
            log.info("Run {} already SUCCESS, skipping duplicate result", run.getId());
            return;
        }

        // Handle FAILED status
        if ("FAILED".equals(result.getStatus())) {
            run.setStatus(RunStatus.FAILED);
            run.setErrorMessage(result.getErrorMessage() != null
                    ? result.getErrorMessage().substring(0, Math.min(result.getErrorMessage().length(), 2000))
                    : "AI processing failed");
            run.setLatencyMs(result.getLatencyMs());
            runRepository.save(run);
            log.warn("AI processing failed for runId={}: {}", run.getId(), result.getErrorMessage());
            return;
        }

        // Validate response has items
        if (result.getItems() == null || result.getItems().isEmpty()) {
            run.setStatus(RunStatus.FAILED);
            run.setErrorMessage("AI response missing required items");
            runRepository.save(run);
            log.warn("AI result has no items for runId={}", run.getId());
            return;
        }

        // Update run with AI results
        run.setStatus("SUCCESS".equals(result.getStatus()) ? RunStatus.SUCCESS : RunStatus.PARTIAL);
        run.setLatencyMs(result.getLatencyMs());

        if (result.getModel() != null) {
            run.setModelName(result.getModel().getName());
            run.setModelVersion(result.getModel().getVersion());
        }

        // try {
        // if (result.getAssessmentJson() != null) {
        // run.setAssessmentJson(objectMapper.writeValueAsString(result.getAssessmentJson()));
        // }
        // if (result.getExplanationJson() != null) {
        // run.setExplanationJson(objectMapper.writeValueAsString(result.getExplanationJson()));
        // }
        // if (result.getWarningsJson() != null) {
        // run.setWarningsJson(objectMapper.writeValueAsString(result.getWarningsJson()));
        // }
        // } catch (JsonProcessingException e) {
        // log.warn("Failed to serialize AI result JSON fields for runId={}",
        // run.getId(), e);
        // }

        // Store data completeness on the run for frontend display
        if (result.getDataCompleteness() != null) {
            run.setDataCompletenessJson(result.getDataCompleteness());
        }

        runRepository.save(run);

        // Auto-create pending lab tasks from completeness missing items
        createPendingTasksFromCompleteness(run, result.getDataCompleteness());

        // Save items
        Map<String, AiRecommendationItem> itemKeyMap = new HashMap<>();

        for (RabbitMQRecommendationResultMessage.ItemDTO itemDTO : result.getItems()) {
            AiRecommendationItem item = AiRecommendationItem.builder()
                    .run(run)
                    .category(parseCategory(itemDTO.getCategory()))
                    .title(itemDTO.getTitle())
                    .priorityOrder(itemDTO.getPriorityOrder())
                    .isPrimary(itemDTO.getIsPrimary())
                    .build();

            try {
                if (itemDTO.getItemJson() != null) {
                    item.setItemJson(objectMapper.writeValueAsString(itemDTO.getItemJson()));
                }
            } catch (JsonProcessingException e) {
                log.warn("Failed to serialize item_json for category={}", itemDTO.getCategory());
            }

            AiRecommendationItem saved = itemRepository.save(item);
            if (itemDTO.getClientItemKey() != null) {
                itemKeyMap.put(itemDTO.getClientItemKey(), saved);
            }
        }

        // Save citations
        if (result.getCitations() != null) {
            for (RabbitMQRecommendationResultMessage.CitationDTO citDTO : result.getCitations()) {
                AiRagCitation citation = AiRagCitation.builder()
                        .run(run)
                        .sourceType(parseSourceType(citDTO.getSourceType()))
                        .sourceTitle(citDTO.getSourceTitle())
                        .sourceUri(citDTO.getSourceUri())
                        .snippet(citDTO.getSnippet())
                        .relevanceScore(citDTO.getRelevanceScore())
                        .citedFor(citDTO.getCitedFor())
                        .build();

                if (citDTO.getClientItemKey() != null && itemKeyMap.containsKey(citDTO.getClientItemKey())) {
                    citation.setItem(itemKeyMap.get(citDTO.getClientItemKey()));
                }

                citationRepository.save(citation);
            }
        }

        // Evict run detail cache so next poll gets fresh data
        redisService.evictRunDetail(run.getId());

        log.info("Successfully saved AI result for runId={}: {} items, {} citations",
                run.getId(),
                result.getItems().size(),
                result.getCitations() != null ? result.getCitations().size() : 0);
    }

    private ItemCategory parseCategory(String category) {
        try {
            return ItemCategory.valueOf(category);
        } catch (Exception e) {
            log.warn("Unknown item category: {}", category);
            return ItemCategory.DIAGNOSTIC_TEST;
        }
    }

    private SourceType parseSourceType(String sourceType) {
        try {
            return SourceType.valueOf(sourceType);
        } catch (Exception e) {
            log.warn("Unknown source type: {}", sourceType);
            return SourceType.GUIDELINE;
        }
    }

    @SuppressWarnings("unchecked")
    private void createPendingTasksFromCompleteness(AiRecommendationRun run,
                                                    Map<String, Object> dataCompleteness) {
        if (dataCompleteness == null) return;

        List<Map<String, Object>> missingItems =
                (List<Map<String, Object>>) dataCompleteness.get("missing_items");
        if (missingItems == null || missingItems.isEmpty()) return;

        try {
            Long episodeId = run.getEpisode().getId();
            Long patientId = run.getEpisode().getPatient() != null
                    ? run.getEpisode().getPatient().getId() : null;
            // Assign to the user who created this run
            Long userId = null;
            if (run.getCreatedBy() != null) {
                // createdBy stores the email; we pass null for now — the controller
                // endpoint for manual creation handles userId. For async runs the
                // frontend will call the create-from-completeness endpoint with the
                // logged-in user, so we keep this as a fallback.
                userId = null;
            }

            pendingLabTaskService.createFromCompleteness(
                    episodeId, patientId, userId, run.getId(), missingItems);
        } catch (Exception e) {
            log.warn("Failed to create pending tasks from completeness for runId={}: {}",
                    run.getId(), e.getMessage());
        }
    }
}
