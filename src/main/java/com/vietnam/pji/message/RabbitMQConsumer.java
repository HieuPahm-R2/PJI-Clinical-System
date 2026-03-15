package com.vietnam.pji.message;

import com.vietnam.pji.config.integration.RabbitMQConfig;
import com.vietnam.pji.constant.RunStatus;
import com.vietnam.pji.constant.TriggerType;
import com.vietnam.pji.dto.request.RabbitMQRecommendationMessage;
import com.vietnam.pji.model.agentic.AiRecommendationRun;
import com.vietnam.pji.repository.AiRecommendationRunRepository;
import com.vietnam.pji.services.AiRecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitMQConsumer {

    private final AiRecommendationService aiRecommendationService;
    private final AiRecommendationRunRepository runRepository;

    @RabbitListener(queues = RabbitMQConfig.RECOMMENDATION_QUEUE)
    public void handleRecommendationJob(RabbitMQRecommendationMessage message) {
        log.info("Received recommendation job: requestId={}, episodeId={}, triggerType={}",
                message.getRequestId(), message.getEpisodeId(), message.getTriggerType());

        // Idempotency check
        if (message.getRunId() != null) {
            Optional<AiRecommendationRun> existingRun = runRepository.findById(message.getRunId());
            if (existingRun.isPresent() && existingRun.get().getStatus() == RunStatus.SUCCESS) {
                log.info("Run {} already SUCCESS, skipping duplicate message", message.getRunId());
                return;
            }
        }

        // Check by requestId
        if (message.getRequestId() != null
                && runRepository.existsByRequestIdAndStatus(message.getRequestId(), RunStatus.SUCCESS)) {
            log.info("Request {} already SUCCESS, skipping duplicate message", message.getRequestId());
            return;
        }

        try {
            TriggerType triggerType = TriggerType.valueOf(message.getTriggerType());
            aiRecommendationService.generateRecommendation(message.getEpisodeId(), triggerType);
            log.info("Background recommendation completed for episodeId={}", message.getEpisodeId());
        } catch (Exception e) {
            log.error("Background recommendation failed for episodeId={}", message.getEpisodeId(), e);
            throw e; // Let RabbitMQ handle retry/DLQ
        }
    }
}
