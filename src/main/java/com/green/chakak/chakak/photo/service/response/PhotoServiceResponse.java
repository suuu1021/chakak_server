package com.green.chakak.chakak.photo.service.response;

import com.green.chakak.chakak.photo.domain.PhotoServiceInfo;
import com.green.chakak.chakak.photographer.domain.PhotographerProfile;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

public class PhotoServiceResponse {

    // 포토 서비스 목록 응답 DTO
    @Data
    public static class PhotoServiceListDTO {
        private Long serviceId;
        private String title;
        private String description;
        private String imageData;
        private Timestamp createdAt;
        private Timestamp updatedAt;

        @Builder
        public PhotoServiceListDTO(PhotoServiceInfo photoServiceInfo) {
            this.serviceId = photoServiceInfo.getServiceId();
            this.title = photoServiceInfo.getTitle();
            this.description = photoServiceInfo.getDescription();
            this.imageData = photoServiceInfo.getImageData();
            this.createdAt = photoServiceInfo.getCreatedAt();
            this.updatedAt = photoServiceInfo.getUpdatedAt();
        }
    } // END OF INNER CLASS

    @Data
    public static class PhotoServiceDetailDTO {
        private Long serviceId;
        private PhotographerProfile photographerProfile;
        private String title;
        private String description;
        private String imageData;
        private Timestamp createdAt;
        private Timestamp updatedAt;
        private boolean canEdit;

        @Builder
        public PhotoServiceDetailDTO(PhotoServiceInfo photoServiceInfo) {
            this.serviceId = photoServiceInfo.getServiceId();
            this.photographerProfile = photoServiceInfo.getPhotographerProfile();
            this.title = photoServiceInfo.getTitle();
            this.description = photoServiceInfo.getDescription();
            this.imageData = photoServiceInfo.getImageData();
            this.createdAt = photoServiceInfo.getCreatedAt();
            this.updatedAt = photoServiceInfo.getUpdatedAt();
            this.canEdit = false;
        }
    } // END OF INNER CLASS
}