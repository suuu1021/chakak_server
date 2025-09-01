package com.green.chakak.chakak.account.user;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class UserResponse {


    @Data
    public static class SignupResponse {
        private Long userId;
        private String email;
        private String userTypeCode;
        private User.UserStatus status;
        private LocalDateTime createdAt;

        @Builder
        public SignupResponse(Long userId, String email, String userTypeCode, User.UserStatus status, LocalDateTime createdAt) {
            this.userId = userId;
            this.email = email;
            this.userTypeCode = userTypeCode;
            this.status = status;
            this.createdAt = createdAt;
        }

        @Getter
        @Builder
        public static class LoginResponse {
            private String tokenType;    // 예: "Bearer"
            private String accessToken;  // JWT 액세스 토큰
            private Timestamp issuedAt;  // 발급 시각(옵션)
            private Timestamp expiresAt; // 만료 시각(옵션)
            private LoginUser user;      // 로그인된 최소 유저 정보
        }


    }



}
