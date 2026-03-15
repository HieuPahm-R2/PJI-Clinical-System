package com.vietnam.pji.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Payload returned from FastAPI for chat response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiChatResponseDTO implements Serializable {

    private String answer;

    @JsonProperty("latency_ms")
    private Long latencyMs;

    @JsonProperty("tokens_used")
    private Integer tokensUsed;

    @JsonProperty("references")
    private List<Map<String, Object>> references;
}
