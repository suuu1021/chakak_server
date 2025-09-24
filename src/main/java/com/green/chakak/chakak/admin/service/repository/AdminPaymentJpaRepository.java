package com.green.chakak.chakak.admin.service.repository;

import com.green.chakak.chakak.payment.domain.Payment;
import com.green.chakak.chakak.payment.domain.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface AdminPaymentJpaRepository extends JpaRepository<Payment, Long> {

    /**
     * 관리자 결제 내역 조회 (상태 + 기간 필터)
     */
    @Query("SELECT p FROM Payment p " +
            "WHERE (:status IS NULL OR p.status = :status) " +
            "AND (:startDate IS NULL OR DATE(p.createdAt) >= :startDate) " +
            "AND (:endDate IS NULL OR DATE(p.createdAt) <= :endDate) " +
            "ORDER BY p.createdAt DESC")
    Page<Payment> findAllByStatusAndDateRange(@Param("status") PaymentStatus status,
                                              @Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate,
                                              Pageable pageable);

    /**
     * 관리자 통계 - 전체 건수
     */
    @Query("SELECT COUNT(p) FROM Payment p " +
            "WHERE (:status IS NULL OR p.status = :status)")
    int countAll(@Param("status") PaymentStatus status);

    /**
     * 관리자 통계 - 총 결제 금액
     */
    @Query("SELECT COALESCE(SUM(p.totalAmount), 0) FROM Payment p")
    long sumAllTotalAmount();

    /**
     * 관리자 통계 - 월별 총 결제 금액
     */
    @Query("SELECT COALESCE(SUM(p.totalAmount), 0) FROM Payment p " +
            "WHERE FUNCTION('YEAR', p.createdAt) = :year " +
            "AND FUNCTION('MONTH', p.createdAt) = :month " +
            "AND p.status = 'APPROVED'")
    long sumTotalAmountByMonth(@Param("year") int year, @Param("month") int month);
}
