package com.vietnam.pji.dto.response;

import com.vietnam.pji.constant.UserStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

@Builder
@Getter
public class UserDetailResponse implements Serializable {
    private Long id;

    private String fullName;

    private String email;

    private String phone;

    private Date dateOfBirth;

    private String department;

    private UserStatus status;
    private Instant updatedAt;
    private Instant createdAt;

    private RoleOfUser role;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RoleOfUser {
        private long id;
        private String name;
    }
}
