package com.green.chakak.chakak.photo.domain;

import com.green.chakak.chakak.photo.service.request.PhotoServiceInfoRequest;
import com.green.chakak.chakak.photographer.domain.PhotographerProfile;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.w3c.dom.Text;

import java.sql.Timestamp;

@NoArgsConstructor
@Data
@Entity
@Table(name = "photo_service_info")
public class PhotoServiceInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long serviceId;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private PhotographerProfile photographerProfile;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false)
    @Lob
    private String description;

    @Lob
    private String imageData;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    @Builder
    public PhotoServiceInfo(Long serviceId, PhotographerProfile photographerProfile, String title, String description, String imageData, Timestamp createdAt, Timestamp updatedAt) {
        this.serviceId = serviceId;
        this.photographerProfile = photographerProfile;
        this.title = title;
        this.description = description;
        this.imageData = imageData;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void updateFromDto(PhotoServiceInfoRequest.UpdateDTO dto) {
        if (dto.getTitle() != null) {
            this.title = dto.getTitle();
        }
        if (dto.getDescription() != null) {
            this.description = dto.getDescription();
        }
        if (dto.getImageData() != null) {
            this.imageData = dto.getImageData();
        }
    }
}