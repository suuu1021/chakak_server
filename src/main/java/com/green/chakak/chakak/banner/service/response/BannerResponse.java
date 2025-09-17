package com.green.chakak.chakak.banner.service.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.green.chakak.chakak.banner.domain.Banner;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class BannerResponse {

    // 배너 목록 응답 DTO
    @Data
    public static class BannerListDTO {
        private Long id;
        private String title;
        private String subtitle;

        @JsonProperty("image_url")
        private String imageUrl;

        @JsonProperty("link_url")
        private String linkUrl;

        @JsonProperty("is_active")
        private Boolean isActive;

        @JsonProperty("created_at")
        private LocalDateTime createdAt;

        @JsonProperty("expires_at")
        private LocalDateTime expiresAt;

        private Integer displayOrder;
        private boolean hasImage;

        @Builder
        public BannerListDTO(Banner banner) {
            this.id = banner.getBannerId();
            this.title = banner.getTitle();
            this.subtitle = banner.getSubtitle();
            this.imageUrl = banner.getImageUrl();
            this.linkUrl = banner.getLinkUrl();
            this.isActive = banner.isActive();
            this.createdAt = banner.getCreatedAt();
            this.expiresAt = banner.getExpiresAt();
            this.displayOrder = banner.getDisplayOrder();
            this.hasImage = banner.getImageUrl() != null && !banner.getImageUrl().isEmpty();
        }
    }

    // 배너 상세 응답 DTO
    @Data
    public static class BannerDetailDTO {
        private Long id;
        private String title;
        private String subtitle;

        @JsonProperty("image_url")
        private String imageUrl;

        @JsonProperty("link_url")
        private String linkUrl;

        @JsonProperty("is_active")
        private boolean isActive;

        @JsonProperty("created_at")
        private LocalDateTime createdAt;

        @JsonProperty("updated_at")
        private LocalDateTime updatedAt;

        @JsonProperty("expires_at")
        private LocalDateTime expiresAt;

        private Integer displayOrder;
        private boolean canEdit = false;
        private boolean isExpired;
        private boolean isDisplayable;

        @Builder
        public BannerDetailDTO(Banner banner) {
            this.id = banner.getBannerId();
            this.title = banner.getTitle();
            this.subtitle = banner.getSubtitle();
            this.imageUrl = banner.getImageUrl();
            this.linkUrl = banner.getLinkUrl();
            this.isActive = banner.isActive();
            this.createdAt = banner.getCreatedAt();
            this.updatedAt = banner.getUpdatedAt();
            this.expiresAt = banner.getExpiresAt();
            this.displayOrder = banner.getDisplayOrder();
            this.isExpired = banner.isExpired();
            this.isDisplayable = banner.isDisplayable();
            this.canEdit = false;
        }

    }
}
