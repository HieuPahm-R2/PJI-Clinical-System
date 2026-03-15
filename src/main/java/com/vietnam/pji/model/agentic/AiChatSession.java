package com.vietnam.pji.model.agentic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vietnam.pji.constant.ChatType;
import com.vietnam.pji.model.AbstractEntity;
import com.vietnam.pji.model.medical.PjiEpisode;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ai_chat_sessions")
public class AiChatSession extends AbstractEntity<Long> implements Serializable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "episode_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private PjiEpisode episode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "run_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private AiRecommendationRun run;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_item_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private AiRecommendationItem currentItem;

    @Enumerated(EnumType.STRING)
    @Column(name = "chat_type", length = 30)
    private ChatType chatType;

    @Column(name = "title", length = 500)
    private String title;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "context_json", columnDefinition = "jsonb")
    private String contextJson;
}
