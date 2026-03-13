package com.vietnam.pji.model.medical;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vietnam.pji.model.AbstractEntity;
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
@Table(name = "lab_results")
public class LabResult extends AbstractEntity<Long> implements Serializable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "episode_id")
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private PjiEpisode episode;

    // Blood markers (systemic inflammation)
    @Column(name = "esr")
    private Integer esr;

    @Column(name = "wbc_blood", precision = 10, scale = 2)
    private BigDecimal wbcBlood;

    @Column(name = "neut", precision = 5, scale = 2)
    private BigDecimal neut;

    @Column(name = "mono", precision = 4, scale = 1)
    private BigDecimal mono;

    @Column(name = "rbc", precision = 5, scale = 2)
    private BigDecimal rbc;

    @Column(name = "ig", precision = 5, scale = 2)
    private BigDecimal ig;

    @Column(name = "mcv", precision = 5, scale = 2)
    private BigDecimal mcv;

    @Column(name = "mch", precision = 5, scale = 2)
    private BigDecimal mch;

    private BigDecimal dimer; // -- một xét nghiệm máu đo lường các mảnh protein
    private BigDecimal serum_il6; // -- một cytokine tiền viêm quan trọng
    private BigDecimal alpha_defensin; // alpha-defensin trong huyết thanh
    private Integer egfr;
    // Synovial fluid markers (local inflammation)
    @Column(name = "crp", precision = 10, scale = 2)
    private BigDecimal crp;

    @Column(name = "synovial_wbc")
    private Integer synovialWbc;

    @Column(name = "synovial_pmn", precision = 5, scale = 2)
    private BigDecimal synovialPmn;

    // Biochemical data (JSONB)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "biochemical_data", columnDefinition = "jsonb")
    private String biochemicalData;

}
