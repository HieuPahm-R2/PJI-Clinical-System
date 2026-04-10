package com.vietnam.pji.model.agentic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vietnam.pji.model.AbstractEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ai_chat_messages")
public class AiChatMessage extends AbstractEntity<Long> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "episode", "run", "currentItem" })
    private AiChatSession session;

    @Column(name = "role", length = 20, nullable = false)
    private String role;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "latency_ms")
    private Long latencyMs;

    @Column(name = "tokens_used")
    private Integer tokensUsed;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "references_json", columnDefinition = "jsonb")
    private String referencesJson;
}
