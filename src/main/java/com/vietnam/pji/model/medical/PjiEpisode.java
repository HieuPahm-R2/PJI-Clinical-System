package com.vietnam.pji.model.medical;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vietnam.pji.model.AbstractEntity;
import com.vietnam.pji.model.auth.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pji_episodes")
public class PjiEpisode extends AbstractEntity implements Serializable {


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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

    @Column(name = "direct", length = 50)
    private String direct;

    @Column(name = "days_treatment")
    private Integer daysTreatment;

    @Column(name = "referral_source", length = 255)
    private String referralSource;

    @Column(name = "result", length = 30)
    private String result;


}
