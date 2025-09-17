package com.green.chakak.chakak.banner.service.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.green.chakak.chakak.banner.domain.Banner;
import com.green.chakak.chakak.banner.service.response.BannerResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BannerRequest {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateDTO {
        @NotBlank(message = "제목을 입력해주세요")
        private String title;
        private String subtitle;

        @NotBlank(message = "이미지 URL을 입력해주세요")
        @JsonProperty("image_url")
        private String imageUrl; // base64 또는 URL 형태로 받음

        @NotBlank(message = "링크 URL을 입력해주세요")
        @JsonProperty("link_url")
        private String linkUrl;

        @NotNull(message = "표시 순서를 입력해주세요")
        private Integer displayOrder;

        @JsonProperty("expires_at")
        private LocalDateTime expiresAt; // ISO8601 String 형태로 받음


        public Banner CreateDtoToEntity() {




            return Banner.builder()
                    .title(this.title)
                    .subtitle(this.subtitle)
                    .imageUrl(this.imageUrl)
                    .linkUrl(this.linkUrl)
                    .displayOrder(this.displayOrder)
                    .expiresAt(expiresAt)
                    .build();
        }

    }




    // 배너 목록 조회용 간단한 DTO (관리자용)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ListResponseDto {
        private Long id;
        private String title;
        private String subtitle;

        @NotBlank(message = "링크 URL을 입력해주세요")
        @JsonProperty("link_url")
        private String linkUrl;

        @JsonProperty("is_active")
        private Boolean isActive;

        @NotBlank(message = "표시 순서를 입력해주세요")
        private Integer displayOrder;

        @JsonProperty("created_at")
        private String createdAt;

        @JsonProperty("expires_at")
        private String expiresAt;

        // 이미지는 용량 때문에 목록에서는 제외
        private boolean hasImage; // 이미지 존재 여부만 표시
    }



    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateDTO {

        @NotBlank(message = "제목을 입력해주세요")
        private String title;
        private String subtitle;

        @NotBlank(message = "이미지 URL을 입력해주세요")
        @JsonProperty("image_url")
        private String imageUrl;

        @JsonProperty("link_url")
        private String linkUrl;

        @JsonProperty("is_active")
        private Boolean isActive;

        private Integer displayOrder;

        @JsonProperty("expires_at")
        private LocalDateTime expiresAt; // 바로 LocalDateTime으로 받기

        public Banner UpdateDtoToEntity() {




            return Banner.builder()
                    .title(this.title)
                    .subtitle(this.subtitle)
                    .imageUrl(this.imageUrl)
                    .linkUrl(this.linkUrl)
                    .isActive(this.isActive)
                    .displayOrder(this.displayOrder)
                    .expiresAt(expiresAt)
                    .build();
        }

    }


}