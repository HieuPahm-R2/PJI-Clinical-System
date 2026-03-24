package com.vietnam.pji.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import com.vietnam.pji.constant.DirectEnum;

import java.time.LocalDate;

@Getter
@Setter
public class EpisodeRequestDTO {

    @NotNull(message = "patientId must not be null")
    private Long patientId;

    @NotNull(message = "admissionDate must not be null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate admissionDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dischargeDate;

    private Integer treatmentDays;

    private String reason;

    private String department;

    private DirectEnum direct;

    private String referralSource;

    private String result;

    private String status;
}
