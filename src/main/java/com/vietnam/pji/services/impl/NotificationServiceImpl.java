package com.vietnam.pji.services.impl;

import com.vietnam.pji.model.agentic.AiPrediction;
import com.vietnam.pji.services.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

    @Override
    public void notifyPredictionReady(Long episodeId, AiPrediction prediction) {
        // TODO: replace with WebSocket push / email / in-app notification when ready
        log.info("[Notification] AI prediction ready for episode {}. " +
                        "Risk level: {}, Infection probability: {}%",
                episodeId,
                prediction.getRiskLevel(),
                prediction.getInfectionProbability());
    }
}
