package com.vietnam.pji.model.medical;

import com.vietnam.pji.constant.GenderEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 15)
    private GenderEnum gender;

    @Column(name = "identity_card", unique = true, length = 50)
    private String identityCard;

    @Column(name = "insurance_number", length = 50)
    private String insuranceNumber;

    @Column(name = "insurance_expired")
    private LocalDate insuranceExpired;

    @Column(name = "nationality", length = 50)
    private String nationality;

    @Column(name = "ethnicity", length = 50)
    private String ethnicity;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "career", length = 50)
    private String career;

    @Column(name = "subject", length = 50)
    private String subject;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "relative_info", columnDefinition = "jsonb")
    private String relativeInfo;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;
}
