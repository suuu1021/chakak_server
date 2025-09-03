package com.green.chakak.chakak.booking.domain;

import com.green.chakak.chakak.account.user_profile.UserProfile;
import com.green.chakak.chakak.booking.booking_info.BookingInfo;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Getter
@Entity
@Table(name = "booking_cancel_info")
public class BookingCancelInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long cancellationId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_info_id")
    private BookingInfo bookingInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_profile_id")
    private UserProfile userProfile;

    private String cancelReasonText;

    private int penaltyAmount;

    private int refundAmount;

    @CreationTimestamp
    private Timestamp createdAt;

}