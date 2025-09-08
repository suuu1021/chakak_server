package com.green.chakak.chakak.photo.service.request;

import com.green.chakak.chakak.photo.domain.PhotoServiceCategory;
import com.green.chakak.chakak.photo.domain.PhotoServiceInfo;
import com.green.chakak.chakak.photo.domain.PhotoServiceMapping;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class PhotoMappingRequest {

    // 매핑 생성 요청 DTO
    @Data
    @NoArgsConstructor
    public static class SaveDTO {

        @NotNull(message = "서비스 ID를 입력해주세요.")
        private Long serviceId;

        @NotEmpty(message = "카테고리 ID를 최소 하나는 입력해주세요.")
        private List<Long> categoryIdList; // List로 변경!

        @Builder
        public SaveDTO(Long serviceId, List<Long> categoryIdList) {
            this.serviceId = serviceId;
            this.categoryIdList = categoryIdList;
        }

        public PhotoServiceMapping toEntity(PhotoServiceInfo photoServiceInfo, PhotoServiceCategory photoServiceCategory) {
            return PhotoServiceMapping.builder()
                    .photoServiceInfo(photoServiceInfo)
                    .photoServiceCategory(photoServiceCategory)
                    .build();
        }
    }
}