package com.green.chakak.chakak.admin.service.request;

import com.green.chakak.chakak.account.domain.User;
import com.green.chakak.chakak.admin.domain.Admin;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

public class AdminRequest {

    @Data
    public static class LoginRequest {
        @NotBlank(message = "사용자명을 입력해주세요")
        private String name;
        @NotBlank(message = "비밀번호를 입력해주세요")
        private String password;
        public Admin toEntity() {
            return Admin.builder()
                    .adminName(this.name)
                    .password(this.password)
                    .build();

        }
    }


}
