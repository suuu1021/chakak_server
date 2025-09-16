package com.green.chakak.chakak.banner.service.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class BannerRequest {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class BannerCreateDto {
        private String title;
        private String subtitle;

        @JsonProperty("image_url")
        private String imageUrl; // base64 또는 URL 형태로 받음

        @JsonProperty("link_url")
        private String linkUrl;

        private Integer displayOrder;

        @JsonProperty("expires_at")
        private String expiresAt; // ISO8601 String 형태로 받음
    }

    // 배너 수정 요청 DTO
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class BannerUpdateDto {
        private String title;
        private String subtitle;

        @JsonProperty("image_url")
        private String imageUrl;

        @JsonProperty("link_url")
        private String linkUrl;

        @JsonProperty("is_active")
        private Boolean isActive;

        private Integer displayOrder;

        @JsonProperty("expires_at")
        private String expiresAt;
    }

    // 배너 목록 조회용 간단한 DTO (관리자용)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class BannerListResponseDto {
        private Long id;
        private String title;
        private String subtitle;

        @JsonProperty("link_url")
        private String linkUrl;

        @JsonProperty("is_active")
        private Boolean isActive;

        private Integer displayOrder;

        @JsonProperty("created_at")
        private String createdAt;

        @JsonProperty("expires_at")
        private String expiresAt;

        // 이미지는 용량 때문에 목록에서는 제외
        private boolean hasImage; // 이미지 존재 여부만 표시
    }


}
