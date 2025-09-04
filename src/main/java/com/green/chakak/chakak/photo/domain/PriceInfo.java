package com.green.chakak.chakak.photo.domain;

import com.green.chakak.chakak.photo.service.PhotoService;
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
@Table(name = "price_info")
public class PriceInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long priceInfoId;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private PhotoServiceInfo photoServiceInfo;

    @Column(nullable = false, length = 30)
    private String title;

    @Column(nullable = false)
    private int participantCount;

    @Column(nullable = false)
    private int shootingDuration;

    @Column(nullable = false)
    private int outfitChanges;

    @Column(nullable = false)
    @Lob
    private String specialEquipment;

    @Column(nullable = false)
    private boolean isMakeupService;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    @Builder
    public PriceInfo(Long priceInfoId, PhotoServiceInfo photoServiceInfo, String title, int participantCount, int shootingDuration, int outfitChanges, String specialEquipment, boolean isMakeupService, Timestamp createdAt, Timestamp updatedAt) {
        this.priceInfoId = priceInfoId;
        this.photoServiceInfo = photoServiceInfo;
        this.title = title;
        this.participantCount = participantCount;
        this.shootingDuration = shootingDuration;
        this.outfitChanges = outfitChanges;
        this.specialEquipment = specialEquipment;
        this.isMakeupService = isMakeupService;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}