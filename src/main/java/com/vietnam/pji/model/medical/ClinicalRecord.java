package com.vietnam.pji.model.medical;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vietnam.pji.model.AbstractEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

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
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private PjiEpisode episode;

    @Column(name = "on_illness")
    private LocalDate onIllness;

    @Column(name = "blood_pressure", length = 20)
    private String bloodPressure;

    @Column(name = "bmi", precision = 4, scale = 2)
    private BigDecimal bmi;

    private Boolean fever;

    private Boolean pain;

    private Boolean erythema; // có ban đỏ

    private Boolean swelling;  // sưng tấy

    private Boolean sinusTract; // có đường rò

    @Column(name = "hematogenous_suspected")
    private Boolean hematogenousSuspected; // nghi ngờ lây truyền qua đường máu
    @Column(name = "pmma_allergy")
    private Boolean pmmaAllergy;

    @Column(name = "suspected_infection_type")
    private String suspectedInfectionType;

    private String softTissue; // tình trạng mô mềm

    private String implantStability; //độ ổn định của cấy ghép

    @Column(name = "prosthesis_joint")
    private String prosthesisJoint;

    @Column(name = "days_since_index_arthroplasty")
    private Integer daysSinceIndexArthroplasty;

    @Column(name = "notations", columnDefinition = "TEXT")
    private String notations;

}
