package com.vietnam.pji.model.agentic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vietnam.pji.model.AbstractEntity;
import com.vietnam.pji.model.medical.PjiEpisode;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ai_predictions")
public class AiPrediction extends AbstractEntity<Long> implements Serializable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "episode_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private PjiEpisode episode;

    @Column(name = "risk_level", length = 50)
    private String riskLevel;

    @Column(name = "infection_probability", precision = 5, scale = 2)
    private BigDecimal infectionProbability;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "reasoning_json", columnDefinition = "jsonb")
    private String reasoningJson;
}
