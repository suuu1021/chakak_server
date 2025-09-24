package com.green.chakak.chakak.admin.service.request;

import com.green.chakak.chakak.account.domain.User;
import com.green.chakak.chakak.account.domain.UserProfile;
import com.green.chakak.chakak.admin.domain.Admin;
import com.green.chakak.chakak.admin.domain.AdminProfile;
import jakarta.validation.constraints.*;
import lombok.Data;

public class AdminProfileRequest {

    @Data
    public static class CreateDTO {

        @NotNull(message = "유저 정보를 선택하세요")
        @Positive
        private Long adminInfoId;

        @NotEmpty(message = "닉네임을 입력하세요")
        @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하로 입력해주세요")
        private String nickName;

        private String imageData;

        public AdminProfile toEntity(Admin admin) {
            return AdminProfile.builder()
                    .admin(admin)
                    .nickName(this.nickName)  // this 키워드로 명확하게 지정
                    .imageUrl(this.imageData)
                    .build();
        }
    }

    @Data
    public static class UpdateDTO {

        @NotEmpty(message = "닉네임을 입력하세요")
        @Size(min = 2, max = 50, message = "닉네임은 2자 이상 50자 이하로 입력해주세요")
        private String nickName;

        @Email(message = "올바른 이메일 형식으로 입력해주세요")
        private String email;

        private String imageUrl;
    }

}
