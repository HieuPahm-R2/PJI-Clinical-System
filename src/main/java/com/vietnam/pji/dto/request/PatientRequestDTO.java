package com.vietnam.pji.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vietnam.pji.constant.GenderEnum;
import com.vietnam.pji.utils.validators.EnumPattern;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Map;

@Getter
@Setter
public class PatientRequestDTO {

    @NotBlank(message = "fullName must not be blank")
    private String fullName;

    @JsonProperty("patientCode")
    private String patientCode;

    @NotNull(message = "dateOfBirth must not be null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate dateOfBirth;

    @EnumPattern(name = "gender", regexp = "MALE|FEMALE|OTHER")
    private GenderEnum gender;

    private String identityCard;

    private String insuranceNumber;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate insuranceExpired;

    private String nationality;

    private String ethnicity;

    private String phone;

    private String career;

    private String subject;

    private String address;

    private Map<String, Object> relativeInfo;
}
