package com.green.chakak.chakak.account.user;

import lombok.Getter;

public class UserRequest {


    @Getter
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
    @Getter
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

}
