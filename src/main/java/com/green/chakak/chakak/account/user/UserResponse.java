package com.green.chakak.chakak.account.user;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

public class UserResponse {


    @Data
    public static class UserSignupResponse {
        private Long userId;
        private String email;
        private String userTypeCode;
        private User.UserStatus status;
        private LocalDateTime createdAt;

        @Builder
        public UserSignupResponse(Long userId, String email, String userTypeCode, User.UserStatus status, LocalDateTime createdAt) {
            this.userId = userId;
            this.email = email;
            this.userTypeCode = userTypeCode;
            this.status = status;
            this.createdAt = createdAt;
        }


    }



}
