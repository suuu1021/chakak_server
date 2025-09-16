package com.green.chakak.chakak.banner.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "banner")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Banner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title; // 배너 제목

    @Column(columnDefinition = "TEXT")
    private String subtitle; // 배너 부제목 (Flutter의 subtitle과 매칭)

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String imageUrl; // 이미지 URL 또는 base64 데이터

    @Column(length = 500)
    private String linkUrl; // 배너 클릭시 이동할 URL

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true; // 활성화 여부

    @Column(nullable = false)
    @Builder.Default
    private Integer displayOrder = 0; // 표시 순서 (낮을수록 먼저 표시)

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime expiresAt; // 배너 만료일시 (Flutter의 expiresAt과 매칭)


    // DTO로부터 업데이트하는 메서드
//    public void updateFromDTO(BannerUpdateDTO dto) {
//        if (dto.getTitle() != null) {
//            this.title = dto.getTitle();
//        }
//        if (dto.getSubtitle() != null) {
//            this.subtitle = dto.getSubtitle();
//        }
//        if (dto.getImageUrl() != null) {
//            this.imageUrl = dto.getImageUrl();
//        }
//        if (dto.getLinkUrl() != null) {
//            this.linkUrl = dto.getLinkUrl();
//        }
//        if (dto.getIsActive() != null) {
//            this.isActive = dto.getIsActive();
//        }
//        if (dto.getDisplayOrder() != null) {
//            this.displayOrder = dto.getDisplayOrder();
//        }
//        if (dto.getExpiresAt() != null) {
//            this.expiresAt = dto.getExpiresAt();
//        }
//    }



    public void toggleActive() {
        this.isActive = !this.isActive;
    }

    // 배너가 만료되었는지 확인하는 메서드
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }

    // 활성화되어 있고 만료되지 않은 배너인지 확인
    public boolean isDisplayable() {
        return isActive && !isExpired();
    }
}