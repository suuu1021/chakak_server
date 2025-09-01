package com.green.chakak.chakak.account.user_profile;

import com.green.chakak.chakak.account.user.User;
import lombok.Builder;
import lombok.Data;

public class UserProfileResponse {

    @Data
    public static class DetailDTO{
        private Long UserProfileId;
        private String nickName;
        private String introduce;

        public DetailDTO(UserProfile userProfile) {
            this.UserProfileId = userProfile.getUserProfileId();
            this.nickName = userProfile.getNickName();
            this.introduce = userProfile.getIntroduce();
        }

        public UserProfile toEntity(){
            return UserProfile.builder()
                    .userProfileId(this.UserProfileId)
                    .nickName(this.nickName)
                    .introduce(this.introduce)
                    .build();
        }
    }

}
