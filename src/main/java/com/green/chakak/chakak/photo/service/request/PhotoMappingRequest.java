package com.green.chakak.chakak.photo.service.request;

import com.green.chakak.chakak.photo.domain.PhotoServiceCategory;
import com.green.chakak.chakak.photo.domain.PhotoServiceInfo;
import com.green.chakak.chakak.photo.domain.PhotoServiceMapping;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class PhotoMappingRequest {

    // 매핑 생성 요청 DTO
    @Data
    @NoArgsConstructor
    public static class SaveDTO {

        @NotNull(message = "서비스 ID를 입력해주세요.")
        private Long serviceId;

        @NotNull(message = "카테고리 ID를 입력해주세요.")
        private Long categoryId;

        @Builder
        public SaveDTO(Long serviceId, Long categoryId) {
            this.serviceId = serviceId;
            this.categoryId = categoryId;
        }

        public PhotoServiceMapping toEntity(PhotoServiceInfo photoServiceInfo, PhotoServiceCategory photoServiceCategory) {
            return PhotoServiceMapping.builder()
                    .photoServiceInfo(photoServiceInfo)
                    .photoServiceCategory(photoServiceCategory)
                    .build();
        }
    }
}