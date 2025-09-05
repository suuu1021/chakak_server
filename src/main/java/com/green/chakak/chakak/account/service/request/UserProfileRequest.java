package com.green.chakak.chakak.account.service.request;

import com.green.chakak.chakak.account.domain.User;
import com.green.chakak.chakak.account.domain.UserProfile;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class UserProfileRequest {

    @Data
    public static class CreateDTO {

        // 이 필드는 User 객체를 통해 전달받으므로 DTO에서 불필요하여 제거합니다.
        // @NotEmpty(message = "유저 아이디값은 필수 입니다.")
        // private Long userInfoId;

        @NotEmpty(message = "닉네임을 입력하세요")
        @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하로 입력해주세요")
        private String nickName;

        @Size(max = 100, message = "소개는 100자를 초과할 수 없습니다.")
        private String introduce;

        private String imageData;


        public UserProfile toEntity(User user) {
            return UserProfile.builder()
                    .user(user)
                    // ID는 DB가 자동 생성하므로 빌더에서 설정하는 코드 제거
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
