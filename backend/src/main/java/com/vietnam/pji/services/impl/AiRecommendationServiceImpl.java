package com.vietnam.pji.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vietnam.pji.constant.*;
import com.vietnam.pji.dto.request.AiRecommendationGenerateRequestDTO;
import com.vietnam.pji.dto.response.AiRecommendationGenerateResponseDTO;
import com.vietnam.pji.dto.response.AiRecommendationRunDetailDTO;
import com.vietnam.pji.dto.response.PaginationResultDTO;
import com.vietnam.pji.exception.BusinessException;
import com.vietnam.pji.exception.ResourceNotFoundException;
import com.vietnam.pji.model.agentic.*;
import com.vietnam.pji.model.medical.PjiEpisode;
import com.vietnam.pji.repository.*;
import com.vietnam.pji.dto.request.RabbitMQRecommendationMessage;
import com.vietnam.pji.message.RabbitMQPublisher;
import com.vietnam.pji.services.AiRecommendationService;
import com.vietnam.pji.services.AiServiceClient;
import com.vietnam.pji.services.EpisodeSnapshotAssemblerService;
import com.vietnam.pji.services.EpisodeSnapshotAssemblerService.SnapshotBuildResult;
import com.vietnam.pji.services.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiRecommendationServiceImpl implements AiRecommendationService {

    private static final int MAX_RUNS_PER_EPISODE = 5;
    private static final long RUN_DETAIL_CACHE_TTL = 1800; // 30 minutes

    private final EpisodeRepository episodeRepository;
    private final CaseClinicalSnapshotRepository snapshotRepository;
    private final AiRecommendationRunRepository runRepository;
    private final AiRecommendationItemRepository itemRepository;
    private final AiRagCitationRepository citationRepository;
    private final EpisodeSnapshotAssemblerService snapshotAssemblerService;
    private final AiServiceClient aiServiceClient;
    private final RabbitMQPublisher rabbitMQPublisher;
    private final ObjectMapper objectMapper;
    private final RedisService redisService;

    @Override
    public AiRecommendationRunDetailDTO generateRecommendation(Long episodeId, TriggerType triggerType) {
        PjiEpisode episode = episodeRepository.findById(episodeId)
                .orElseThrow(() -> new ResourceNotFoundException("Episode not found: " + episodeId));

        // Enforce max runs per episode
        long runCount = runRepository.countByEpisodeId(episodeId);
        if (runCount >= MAX_RUNS_PER_EPISODE) {
            throw new BusinessException("Đã đạt giới hạn " + MAX_RUNS_PER_EPISODE
                    + " lần gọi AI cho bệnh án này. Không thể tạo thêm.");
        }

        // TX1: Build snapshot + create run
        SnapshotBuildResult buildResult = snapshotAssemblerService.buildSnapshot(episodeId);

        CaseClinicalSnapshot snapshot = createSnapshot(episode, buildResult);
        AiRecommendationRun run = createRun(episode, snapshot, triggerType);

        String requestId = run.getRequestId();

        // Call AI outside transaction
        AiRecommendationGenerateResponseDTO aiResponse;
        try {
            AiRecommendationGenerateRequestDTO request = AiRecommendationGenerateRequestDTO.builder()
                    .requestId(requestId)
                    .triggerType(triggerType.name())
                    .episodeId(episodeId)
                    .snapshotId(snapshot.getId())
                    .snapshotDataJson(buildResult.getSnapshotDataJson())
                    .options(AiRecommendationGenerateRequestDTO.Options.builder().build())
                    .build();

            aiResponse = aiServiceClient.generateRecommendation(request);
        } catch (Exception e) {
            log.error("AI service call failed for requestId={}", requestId, e);
            handleAiError(run.getId(), e);
            throw new RuntimeException("AI service call failed: " + e.getMessage(), e);
        }

        // TX2: Save results
        return saveAiResults(run.getId(), aiResponse);
    }

    @Override
    public AiRecommendationRunDetailDTO generateRecommendationAsync(Long episodeId, TriggerType triggerType) {
        PjiEpisode episode = episodeRepository.findById(episodeId)
                .orElseThrow(() -> new ResourceNotFoundException("Episode not found: " + episodeId));

        // Enforce max runs per episode
        long runCount = runRepository.countByEpisodeId(episodeId);
        if (runCount >= MAX_RUNS_PER_EPISODE) {
            throw new BusinessException("Đã đạt giới hạn " + MAX_RUNS_PER_EPISODE
                    + " lần gọi AI cho bệnh án này. Không thể tạo thêm.");
        }

        // Build snapshot + create run (same as sync)
        SnapshotBuildResult buildResult = snapshotAssemblerService.buildSnapshot(episodeId);
        CaseClinicalSnapshot snapshot = createSnapshot(episode, buildResult);
        AiRecommendationRun run = createRun(episode, snapshot, triggerType);

        // Publish to RabbitMQ — Python worker will process asynchronously
        RabbitMQRecommendationMessage message = RabbitMQRecommendationMessage.builder()
                .requestId(run.getRequestId())
                .runId(run.getId())
                .episodeId(episodeId)
                .snapshotId(snapshot.getId())
                .triggerType(triggerType.name())
                .snapshotDataJson(buildResult.getSnapshotDataJson())
                .options(Map.of("language", "vi", "include_citations", true, "top_k", 5))
                .build();

        rabbitMQPublisher.publishRecommendationJob(message);

        log.info("Published async recommendation job: requestId={}, runId={}, episodeId={}",
                run.getRequestId(), run.getId(), episodeId);

        // Return immediately with PROCESSING status — client polls GET /runs/{runId}
        return AiRecommendationRunDetailDTO.builder()
                .run(run)
                .items(Collections.emptyList())
                .citations(Collections.emptyList())
                .build();
    }

    @Transactional
    protected CaseClinicalSnapshot createSnapshot(PjiEpisode episode, SnapshotBuildResult buildResult) {
        int nextSnapshotNo = snapshotRepository.findMaxSnapshotNoByEpisodeId(episode.getId()) + 1;

        CaseClinicalSnapshot snapshot = CaseClinicalSnapshot.builder()
                .episode(episode)
                .snapshotNo(nextSnapshotNo)
                .dataCompletenessScore(buildResult.getCompletenessScore())
                .build();

        try {
            snapshot.setSnapshotDataJson(objectMapper.writeValueAsString(buildResult.getSnapshotDataJson()));
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize snapshot data", e);
            throw new RuntimeException("Failed to serialize snapshot data", e);
        }

        return snapshotRepository.save(snapshot);
    }

    @Transactional
    protected AiRecommendationRun createRun(PjiEpisode episode, CaseClinicalSnapshot snapshot,
            TriggerType triggerType) {
        int nextRunNo = runRepository.findMaxRunNoByEpisodeId(episode.getId()) + 1;

        AiRecommendationRun run = AiRecommendationRun.builder()
                .episode(episode)
                .snapshot(snapshot)
                .runNo(nextRunNo)
                .triggerType(triggerType)
                .status(RunStatus.PROCESSING)
                .requestId(UUID.randomUUID().toString())
                .build();

        return runRepository.save(run);
    }

    @Transactional
    protected void handleAiError(Long runId, Exception e) {
        AiRecommendationRun run = runRepository.findById(runId).orElse(null);
        if (run != null) {
            boolean isTimeout = e.getMessage() != null && e.getMessage().toLowerCase().contains("timeout");
            run.setStatus(isTimeout ? RunStatus.TIMEOUT : RunStatus.FAILED);
            run.setErrorMessage(
                    e.getMessage() != null ? e.getMessage().substring(0, Math.min(e.getMessage().length(), 2000))
                            : "Unknown error");
            runRepository.save(run);
        }
    }

    @Transactional
    protected AiRecommendationRunDetailDTO saveAiResults(Long runId, AiRecommendationGenerateResponseDTO response) {
        AiRecommendationRun run = runRepository.findById(runId)
                .orElseThrow(() -> new ResourceNotFoundException("Run not found: " + runId));

        // Validate response
        if (response == null || response.getItems() == null || response.getItems().isEmpty()) {
            run.setStatus(RunStatus.FAILED);
            run.setErrorMessage("AI response missing required items");
            runRepository.save(run);
            throw new RuntimeException("AI response validation failed: missing items");
        }

        // Update run
        run.setStatus("SUCCESS".equals(response.getStatus()) ? RunStatus.SUCCESS : RunStatus.PARTIAL);
        if (response.getModel() != null) {
            run.setModelName(response.getModel().getName());
            run.setModelVersion(response.getModel().getVersion());
        }
        run.setLatencyMs(response.getLatencyMs());

        // try {
        // if (response.getAssessmentJson() != null) {
        // run.setAssessmentJson(objectMapper.writeValueAsString(response.getAssessmentJson()));
        // }
        // if (response.getExplanationJson() != null) {
        // run.setExplanationJson(objectMapper.writeValueAsString(response.getExplanationJson()));
        // }
        // if (response.getWarningsJson() != null) {
        // run.setWarningsJson(objectMapper.writeValueAsString(response.getWarningsJson()));
        // }
        // } catch (JsonProcessingException e) {
        // log.warn("Failed to serialize AI response JSON fields", e);
        // }

        runRepository.save(run);

        // Save items
        Map<String, AiRecommendationItem> itemKeyMap = new HashMap<>();
        List<AiRecommendationItem> savedItems = new ArrayList<>();

        for (AiRecommendationGenerateResponseDTO.ItemDTO itemDTO : response.getItems()) {
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
            savedItems.add(saved);

            if (itemDTO.getClientItemKey() != null) {
                itemKeyMap.put(itemDTO.getClientItemKey(), saved);
            }
        }

        // Save citations
        List<AiRagCitation> savedCitations = new ArrayList<>();
        if (response.getCitations() != null) {
            for (AiRecommendationGenerateResponseDTO.CitationDTO citDTO : response.getCitations()) {
                AiRagCitation citation = AiRagCitation.builder()
                        .run(run)
                        .sourceType(parseSourceType(citDTO.getSourceType()))
                        .sourceTitle(citDTO.getSourceTitle())
                        .sourceUri(citDTO.getSourceUri())
                        .snippet(citDTO.getSnippet())
                        .relevanceScore(citDTO.getRelevanceScore())
                        .citedFor(citDTO.getCitedFor())
                        .build();

                // Map citation to item via client_item_key
                if (citDTO.getClientItemKey() != null && itemKeyMap.containsKey(citDTO.getClientItemKey())) {
                    citation.setItem(itemKeyMap.get(citDTO.getClientItemKey()));
                }

                savedCitations.add(citationRepository.save(citation));
            }
        }

        return AiRecommendationRunDetailDTO.builder()
                .run(run)
                .items(savedItems)
                .citations(savedCitations)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AiRecommendationRunDetailDTO getRunDetail(Long runId) {
        // Check cache for terminal runs
        try {
            String cached = redisService.getCachedRunDetail(runId);
            if (cached != null) {
                log.debug("Run detail cache hit for runId={}", runId);
                return objectMapper.readValue(cached, AiRecommendationRunDetailDTO.class);
            }
        } catch (Exception e) {
            log.warn("Failed to read run detail cache for runId={}, loading from DB", runId);
        }

        AiRecommendationRun run = runRepository.findById(runId)
                .orElseThrow(() -> new ResourceNotFoundException("Run not found: " + runId));

        List<AiRecommendationItem> items = itemRepository.findByRunIdOrderByPriorityOrderAsc(runId);
        List<AiRagCitation> citations = citationRepository.findByRunId(runId);

        AiRecommendationRunDetailDTO detail = AiRecommendationRunDetailDTO.builder()
                .run(run)
                .items(items)
                .citations(citations)
                .build();

        // Only cache terminal statuses (immutable data)
        if (isTerminalStatus(run.getStatus())) {
            try {
                redisService.cacheRunDetail(runId, objectMapper.writeValueAsString(detail), RUN_DETAIL_CACHE_TTL);
                log.debug("Run detail cached for runId={}", runId);
            } catch (Exception e) {
                log.warn("Failed to cache run detail for runId={}", runId);
            }
        }

        return detail;
    }

    private boolean isTerminalStatus(RunStatus status) {
        return status == RunStatus.SUCCESS || status == RunStatus.FAILED
                || status == RunStatus.PARTIAL || status == RunStatus.TIMEOUT;
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResultDTO getRunHistory(Long episodeId, Pageable pageable) {
        if (!episodeRepository.existsById(episodeId)) {
            throw new ResourceNotFoundException("Episode not found: " + episodeId);
        }

        Page<AiRecommendationRun> page = runRepository.findByEpisodeIdOrderByCreatedAtDesc(episodeId, pageable);

        PaginationResultDTO.Meta meta = new PaginationResultDTO.Meta();
        meta.setPage(page.getNumber() + 1);
        meta.setPageSize(page.getSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        PaginationResultDTO result = new PaginationResultDTO();
        result.setMeta(meta);
        result.setResult(page.getContent());
        return result;
    }

    @Override
    public AiRecommendationRunDetailDTO retryRun(Long runId) {
        AiRecommendationRun existingRun = runRepository.findById(runId)
                .orElseThrow(() -> new ResourceNotFoundException("Run not found: " + runId));

        if (existingRun.getStatus() != RunStatus.FAILED && existingRun.getStatus() != RunStatus.TIMEOUT) {
            throw new IllegalStateException("Can only retry FAILED or TIMEOUT runs");
        }

        return generateRecommendation(existingRun.getEpisode().getId(), existingRun.getTriggerType());
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
}
