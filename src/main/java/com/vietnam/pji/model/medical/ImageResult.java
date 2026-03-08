package com.vietnam.pji.model.medical;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "image_results")
public class ImageResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "episode_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private PjiEpisode episode;

    @Column(name = "type", length = 50)
    private String type;

    @Column(name = "imaging_date")
    private LocalDate imagingDate;

    @Column(name = "findings", columnDefinition = "TEXT")
    private String findings;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "file_metadata", columnDefinition = "jsonb")
    private String fileMetadata;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;
}
