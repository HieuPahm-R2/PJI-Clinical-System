package com.vietnam.pji.model.medical;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vietnam.pji.constant.ImplantType;
import com.vietnam.pji.constant.InfectionType;
import com.vietnam.pji.model.AbstractEntity;
import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "clinical_records")
public class ClinicalRecord extends AbstractEntity<Long> implements Serializable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "episode_id")
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private PjiEpisode episode;

    @Column(name = "illness_onset_date")
    private LocalDate illnessOnsetDate;

    @Column(name = "blood_pressure", length = 20)
    private String bloodPressure;

    @Column(name = "bmi", precision = 4, scale = 2)
    private BigDecimal bmi;

    private Boolean fever;

    private Boolean pain;

    private Boolean erythema; // có ban đỏ

    private Boolean swelling; // sưng tấy

    private Boolean sinusTract; // có đường rò

    @Column(name = "hematogenous_suspected")
    private Boolean hematogenousSuspected; // nghi ngờ lây truyền qua đường máu
    @Column(name = "pmma_allergy")
    private Boolean pmmaAllergy;

    @Enumerated(EnumType.STRING)
    @Column(name = "suspected_infection_type")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private InfectionType suspectedInfectionType;

    private String softTissue; // tình trạng mô mềm

    @Enumerated(EnumType.STRING)
    @Column(name = "implant_stability", columnDefinition = "implant_stability_type")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private ImplantType implantStability; // độ ổn định của cấy ghép

    @Column(name = "prosthesis_joint")
    private String prosthesisJoint;

    @Column(name = "days_since_index_arthroplasty")
    private Integer daysSinceIndexArthroplasty;

    @Column(name = "notations", columnDefinition = "TEXT")
    private String notations;

}
