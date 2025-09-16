package com.green.chakak.chakak.payment.repository;

import com.green.chakak.chakak.payment.domain.Payment;
import com.green.chakak.chakak.payment.domain.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PaymentJpaRepository extends JpaRepository<Payment,Long> {
    Optional<Payment> findByTid(String tid);
    Optional<Payment> findByPartnerOrderId(String partnerOrderId);
    List<Payment> findByPartnerUserId(String partnerUserId);
    Optional<Payment> findTopByStatusOrderByCreatedAtDesc(PaymentStatus status);

    // 결제 내역 조회용 메서드들

    @Query("SELECT DISTINCT p FROM Payment p " +
            "LEFT JOIN BookingInfo b ON b.payment = p " +
            "WHERE (p.partnerUserId = :userIdStr " +              // 고객으로서 결제한 것
            "    OR (b.photographerProfile IS NOT NULL " +
            "        AND b.photographerProfile.user.id = :userId)) " + // 포토그래퍼로서 수익받은 것
            "AND (:status IS NULL OR p.status = :status) " +
            "AND (:startDate IS NULL OR CAST(p.createdAt AS LocalDate) >= :startDate) " +
            "AND (:endDate IS NULL OR CAST(p.createdAt AS LocalDate) <= :endDate) " +
            "ORDER BY p.createdAt DESC")
    Page<Payment> findRelatedPaymentHistoryByUserId(@Param("userId") Long userId,
                                                    @Param("userIdStr") String userIdStr,
                                                    @Param("status") PaymentStatus status,
                                                    @Param("startDate") LocalDate startDate,
                                                    @Param("endDate") LocalDate endDate,
                                                    Pageable pageable);

    @Query("SELECT p FROM Payment p " +
            "WHERE p.partnerUserId = :userId " +
            "AND (:status IS NULL OR p.status = :status) " +
            "AND (:startDate IS NULL OR CAST(p.createdAt AS LocalDate) >= :startDate) " +
            "AND (:endDate IS NULL OR CAST(p.createdAt AS LocalDate) <= :endDate) " +
            "ORDER BY p.createdAt DESC")
    Page<Payment> findUserPaymentHistory(@Param("userId") String userId,
                                         @Param("status") PaymentStatus status,
                                         @Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate,
                                         Pageable pageable);

    // 포토그래퍼별 수익 내역 (BookingInfo와 조인) - DATE 함수 제거
    @Query("SELECT p FROM Payment p " +
            "JOIN BookingInfo b ON b.payment = p " +
            "WHERE b.photographerProfile.photographerProfileId = :photographerId " +
            "AND p.status = 'APPROVED' " +
            "AND (:startDate IS NULL OR CAST(p.approvedAt AS LocalDate) >= :startDate) " +
            "AND (:endDate IS NULL OR CAST(p.approvedAt AS LocalDate) <= :endDate) " +
            "ORDER BY p.approvedAt DESC")
    Page<Payment> findPhotographerIncomeHistory(@Param("photographerId") Long photographerId,
                                                @Param("startDate") LocalDate startDate,
                                                @Param("endDate") LocalDate endDate,
                                                Pageable pageable);

    // 사용자별 결제 통계 (null 상태 처리 개선)
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.partnerUserId = :userId " +
            "AND (:status IS NULL OR p.status = :status)")
    int countByUserIdAndStatus(@Param("userId") String userId, @Param("status") PaymentStatus status);

    @Query("SELECT COALESCE(SUM(p.totalAmount), 0) FROM Payment p " +
            "WHERE p.partnerUserId = :userId AND p.status = 'APPROVED'")
    long sumTotalAmountByUserId(@Param("userId") String userId);

    // 월별 집계도 CAST 방식으로 변경
    @Query("SELECT COALESCE(SUM(p.totalAmount), 0) FROM Payment p " +
            "WHERE p.partnerUserId = :userId AND p.status = 'APPROVED' " +
            "AND YEAR(p.approvedAt) = :year AND MONTH(p.approvedAt) = :month")
    long sumTotalAmountByUserIdAndMonth(@Param("userId") String userId,
                                        @Param("year") int year,
                                        @Param("month") int month);

    // 포토그래퍼별 수익 통계
    @Query("SELECT COALESCE(SUM(p.totalAmount), 0) FROM Payment p " +
            "JOIN BookingInfo b ON b.payment = p " +
            "WHERE b.photographerProfile.photographerProfileId = :photographerId " +
            "AND p.status = 'APPROVED'")
    long sumTotalIncomeByPhotographerId(@Param("photographerId") Long photographerId);

    @Query("SELECT COALESCE(SUM(p.totalAmount), 0) FROM Payment p " +
            "JOIN BookingInfo b ON b.payment = p " +
            "WHERE b.photographerProfile.photographerProfileId = :photographerId " +
            "AND p.status = 'APPROVED' " +
            "AND YEAR(p.approvedAt) = :year AND MONTH(p.approvedAt) = :month")
    long sumTotalIncomeByPhotographerIdAndMonth(@Param("photographerId") Long photographerId,
                                                @Param("year") int year,
                                                @Param("month") int month);
}