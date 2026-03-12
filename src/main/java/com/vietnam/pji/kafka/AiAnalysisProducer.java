package com.vietnam.pji.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vietnam.pji.config.KafkaConfig;
import com.vietnam.pji.dto.request.AiAnalysisRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiAnalysisProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Publishes the aggregated episode data to the AI analysis request topic.
     * Key = episodeId (ensures all messages for one episode go to the same partition).
     */
    public void sendAnalysisRequest(AiAnalysisRequestDTO payload) {
        String key = String.valueOf(payload.getEpisodeId());
        String value;
        try {
            value = objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            log.error("[Kafka] Failed to serialize AiAnalysisRequestDTO for episode {}: {}",
                    payload.getEpisodeId(), e.getMessage());
            throw new RuntimeException("Failed to serialize analysis request", e);
        }

        CompletableFuture<SendResult<String, String>> future =
                kafkaTemplate.send(KafkaConfig.TOPIC_AI_ANALYSIS_REQUEST, key, value);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("[Kafka] Failed to send analysis request for episode {}: {}",
                        payload.getEpisodeId(), ex.getMessage());
            } else {
                log.info("[Kafka] Analysis request sent for episode {} → partition={}, offset={}",
                        payload.getEpisodeId(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }
}
