package com.green.chakak.chakak.photo.service.request;

import com.green.chakak.chakak.photo.domain.PhotoServiceCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class PhotoCategoryRequest {

    // 카테고리 생성 요청 DTO
    @Data
    @NoArgsConstructor
    public static class SaveDTO {

        @NotBlank(message = "카테고리명을 입력해주세요.")
        @Size(max = 50, message = "카테고리명은 50자 이하로 입력해주세요.")
        private String categoryName;

        @NotBlank(message = "카테고리 이미지를 입력해주세요.")
        private String categoryImageData;

        @Builder
        public SaveDTO(String categoryName, String categoryImageData) {
            this.categoryName = categoryName;
            this.categoryImageData = categoryImageData;
        }

        public PhotoServiceCategory toEntity() {
            return PhotoServiceCategory.builder()
                    .categoryName(this.categoryName)
                    .categoryImageData(this.categoryImageData)
                    .build();
        }
    }

    // 카테고리 수정 요청 DTO
    @Data
    @NoArgsConstructor
    public static class UpdateDTO {

        @Size(max = 50, message = "카테고리명은 50자 이하로 입력해주세요.")
        private String categoryName;

        private String categoryImageData;

        @Builder
        public UpdateDTO(String categoryName, String categoryImageData) {
            this.categoryName = categoryName;
            this.categoryImageData = categoryImageData;
        }
    }
}