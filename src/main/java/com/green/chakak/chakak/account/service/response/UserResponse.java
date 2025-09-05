package com.green.chakak.chakak.account.service.response;

import com.green.chakak.chakak.account.domain.User;
import lombok.*;

import java.time.LocalDateTime;

public class UserResponse {


    @Builder
    @Data
    public static class SignupResponse {
        private Long userId;
        private String email;
        private String userTypeCode;
        private User.UserStatus status;
        private LocalDateTime createdAt;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LoginResponse {
        private String tokenType;     // ex)"Bearer"
        private String accessToken;   // JWT
        private Long userId;
        private String email;
        private String nickname;
        // 필요하면 refreshToken, roles 등 추가
    }

    @Builder
    @Data
    public static class UpdateResponse {
        private Long userId;
        private String email;
        private String userTypeCode;
        private User.UserStatus status;
        private LocalDateTime updatedAt;

        public static UpdateResponse from(User user) {
            return UpdateResponse.builder()
                    .userId(user.getUserId())
                    .email(user.getEmail())
                    .userTypeCode(user.getUserType().getTypeCode())
                    .status(user.getStatus())
                    .updatedAt(user.getUpdatedAt() != null ? user.getUpdatedAt().toLocalDateTime() : java.time.LocalDateTime.now())
                    .build();
        }
    }
}
