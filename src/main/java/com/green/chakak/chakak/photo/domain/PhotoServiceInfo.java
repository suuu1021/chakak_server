package com.green.chakak.chakak.photo.domain;

import com.green.chakak.chakak.photographer.domain.PhotographerProfile;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
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
    private Text description;

    @Column(length = 500)
    private String imageUrl;

    @CreationTimestamp
    private Timestamp createdAt;

    private Timestamp updatedAt;

    @Builder
    public PhotoServiceInfo(Long serviceId, PhotographerProfile photographerProfile, String title, Text description, String imageUrl, Timestamp createdAt, Timestamp updatedAt) {
        this.serviceId = serviceId;
        this.photographerProfile = photographerProfile;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}