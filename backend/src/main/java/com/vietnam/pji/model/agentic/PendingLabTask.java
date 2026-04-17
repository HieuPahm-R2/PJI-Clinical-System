package com.vietnam.pji.model.agentic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vietnam.pji.constant.PendingLabTaskStatus;
import com.vietnam.pji.model.AbstractEntity;
import com.vietnam.pji.model.medical.LabResult;
import com.vietnam.pji.model.medical.PjiEpisode;
import com.vietnam.pji.model.medical.Patient;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pending_lab_tasks", uniqueConstraints = {
        @UniqueConstraint(name = "uq_pending_episode_field", columnNames = { "episode_id", "field", "status" })
})
public class PendingLabTask extends AbstractEntity<Long> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "episode_id", nullable = false)
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private PjiEpisode episode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private Patient patient;

    @Column(name = "assigned_to_user_id")
    private Long assignedToUserId;

    @Column(name = "field", nullable = false, length = 80)
    private String field;

    @Column(name = "category", nullable = false, length = 30)
    private String category;

    @Column(name = "importance", nullable = false, length = 20)
    private String importance;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PendingLabTaskStatus status;

    @Column(name = "created_from_run_id")
    private Long createdFromRunId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fulfilled_lab_result_id")
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private LabResult fulfilledLabResult;
}
