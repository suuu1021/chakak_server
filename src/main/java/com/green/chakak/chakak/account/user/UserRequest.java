package com.green.chakak.chakak.account.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

public class UserRequest {


    @Getter
    public static class UserSignupRequest {

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

}
