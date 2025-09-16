package com.green.chakak.chakak.payment.repository.response;

import com.green.chakak.chakak.payment.domain.Payment;
import com.green.chakak.chakak.payment.domain.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class PaymentListResponse {
    private Long paymentId;              // 결제 ID
    private String tid;                  // 카카오페이 거래 ID
    private String partnerOrderId;       // 가맹점 주문번호
    private String itemName;             // 상품명
    private int totalAmount;             // 총 결제금액
    private PaymentStatus status;        // 결제 상태
    private String paymentMethodType;    // 결제 수단
    private String aid;                  // 결제 승인 번호
    private Timestamp createdAt;         // 생성 일시
    private Timestamp approvedAt;        // 결제 승인 시간

    // 추가 정보 (BookingInfo와 연관된 정보)
    private Long bookingInfoId;          // 예약 ID
    private String photographerName;     // 포토그래퍼 이름
    private String serviceTitle;         // 서비스 제목

    // Payment 엔티티로부터 PaymentListResponse 생성하는 정적 메서드
    public static PaymentListResponse of(Payment payment) {
        return PaymentListResponse.builder()
                .paymentId(payment.getPaymentId())
                .tid(payment.getTid())
                .partnerOrderId(payment.getPartnerOrderId())
                .itemName(payment.getItemName())
                .totalAmount(payment.getTotalAmount())
                .status(payment.getStatus())
                .paymentMethodType(payment.getPaymentMethodType())
                .aid(payment.getAid())
                .createdAt(payment.getCreatedAt())
                .approvedAt(payment.getApprovedAt())
                .build();
    }

    // BookingInfo 정보를 포함한 PaymentListResponse 생성 (추후 BookingInfo 조인 시 사용)
    public static PaymentListResponse of(Payment payment, Long bookingInfoId,
                                         String photographerName, String serviceTitle) {
        PaymentListResponse response = of(payment);
        response.setBookingInfoId(bookingInfoId);
        response.setPhotographerName(photographerName);
        response.setServiceTitle(serviceTitle);
        return response;
    }

    // 결제 상태에 따른 한글 설명 반환
    public String getStatusDescription() {
        return status != null ? status.getDescription() : "";
    }

    // 결제 승인 여부 확인
    public boolean isApproved() {
        return PaymentStatus.APPROVED.equals(status);
    }

    // 결제 완료 여부 확인 (승인된 결제만 완료로 간주)
    public boolean isCompleted() {
        return isApproved() && aid != null && !aid.trim().isEmpty();
    }
}
