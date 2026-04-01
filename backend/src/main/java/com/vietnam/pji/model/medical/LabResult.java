package com.vietnam.pji.model.medical;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vietnam.pji.dto.response.Measurement;
import com.vietnam.pji.model.AbstractEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.io.Serializable;
import java.util.Map;

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
    // Chỉ số máu
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "esr", columnDefinition = "jsonb")
    private Measurement esr;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "wbc_blood", columnDefinition = "jsonb")
    private Measurement wbcBlood;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "neut", columnDefinition = "jsonb")
    private Measurement neut;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "mono", columnDefinition = "jsonb")
    private Measurement mono;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "rbc", columnDefinition = "jsonb")
    private Measurement rbc;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "ig", columnDefinition = "jsonb")
    private Measurement ig;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "mcv", columnDefinition = "jsonb")
    private Measurement mcv;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "mch", columnDefinition = "jsonb")
    private Measurement mch;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "dimer", columnDefinition = "jsonb")
    private Measurement dimer;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "serum_il6", columnDefinition = "jsonb")
    private Measurement serumIl6;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "alpha_defensin", columnDefinition = "jsonb")
    private Measurement alphaDefensin;

    // Chỉ số dịch khớp
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "crp", columnDefinition = "jsonb")
    private Measurement crp;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "synovial_wbc", columnDefinition = "jsonb")
    private Measurement synovialWbc;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "synovial_pmn", columnDefinition = "jsonb")
    private Measurement synovialPmn;

    // Biochemical data (JSONB)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "biochemical_data", columnDefinition = "jsonb")
    private Map<String, Object> biochemicalData;

}
