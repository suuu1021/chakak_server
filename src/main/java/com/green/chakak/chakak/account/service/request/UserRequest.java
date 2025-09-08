package com.green.chakak.chakak.account.service.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.green.chakak.chakak.account.domain.User;
import com.green.chakak.chakak.account.domain.UserType;
import lombok.Data;

public class UserRequest {


    @Data
    public static class SignupRequest {

        private String email;
        private String password;
        private String userTypeCode;
        //private String nickName; // @JsonProperty 제거하여 충돌 해결

        public User toEntity(UserType userType) {
            return User.builder()
                    .email(this.email)
                    .password(this.password)
                    .userType(userType)
                    .status(User.UserStatus.ACTIVE)//TODO: INACTIVE로 수정하기
                    .emailVerified(false)
                    .build();
        }
    }
    @Data
    public static class LoginRequest {
        private String email;
        private String password;
        public User toEntity() {
            return User.builder()
                    .email(this.email)
                    .password(this.password)
                    .build();

        }
    }

    @Data
    public static class UpdateRequest {
        private String email;
        private String password;
        public User toEntity() {
            return User.builder()
                    .email(this.email)
                    .password(this.password)
                    .build();
        }

    }
}
