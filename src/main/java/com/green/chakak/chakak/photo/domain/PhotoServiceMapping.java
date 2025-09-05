package com.green.chakak.chakak.photo.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@NoArgsConstructor
@Data
@Entity
@Table(name = "photo_service_mapping")
public class PhotoServiceMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mappingId;

    @JoinColumn(nullable = false, name = "service_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private PhotoServiceInfo photoServiceInfo;

    @JoinColumn(nullable = false, name = "category_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private PhotoServiceCategory photoServiceCategory;

    @CreationTimestamp
    private Timestamp createdAt;

    @Builder
    public PhotoServiceMapping(Long mappingId, PhotoServiceInfo photoServiceInfo, PhotoServiceCategory photoServiceCategory, Timestamp createdAt) {
        this.mappingId = mappingId;
        this.photoServiceInfo = photoServiceInfo;
        this.photoServiceCategory = photoServiceCategory;
        this.createdAt = createdAt;
    }
}