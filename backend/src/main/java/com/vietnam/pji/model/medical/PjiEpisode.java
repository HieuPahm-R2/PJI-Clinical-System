package com.vietnam.pji.model.medical;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vietnam.pji.constant.DirectEnum;
import com.vietnam.pji.model.AbstractEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pji_episodes")
public class PjiEpisode extends AbstractEntity<Long> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private Patient patient;

    @Column(name = "admission_date", nullable = false)
    private LocalDate admissionDate;

    @Column(name = "discharge_date")
    private LocalDate dischargeDate;

    @Column(name = "treatment_days")
    private Integer treatmentDays;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "department", length = 255)
    private String department;

    @Enumerated(EnumType.STRING)
    @Column(name = "direct")
    private DirectEnum direct;

    @Column(name = "referral_source", length = 255)
    private String referralSource;

    @Column(name = "result", length = 30)
    private String result;
    @Column(name = "status", length = 100)
    private String status;
}
