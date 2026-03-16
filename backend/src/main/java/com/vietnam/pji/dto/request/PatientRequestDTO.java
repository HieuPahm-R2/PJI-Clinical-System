package com.vietnam.pji.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vietnam.pji.constant.GenderEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class PatientRequestDTO {

    @NotBlank(message = "fullName must not be blank")
    private String fullName;

    @JsonProperty("patientCode")
    private String patientCode;

    @NotNull(message = "dateOfBirth must not be null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    private GenderEnum gender;


    private String identityCard;

    private String insuranceNumber;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate insuranceExpired;

    private String nationality;

    private String ethnicity;

    private String phone;

    private String career;

    private String subject;

    private String address;

    private String relativeInfo;
}
