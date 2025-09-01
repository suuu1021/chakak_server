package com.green.chakak.chakak.account.user;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class UserResponse {

    @Getter
    @Builder
    public class UserSignupResponse {
        private Long userId;
        private String email;
        private String userTypeCode;
        private User.UserStatus status;
        private LocalDateTime createdAt;
    }



}
