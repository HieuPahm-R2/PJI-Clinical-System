package com.vietnam.pji.model.medical;

import com.vietnam.pji.constant.GenderEnum;
import com.vietnam.pji.model.AbstractEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.Year;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "patients")
public class Patient extends AbstractEntity<Long> implements Serializable {

    @Column(name = "patient_code", length = 30)
    private String patientCode;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", columnDefinition = "gender_type")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
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
    private Map<String, Object> relativeInfo;

    @Override
    protected void handleBeforeCreate() {
        super.handleBeforeCreate();
        this.generatePatientCode();
    }

    public void generatePatientCode() {
        if (this.patientCode == null || this.patientCode.isEmpty()) {
            String prefix = String.valueOf(Year.now().getValue());
            String randomPart = UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 6)
                    .toUpperCase();
            this.patientCode = prefix + randomPart; // VD: 2025A3F9C1
        }
    }

}
