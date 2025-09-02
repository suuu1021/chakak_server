package com.green.chakak.chakak.photo.service.request;

import com.green.chakak.chakak.photo.domain.PhotoServiceInfo;
import com.green.chakak.chakak.photographer.domain.PhotographerProfile;
import lombok.Data;

public class PhotoServiceInfoRequest {

    @Data
    public static class SaveDTO {
        private String title;
        private String description;
        private String imageData;

        public PhotoServiceInfo toEntity(PhotographerProfile userInfo) {
            return PhotoServiceInfo.builder()
                    .photographerProfile(userInfo)
                    .title(title)
                    .description(description)
                    .imageData(imageData)
                    .build();
        }
    }

    @Data
    public static class UpdateDTO {
        private String title;
        private String description;
        private String imageData;

        public PhotoServiceInfo toEntity(PhotographerProfile userInfo) {
            return PhotoServiceInfo.builder()
                    .photographerProfile(userInfo)
                    .title(title)
                    .description(description)
                    .imageData(imageData)
                    .build();
        }
    }

}