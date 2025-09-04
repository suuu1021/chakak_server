package com.green.chakak.chakak.photo.service.response;

import com.green.chakak.chakak.photo.domain.PhotoServiceMapping;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

public class PhotoMappingResponse {

    // 매핑 목록 응답 DTO
    @Data
    @NoArgsConstructor
    public static class PhotoMappingListDTO {
        private Long mappingId;
        private Long serviceId;
        private String serviceTitle;
        private Long categoryId;
        private String categoryName;
        private Timestamp createdAt;

        @Builder
        public PhotoMappingListDTO(PhotoServiceMapping mapping) {
            this.mappingId = mapping.getMappingId();
            this.serviceId = mapping.getPhotoServiceInfo().getServiceId();
            this.serviceTitle = mapping.getPhotoServiceInfo().getTitle();
            this.categoryId = mapping.getPhotoServiceCategory().getCategoryId();
            this.categoryName = mapping.getPhotoServiceCategory().getCategoryName();
            this.createdAt = mapping.getCreatedAt();
        }
    }

    // 매핑 상세 응답 DTO
    @Data
    @NoArgsConstructor
    public static class PhotoMappingDetailDTO {
        private Long mappingId;
        private PhotoServiceResponse.PhotoServiceListDTO photoService;
        private PhotoCategoryResponse.PhotoCategoryListDTO photoCategory;
        private Timestamp createdAt;

        @Builder
        public PhotoMappingDetailDTO(PhotoServiceMapping mapping) {
            this.mappingId = mapping.getMappingId();
            this.photoService = new PhotoServiceResponse.PhotoServiceListDTO(mapping.getPhotoServiceInfo());
            this.photoCategory = new PhotoCategoryResponse.PhotoCategoryListDTO(mapping.getPhotoServiceCategory());
            this.createdAt = mapping.getCreatedAt();
        }
    }
}