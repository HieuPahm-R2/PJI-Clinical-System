package com.vietnam.pji.model.agentic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vietnam.pji.constant.ReviewStatus;
import com.vietnam.pji.model.AbstractEntity;
import com.vietnam.pji.model.medical.PjiEpisode;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "doctor_recommendation_reviews")
public class DoctorRecommendationReview extends AbstractEntity<Long> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "episode_id", nullable = false)
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private PjiEpisode episode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "run_id", nullable = false)
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private AiRecommendationRun run;

    @Enumerated(EnumType.STRING)
    @Column(name = "review_status", length = 20, nullable = false)
    private ReviewStatus reviewStatus;

    @Column(name = "review_note", columnDefinition = "TEXT")
    private String reviewNote;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "modification_json", columnDefinition = "jsonb")
    private Map<String, Object> modificationJson;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

}
