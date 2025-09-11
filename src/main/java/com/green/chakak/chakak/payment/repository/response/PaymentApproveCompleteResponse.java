package com.green.chakak.chakak.payment.repository.response;

import lombok.Builder;
import lombok.Data;

// 프론트로 보낼 결제 승인 완료 응답 DTO
@Data
@Builder
public class PaymentApproveCompleteResponse {
    private String aid;                     // 결제 승인 번호
    private String tid;                     // 결제 고유 번호
    private String partnerOrderId;          // 주문번호
    private String itemName;                // 상품명
    private int totalAmount;                // 총 결제금액
    private String paymentMethodType;       // 결제 수단
    private String approvedAt;              // 결제 승인 시각
    private Long bookingInfoId;             // 예약 ID
}
