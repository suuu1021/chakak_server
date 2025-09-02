package com.green.chakak.chakak.booking.booking_info;

import com.green.chakak.chakak.account.user_profile.UserProfile;
import com.green.chakak.chakak.photo.domain.PhotoServiceInfo;
import com.green.chakak.chakak.photographer.domain.PhotographerCategory;
import com.green.chakak.chakak.photographer.domain.PhotographerProfile;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "booking_info")
@Data
@NoArgsConstructor
public class BookingInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingInfoId; // 예약 ID (PK)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_profile_id", nullable = false)
    private UserProfile userProfile; // 예약자 ID (FK)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photographer_profile_id", nullable = false)
    private PhotographerProfile photographerProfile; // 포토그래퍼 ID(FK)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photographer_category_id", nullable = false)
    private PhotographerCategory photographerCategory;//  촬영 카테고리 ID(FK)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_service_info_id", nullable = false)
    private PhotoServiceInfo photoServiceInfo; // 포토그래퍼 서비스 ID(FK)

    @Column(nullable = false)
    private LocalDate bookingDate; // 촬영 희망일

    @Column(nullable = false)
    private LocalTime bookingTime; // 촬영 희망시간

    @Column(length = 200, nullable = false)
    private String location; // 촬영 장소

    @Column
    private int budget; // 예산

    @Column(length = 500)
    private String specialRequest; // 특별 요청사항

    @Column(length = 20, nullable = false)
    private String status; // 예약 상태

    @Column(nullable = false)
    private int participantCount; // 참여 인원수

    @Column(nullable = false)
    private int shootingDuration; // 촬영 시간(분)

    @CreationTimestamp
    private Timestamp createdAt; // 생성 일시

    @UpdateTimestamp
    private Timestamp updatedAt; // 수정 일시


    @Builder
    public BookingInfo(Long bookingInfoId, UserProfile userProfile, PhotographerProfile photographerProfile, PhotographerCategory photographerCategory, PhotoServiceInfo photoServiceInfo, LocalDate bookingDate, LocalTime bookingTime, String location, int budget, String specialRequest, String status, int participantCount, int shootingDuration, Timestamp createdAt, Timestamp updatedAt) {
        this.bookingInfoId = bookingInfoId;
        this.userProfile = userProfile;
        this.photographerProfile = photographerProfile;
        this.photographerCategory = photographerCategory;
        this.photoServiceInfo = photoServiceInfo;
        this.bookingDate = bookingDate;
        this.bookingTime = bookingTime;
        this.location = location;
        this.budget = budget;
        this.specialRequest = specialRequest;
        this.status = status;
        this.participantCount = participantCount;
        this.shootingDuration = shootingDuration;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}