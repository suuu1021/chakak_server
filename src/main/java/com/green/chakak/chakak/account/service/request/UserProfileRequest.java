package com.green.chakak.chakak.account.service.request;

import com.green.chakak.chakak.account.domain.User;
import com.green.chakak.chakak.account.domain.UserProfile;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class UserProfileRequest {

    @Data
    public static class CreateDTO {

        // --- 충돌 해결: 로그인 정보를 사용하므로 DTO에서 ID 필드는 불필요하여 최종적으로 제거 ---

        @NotEmpty(message = "닉네임을 입력하세요")
        @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하로 입력해주세요")
        private String nickName;

        @Size(max = 100, message = "소개는 100자를 초과할 수 없습니다.")
        private String introduce;

        private String imageData;


        public UserProfile toEntity(User user) {
            return UserProfile.builder()
                    .user(user)
                    .nickName(nickName)
                    .introduce(introduce)
                    .imageData(imageData)
                    .build();
        }
    }

    @Data
    public static class UpdateDTO {
        @NotEmpty(message = "닉네임을 입력하세요")
        @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하로 입력해주세요")
        private String nickName;

        @Size(max = 100, message = "소개는 100자를 초과할 수 없습니다.")
        private String introduce;

        private String imageData;


    }
}
