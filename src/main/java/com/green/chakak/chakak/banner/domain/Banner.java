package com.green.chakak.chakak.banner.domain;

import com.green.chakak.chakak.banner.service.request.BannerRequest;
import com.green.chakak.chakak.banner.service.response.BannerResponse;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
@Entity
@Table(name = "banner")
public class Banner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "banner_id")
    private Long bannerId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String subtitle;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String imageUrl;

    @Column(length = 500)
    private String linkUrl;

    @Column(nullable = false)
    private boolean isActive = true;

    @Column(nullable = false)
    private Integer displayOrder = 0;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Builder
    public Banner(Long bannerId, String title, String subtitle, String imageUrl,
                  String linkUrl, Boolean isActive, Integer displayOrder,
                  LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime expiresAt) {
        this.bannerId = bannerId;
        this.title = title;
        this.subtitle = subtitle;
        this.imageUrl = imageUrl;
        this.linkUrl = linkUrl;
        this.isActive = isActive != null ? isActive : true;
        this.displayOrder = displayOrder != null ? displayOrder : 0;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.expiresAt = expiresAt;
    }



    // 비즈니스 로직 메서드
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isDisplayable() {
        return isActive && !isExpired();
    }

    public void toggleActive() {
        this.isActive = !this.isActive;
    }

    public void updateFromDto(BannerRequest.UpdateDTO dto) {
        if (dto.getTitle() != null) this.title = dto.getTitle();
        if (dto.getSubtitle() != null) this.subtitle = dto.getSubtitle();
        if (dto.getImageData() != null) this.imageUrl = dto.getImageData();
        if (dto.getLinkUrl() != null) this.linkUrl = dto.getLinkUrl();
        if (dto.getIsActive() != null) this.isActive = dto.getIsActive();
        if (dto.getDisplayOrder() != null) this.displayOrder = dto.getDisplayOrder();
        if (dto.getExpiresAt() != null) this.expiresAt = dto.getExpiresAt();
    }
}
