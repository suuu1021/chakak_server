package com.green.chakak.chakak.account.service.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.green.chakak.chakak.account.domain.User;
import lombok.Data;

public class UserRequest {


    @Data
    public static class SignupRequest {

        private String email;
        private String password;
        private String userTypeCode;
        //private String nickName; // @JsonProperty 제거하여 충돌 해결

        public User toEntity() {

            return User.builder()
                    .email(this.email)
                    .password(this.password)
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
