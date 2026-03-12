package com.vietnam.pji.services;

import com.vietnam.pji.dto.response.AiPredictionResponseDTO;
import com.vietnam.pji.dto.response.PaginationResultDTO;
import com.vietnam.pji.model.agentic.AiPrediction;
import org.springframework.data.domain.Pageable;

public interface AiPredictionService {

    /**
     * Aggregate all medical data for the episode and publish it to Kafka.
     * Returns immediately — the prediction result arrives asynchronously via consumer.
     */
    void requestAnalysis(Long episodeId);

    /**
     * Persist the AI prediction received from the Kafka response topic.
     */
    AiPrediction savePrediction(AiPredictionResponseDTO response);

    /**
     * Retrieve all predictions for a given episode (paginated).
     */
    PaginationResultDTO getByEpisode(Long episodeId, Pageable pageable);
}
