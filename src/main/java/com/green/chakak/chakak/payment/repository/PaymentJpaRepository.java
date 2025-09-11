package com.green.chakak.chakak.payment.repository;

import com.green.chakak.chakak.payment.domain.Payment;
import com.green.chakak.chakak.payment.domain.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentJpaRepository extends JpaRepository<Payment,Long> {
    // 기존 메서드들...
    Optional<Payment> findByTid(String tid);
    Optional<Payment> findByPartnerOrderId(String partnerOrderId);
    List<Payment> findByPartnerUserId(String partnerUserId);

    // 추가할 메서드
    Optional<Payment> findTopByStatusOrderByCreatedAtDesc(PaymentStatus status);
}