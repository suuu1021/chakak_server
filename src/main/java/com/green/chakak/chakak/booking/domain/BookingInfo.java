package com.green.chakak.chakak.booking.domain;

import com.green.chakak.chakak.account.domain.UserProfile;
import com.green.chakak.chakak.payment.domain.Payment;
import com.green.chakak.chakak.photo.domain.PhotoServiceInfo;
import com.green.chakak.chakak.photo.domain.PriceInfo;
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
    private PhotographerProfile photographerProfile; // 포토그래퍼 프로필 ID(FK)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_service_info_id", nullable = false)
    private PhotoServiceInfo photoServiceInfo; // 포토 서비스 ID(FK)

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "price_info_id", nullable = false)
    private PriceInfo priceInfo; // 서비스 가격정보 ID (FK)

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "payment_id") // nullable = true (기본값)
    private Payment payment; // 결제 정보 ID (FK)

    @Column(nullable = false)
    private LocalDate bookingDate; // 촬영 희망일

    @Column(nullable = false)
    private LocalTime bookingTime; // 촬영 희망시간

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private BookingStatus status; // 예약 상태

    @CreationTimestamp
    private Timestamp createdAt; // 생성 일시

    @UpdateTimestamp
    private Timestamp updatedAt; // 수정 일시


    @Builder
    public BookingInfo(Long bookingInfoId, UserProfile userProfile, PhotographerProfile photographerProfile, PhotoServiceInfo photoServiceInfo, PriceInfo priceInfo, Payment payment, LocalDate bookingDate, LocalTime bookingTime, BookingStatus status, Timestamp createdAt, Timestamp updatedAt) {
        this.bookingInfoId = bookingInfoId;
        this.userProfile = userProfile;
        this.photographerProfile = photographerProfile;
        this.photoServiceInfo = photoServiceInfo;
        this.priceInfo = priceInfo;
        this.payment = payment;
        this.bookingDate = bookingDate;
        this.bookingTime = bookingTime;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}