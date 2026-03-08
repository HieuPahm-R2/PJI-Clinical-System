package com.vietnam.pji.model.medical;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sensitivity_results")
public class SensitivityResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "culture_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private CultureResult culture;

    @Column(name = "antibiotic_name", nullable = false, length = 100)
    private String antibioticName;

    @Column(name = "mic_value", length = 20)
    private String micValue;

    @Column(name = "sensitivity_code", length = 10)
    private String sensitivityCode;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;
}
