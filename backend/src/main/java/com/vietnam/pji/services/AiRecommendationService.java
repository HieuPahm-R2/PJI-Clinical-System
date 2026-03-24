package com.vietnam.pji.services;

import com.vietnam.pji.constant.TriggerType;
import com.vietnam.pji.dto.response.AiRecommendationRunDetailDTO;
import com.vietnam.pji.dto.response.PaginationResultDTO;
import org.springframework.data.domain.Pageable;

public interface AiRecommendationService {

    /** Synchronous: calls AI service via HTTP, blocks until result. */
    AiRecommendationRunDetailDTO generateRecommendation(Long episodeId, TriggerType triggerType);

    /** Async: publishes to RabbitMQ, returns immediately with PROCESSING run. */
    AiRecommendationRunDetailDTO generateRecommendationAsync(Long episodeId, TriggerType triggerType);

    AiRecommendationRunDetailDTO getRunDetail(Long runId);

    PaginationResultDTO getRunHistory(Long episodeId, Pageable pageable);

    AiRecommendationRunDetailDTO retryRun(Long runId);
}
