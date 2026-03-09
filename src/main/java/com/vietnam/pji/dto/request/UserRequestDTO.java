package com.vietnam.pji.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vietnam.pji.constant.GenderEnum;
import com.vietnam.pji.constant.UserStatus;
import com.vietnam.pji.model.auth.Role;
import com.vietnam.pji.utils.validators.EnumPattern;
import com.vietnam.pji.utils.validators.PhoneNumber;
import jakarta.persistence.Column;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {
    private Long id;

    @NotBlank(message = "fullName must be not blank")
    private String fullName;

    @NotBlank(message = "department must be not blank")
    private String department;

    @Email(message = "email invalid format")
    private String email;

    @PhoneNumber(message = "phone invalid format")
    private String phone;

    private String avatar;

    @NotNull(message = "dateOfBirth must be not null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "MM/dd/yyyy")
    private Date dateOfBirth;

    @EnumPattern(name = "gender", regexp = "MALE|FEMALE|OTHER")
    private GenderEnum gender;

    @NotBlank(message = "department must be not blank")
    private String password;

    @EnumPattern(name = "status", regexp = "ACTIVE|INACTIVE|NONE")
    private UserStatus status;

    private Role role;

}
