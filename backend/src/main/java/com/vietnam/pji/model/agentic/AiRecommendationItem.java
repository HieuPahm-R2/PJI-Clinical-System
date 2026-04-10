package com.vietnam.pji.model.agentic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vietnam.pji.constant.ItemCategory;
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
@Table(name = "ai_recommendation_items")
public class AiRecommendationItem extends AbstractEntity<Long> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "run_id", nullable = false)
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private AiRecommendationRun run;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 30)
    private ItemCategory category;

    @Column(name = "title", length = 500)
    private String title;

    @Column(name = "priority_order")
    private Integer priorityOrder;

    @Column(name = "is_primary")
    private Boolean isPrimary;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "item_json", columnDefinition = "jsonb")
    private String itemJson;
}
