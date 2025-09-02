package com.green.chakak.chakak.account.user_profile;

import com.green.chakak.chakak.account.user.LoginUser;
import com.green.chakak.chakak.account.user.User;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

public class UserProfileRequest {

    @Data
    public static class CreateDTO {
        @NotEmpty(message = "닉네임을 입력하세요")
        private String nickName;
        @NotEmpty(message = "자기소개를 입력하세요")
        private String introduce;

        public UserProfile toEntity(User user){
            return UserProfile.builder()
                    .user(user)
                    .nickName(nickName)
                    .introduce(introduce)
                    .build();
        }
    }

    @Data
    public static class UpdateDTO {
        @NotEmpty(message = "닉네임을 입력하세요")
        private String nickName;
        @NotEmpty(message = "자기소개를 입력하세요")
        private String introduce;

//        public UserProfile toEntity(LoginUser loginUser , User user){
//            return UserProfile.builder()
//                    .user(user)
//                    .nickName(nickName)
//                    .introduce(introduce)
//                    .build();
//        }
    }
}
