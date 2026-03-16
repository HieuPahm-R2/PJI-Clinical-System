package com.vietnam.pji.model.medical;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vietnam.pji.model.AbstractEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.util.Date;

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
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private PjiEpisode episode;

    @Column(name = "sample_type", length = 100)
    private String sampleType;

    @Column(name = "incubation_days")
    private Integer incubationDays;

    @Column(name = "organism_name", length = 255)
    private String organismName;

    @Column(name = "result", length = 50)
    private String result;

    @Column(name = "gram_type", length = 20)
    private String gramType;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

}
