package com.green.chakak.chakak.account.service.response;

import com.green.chakak.chakak.account.domain.UserProfile;
import lombok.Data;

public class UserProfileResponse {

    @Data
    public static class DetailDTO{
        private Long userProfileId; // 필드명을 userProfileId (소문자 u)로 수정
        private String nickName;
        private String introduce;
        private String imageData;

        public DetailDTO(UserProfile userProfile) {
            this.userProfileId = userProfile.getUserProfileId(); // 수정된 필드명으로 값 할당
            this.nickName = userProfile.getNickName();
            this.introduce = userProfile.getIntroduce();
            this.imageData = userProfile.getImageData();
        }

        public UserProfile toEntity(){
            return UserProfile.builder()
                    // ID는 DB가 자동 생성하므로 빌더에서 설정하는 코드 제거
                    .nickName(this.nickName)
                    .introduce(this.introduce)
                    .imageData(this.imageData)
                    .build();
        }
    }

    @Data
    public static class UpdateDTO{
        private Long userProfileId; // 필드명을 userProfileId (소문자 u)로 수정
        private String nickName;
        private String introduce;
        private String imageData;

        public UpdateDTO(UserProfile userProfile) {
            this.userProfileId = userProfile.getUserProfileId(); // 수정된 필드명으로 값 할당
            this.nickName = userProfile.getNickName();
            this.introduce = userProfile.getIntroduce();
            this.imageData = userProfile.getImageData();
        }

        public UserProfile toEntity(){
            return UserProfile.builder()
                    // ID는 DB가 자동 생성하므로 빌더에서 설정하는 코드 제거
                    .nickName(this.nickName)
                    .introduce(this.introduce)
                    .imageData(this.imageData)
                    .build();
        }
    }
}
