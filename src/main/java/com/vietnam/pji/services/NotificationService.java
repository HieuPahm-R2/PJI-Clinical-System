package com.vietnam.pji.services;

import com.vietnam.pji.model.agentic.AiPrediction;

public interface NotificationService {

    /**
     * Notify the doctor that the AI prediction for an episode is ready.
     */
    void notifyPredictionReady(Long episodeId, AiPrediction prediction);
}
