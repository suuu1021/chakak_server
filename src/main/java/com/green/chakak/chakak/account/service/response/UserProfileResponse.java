package com.green.chakak.chakak.account.service.response;

import com.green.chakak.chakak.account.domain.UserProfile;
import lombok.Data;

public class UserProfileResponse {

    @Data
    public static class DetailDTO{
        private Long userProfileId;
        private String nickName;
        private String introduce;
        private String imageData;

        public DetailDTO(UserProfile userProfile) {
            this.userProfileId = userProfile.getUserProfileId();
            this.nickName = userProfile.getNickName();
            this.introduce = userProfile.getIntroduce();
            this.imageData = userProfile.getImageData();
        }

        public UserProfile toEntity(){
            return UserProfile.builder()
                    .nickName(this.nickName)
                    .introduce(this.introduce)
                    .imageData(this.imageData)
                    .build();
        }
    }

    @Data
    public static class UpdateDTO{
        private Long userProfileId;
        private String nickName;
        private String introduce;
        private String imageData;

        public UpdateDTO(UserProfile userProfile) {
            this.userProfileId = userProfile.getUserProfileId();
            this.nickName = userProfile.getNickName();
            this.introduce = userProfile.getIntroduce();
            this.imageData = userProfile.getImageData();
        }

        public UserProfile toEntity(){
            return UserProfile.builder()
                    .nickName(this.nickName)
                    .introduce(this.introduce)
                    .imageData(this.imageData)
                    .build();
        }
    }
}
