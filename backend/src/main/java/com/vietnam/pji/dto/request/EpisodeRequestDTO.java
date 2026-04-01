package com.vietnam.pji.dto.request;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vietnam.pji.constant.DirectEnum;

import java.time.LocalDate;

@Getter
@Setter
public class EpisodeRequestDTO {

    @NotNull(message = "patientId must not be null")
    private Long patientId;

    @NotNull(message = "admissionDate must not be null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate admissionDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dischargeDate;

    private Integer treatmentDays;

    private String reason;

    private String department;

    @Enumerated(EnumType.STRING)
    private DirectEnum direct;

    private String referralSource;

    private String result;

    private String status;
}
