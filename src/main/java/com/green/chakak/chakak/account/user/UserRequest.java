package com.green.chakak.chakak.account.user;

import lombok.Data;

public class UserRequest {


    @Data
    public static class SignupRequest {

        private String email;
        private String password;
        private String userTypeCode;
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
