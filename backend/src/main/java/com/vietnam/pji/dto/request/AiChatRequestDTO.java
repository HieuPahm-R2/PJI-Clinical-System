package com.vietnam.pji.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Payload sent from Spring Boot to FastAPI for chat.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiChatRequestDTO implements Serializable {

    private String question;

    @JsonProperty("episode_summary")
    private Map<String, Object> episodeSummary;

    @JsonProperty("recommendation_context")
    private Map<String, Object> recommendationContext;

    @JsonProperty("chat_history")
    private List<ChatMessageDTO> chatHistory;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatMessageDTO implements Serializable {
        private String role;
        private String content;
    }
}
