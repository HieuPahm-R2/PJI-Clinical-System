package com.vietnam.pji.model.medical;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vietnam.pji.constant.CultureStatus;
import com.vietnam.pji.model.AbstractEntity;
import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "culture_results")
public class CultureResult extends AbstractEntity<Long> implements Serializable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "episode_id")
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private PjiEpisode episode;

    @Column(name = "sample_type", length = 100)
    private String sampleType;

    @Column(name = "incubation_days")
    private Integer incubationDays;

    @Column(name = "name", length = 255)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "result_status", columnDefinition = "sample_result_status")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private CultureStatus result;

    @Column(name = "gram_type", length = 20)
    private String gramType;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

}
