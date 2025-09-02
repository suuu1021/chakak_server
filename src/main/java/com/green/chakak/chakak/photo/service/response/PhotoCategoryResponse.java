package com.green.chakak.chakak.photo.service.response;

import com.green.chakak.chakak.photo.domain.PhotoServiceCategory;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class PhotoCategoryResponse {

    // 카테고리 목록 응답 DTO
    @Data
    @NoArgsConstructor
    public static class PhotoCategoryListDTO {
        private Long categoryId;
        private String categoryName;
        private String categoryImageData;

        @Builder
        public PhotoCategoryListDTO(PhotoServiceCategory category) {
            this.categoryId = category.getCategoryId();
            this.categoryName = category.getCategoryName();
            this.categoryImageData = category.getCategoryImageData();
        }
    }

    // 카테고리 상세 응답 DTO
    @Data
    @NoArgsConstructor
    public static class PhotoCategoryDetailDTO {
        private Long categoryId;
        private String categoryName;
        private String categoryImageData;

        @Builder
        public PhotoCategoryDetailDTO(PhotoServiceCategory category) {
            this.categoryId = category.getCategoryId();
            this.categoryName = category.getCategoryName();
            this.categoryImageData = category.getCategoryImageData();
        }
    }
}