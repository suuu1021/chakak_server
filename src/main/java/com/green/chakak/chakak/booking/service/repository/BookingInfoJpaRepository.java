package com.green.chakak.chakak.booking.service.repository;

import com.green.chakak.chakak.booking.domain.BookingInfo;
import com.green.chakak.chakak.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookingInfoJpaRepository extends JpaRepository<BookingInfo, Long> {

    @Query("SELECT b FROM BookingInfo b " +
            "JOIN FETCH b.userProfile up " +
            "JOIN FETCH up.user u " +
            "JOIN FETCH b.photographerProfile pp " +
            "WHERE u.userId = :userId")
    List<BookingInfo> findByUserId(@Param("userId") Long userId);

    @Query("SELECT b FROM BookingInfo b " +
            "JOIN FETCH b.photographerProfile pp " +
            "JOIN FETCH b.userProfile up " +
            "WHERE pp.user.userId = :photographerUserId")
    List<BookingInfo> findByPhotographerId(@Param("photographerUserId") Long photographerUserId);

    Optional<BookingInfo> findByPayment(Payment payment);

    // 결제 완료된 예약만 조회
    @Query("SELECT b FROM BookingInfo b " +
            "JOIN FETCH b.photographerProfile pp " +
            "JOIN FETCH b.photoServiceInfo psi " +
            "JOIN FETCH b.priceInfo pri " +
            "WHERE b.userProfile.user.userId = :userId AND b.payment IS NOT NULL " +
            "ORDER BY b.createdAt DESC")
    List<BookingInfo> findPaidBookingsByUserId(@Param("userId") Long userId);

    @Query("SELECT b FROM BookingInfo b " +
            "JOIN FETCH b.userProfile up " +
            "JOIN FETCH b.photoServiceInfo psi " +
            "JOIN FETCH b.priceInfo pri " +
            "WHERE b.photographerProfile.user.userId = :userId AND b.payment IS NOT NULL " +
            "ORDER BY b.createdAt DESC")
    List<BookingInfo> findPaidBookingsByPhotographerId(@Param("userId") Long userId);

}
