package com.green.chakak.chakak.payment.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "payment")
@Data
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId; // 결제 ID (PK)

    @Column(nullable = false, unique = true, length = 100)
    private String tid; // 카카오페이 거래 ID

    @Column(nullable = false, length = 100)
    private String partnerOrderId; // 가맹점 주문번호

    @Column(nullable = false, length = 100)
    private String partnerUserId; // 가맹점 회원 ID

    @Column(nullable = false, length = 255)
    private String itemName; // 상품명

    @Column(nullable = false)
    private int totalAmount; // 총 결제금액

    @Column(nullable = false)
    private int vatAmount; // 부가세

    @Column(nullable = false)
    private int taxFreeAmount; // 비과세 금액

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status; // 결제 상태

    @Column(length = 50)
    private String paymentMethodType; // 결제 수단 (MONEY, CARD 등)

    @Column(length = 100)
    private String aid; // 결제 승인 번호 (결제 완료 시 저장)

    @CreationTimestamp
    private Timestamp createdAt; // 생성 일시

    @Column
    private Timestamp approvedAt; // 결제 승인 시간

    @Builder
    public Payment(Long paymentId, String tid, String partnerOrderId,
                   String partnerUserId, String itemName, int totalAmount,
                   int vatAmount, int taxFreeAmount, PaymentStatus status,
                   String paymentMethodType, String aid, Timestamp createdAt,
                   Timestamp approvedAt) {
        this.paymentId = paymentId;
        this.tid = tid;
        this.partnerOrderId = partnerOrderId;
        this.partnerUserId = partnerUserId;
        this.itemName = itemName;
        this.totalAmount = totalAmount;
        this.vatAmount = vatAmount;
        this.taxFreeAmount = taxFreeAmount;
        this.status = status;
        this.paymentMethodType = paymentMethodType;
        this.aid = aid;
        this.createdAt = createdAt;
        this.approvedAt = approvedAt;
    }
}