package com.green.chakak.chakak.admin.service.response;

import com.green.chakak.chakak.admin.domain.AdminProfile;
import lombok.Data;

public class AdminProfileResponse {

    @Data
    public static class DetailDTO {
        private Long profileId;
        private String nickName;
        private String email;
        private String imageUrl;
        private Long adminId;

        public DetailDTO(AdminProfile adminProfile) {
            this.profileId = adminProfile.getProfileId();
            this.nickName = adminProfile.getNickName();
            this.email = adminProfile.getEmail();
            this.imageUrl = adminProfile.getImageUrl();

            // Admin 정보에서 필요한 부분만 추출
            if (adminProfile.getAdmin() != null) {
                this.adminId = adminProfile.getAdmin().getAdminId();
            }
        }
    }

    @Data
    public static class UpdateDTO {
        private Long profileId;
        private String nickName;
        private String email;
        private String imageUrl;

        public UpdateDTO(AdminProfile adminProfile) {
            this.profileId = adminProfile.getProfileId();
            this.nickName = adminProfile.getNickName();
            this.email = adminProfile.getEmail();
            this.imageUrl = adminProfile.getImageUrl();
        }
    }

}
