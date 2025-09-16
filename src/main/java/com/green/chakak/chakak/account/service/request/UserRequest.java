package com.green.chakak.chakak.account.service.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.green.chakak.chakak.account.domain.User;
import com.green.chakak.chakak.account.domain.UserType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class UserRequest {


    @Data
    public static class SignupRequest {

        @NotEmpty(message = "이메일을 입력하세요")
        @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하로 입력해주세요")
        private String email;

        @NotEmpty(message = "비밀번호를 입력하세요")
        @Size(min = 6, max = 20, message = "비밀번호는 6자 이상 20자 이하로 입력해주세요")
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
        @NotEmpty(message = "이메일을 입력하세요")
        @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하로 입력해주세요")
        private String email;

        @NotEmpty(message = "비밀번호를 입력하세요")
        @Size(min = 6, max = 20, message = "비밀번호는 6자 이상 20자 이하로 입력해주세요")
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
