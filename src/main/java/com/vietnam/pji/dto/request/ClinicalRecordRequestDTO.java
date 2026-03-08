package com.vietnam.pji.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    private BigDecimal temperature;

    private String bloodPressure;

    private Integer heartRate;

    private Integer respiratoryRate;

    private BigDecimal bmi;

    private String localSymptoms;

    private String notations;
}
