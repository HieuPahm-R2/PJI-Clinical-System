package com.vietnam.pji.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * Payload sent from Spring Boot to FastAPI for recommendation generation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiRecommendationGenerateRequestDTO implements Serializable {

    private String requestId;
    private String triggerType;
    private Long episodeId;
    private Long snapshotId;
    private Map<String, Object> snapshotDataJson;
    private Options options;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Options implements Serializable {
        @Builder.Default
        private String language = "vi";
        @Builder.Default
        private boolean includeCitations = true;
        @Builder.Default
        private int topK = 5;
    }
}
