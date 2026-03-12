package com.vietnam.pji.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vietnam.pji.config.KafkaConfig;
import com.vietnam.pji.dto.response.AiPredictionResponseDTO;
import com.vietnam.pji.model.agentic.AiPrediction;
import com.vietnam.pji.services.AiPredictionService;
import com.vietnam.pji.services.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiAnalysisConsumer {

    private final ObjectMapper objectMapper;
    private final AiPredictionService aiPredictionService;
    private final NotificationService notificationService;

    /**
     * Listens on the response topic published by the Agentic RAG team.
     * Deserializes the prediction, persists it, and notifies the doctor.
     */
    @KafkaListener(
            topics = KafkaConfig.TOPIC_AI_ANALYSIS_RESPONSE,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onPredictionReceived(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("[Kafka] Received prediction from topic={} partition={} offset={}", topic, partition, offset);

        AiPredictionResponseDTO response;
        try {
            response = objectMapper.readValue(message, AiPredictionResponseDTO.class);
        } catch (Exception e) {
            log.error("[Kafka] Failed to deserialize AiPredictionResponseDTO: {}", e.getMessage());
            return;
        }

        try {
            AiPrediction saved = aiPredictionService.savePrediction(response);
            notificationService.notifyPredictionReady(response.getEpisodeId(), saved);
        } catch (Exception e) {
            log.error("[Kafka] Failed to process prediction for episode {}: {}",
                    response.getEpisodeId(), e.getMessage());
        }
    }
}
