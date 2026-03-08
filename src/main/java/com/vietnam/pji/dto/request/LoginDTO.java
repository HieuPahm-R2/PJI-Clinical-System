package com.vietnam.pji.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDTO {
    @NotBlank(message = "username not be blank")
    private String username;
    @NotBlank(message = "mật khẩu không được để trống")
    private String password;
}
