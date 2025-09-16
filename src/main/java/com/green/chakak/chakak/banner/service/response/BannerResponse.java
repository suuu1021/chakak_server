package com.green.chakak.chakak.banner.service.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class BannerResponse {

    // 배너 응답 DTO (Flutter BannerDto와 매칭)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class BannerResponseDto {
        private Long id;

        private String title;

        private String subtitle;

        @JsonProperty("image_url")  // Flutter의 image_url과 매칭
        private String imageUrl;

        @JsonProperty("link_url")   // Flutter의 link_url과 매칭
        private String linkUrl;

        @JsonProperty("is_active")  // Flutter의 is_active와 매칭
        private Boolean isActive;

        @JsonProperty("created_at") // Flutter의 created_at과 매칭
        private String createdAt;   // ISO8601 String 형태로 전송

        @JsonProperty("expires_at") // Flutter의 expires_at과 매칭
        private String expiresAt;   // ISO8601 String 형태로 전송

        // 백엔드 관리용 필드 (Flutter 응답에는 포함되지 않음)
        private Integer displayOrder;
        private String updatedAt;
    }
}
