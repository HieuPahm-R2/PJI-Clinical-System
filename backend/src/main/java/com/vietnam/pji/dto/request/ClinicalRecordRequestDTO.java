package com.vietnam.pji.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class ClinicalRecordRequestDTO {

    @NotNull(message = "episodeId must not be null")
    private Long episodeId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate onIllness;

    private String bloodPressure;

    private BigDecimal bmi;

    private Boolean fever;
    private Boolean pain;
    private Boolean erythema; // có ban đỏ
    private Boolean swelling;  // sưng tấy
    private Boolean sinusTract; // có đường rò

    private Boolean hematogenousSuspected; // nghi ngờ lây truyền qua đường máu

    private Boolean pmmaAllergy;

    private String suspectedInfectionType;

    private String softTissue; // tình trạng mô mềm

    private String implantStability; //độ ổn định của cấy ghép

    private String prosthesisJoint;

    private Integer daysSinceIndexArthroplasty;

    private String notations;
}
