package com.green.chakak.chakak.photo.service.response;

import com.green.chakak.chakak.photo.domain.PhotoServiceInfo;
import com.green.chakak.chakak.photographer.domain.PhotographerProfile;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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

        private List<PriceInfoResponse.PriceInfoListDTO> priceInfoList;
        private List<PhotoCategoryResponse.PhotoCategoryListDTO> categoryList;

        @Builder
        public PhotoServiceListDTO(PhotoServiceInfo photoServiceInfo) {
            this.serviceId = photoServiceInfo.getServiceId();
            this.title = photoServiceInfo.getTitle();
            this.description = photoServiceInfo.getDescription();
            this.imageData = photoServiceInfo.getImageData();
            this.createdAt = photoServiceInfo.getCreatedAt();
            this.updatedAt = photoServiceInfo.getUpdatedAt();

            this.priceInfoList = new ArrayList<>();
            this.categoryList = new ArrayList<>();
        }

        // 가격 정보와 카테고리 정보를 설정하는 메서드들
        public void setPriceInfoList(List<PriceInfoResponse.PriceInfoListDTO> priceInfoList) {
            this.priceInfoList = priceInfoList != null ? priceInfoList : new ArrayList<>();
        }

        public void setCategoryList(List<PhotoCategoryResponse.PhotoCategoryListDTO> categoryList) {
            this.categoryList = categoryList != null ? categoryList : new ArrayList<>();
        }
    } // END OF INNER CLASS

    @Data
    public static class PhotoServiceDetailDTO {
        private Long serviceId;
        private String title;
        private String description;
        private String imageData;
        private Timestamp createdAt;
        private Timestamp updatedAt;
        private List<PriceInfoResponse.PriceInfoListDTO> priceInfoList;  // 가격정보 추가

        // 포토그래퍼 프로필 정보
        private String businessName; // 상호명
        private String introduction; // 소개글
        private String location; // 활동 지역
        private Integer experienceYears; // 경력 연수

        private boolean canEdit;

        @Builder
        public PhotoServiceDetailDTO(PhotoServiceInfo photoServiceInfo) {
            this.serviceId = photoServiceInfo.getServiceId();
            this.title = photoServiceInfo.getTitle();
            this.description = photoServiceInfo.getDescription();
            this.imageData = photoServiceInfo.getImageData();
            this.createdAt = photoServiceInfo.getCreatedAt();
            this.updatedAt = photoServiceInfo.getUpdatedAt();

            this.businessName = photoServiceInfo.getPhotographerProfile().getBusinessName();
            this.introduction = photoServiceInfo.getPhotographerProfile().getIntroduction();
            this.location = photoServiceInfo.getPhotographerProfile().getLocation();
            this.experienceYears = photoServiceInfo.getPhotographerProfile().getExperienceYears();

            this.canEdit = false;
        }
    } // END OF INNER CLASS
}