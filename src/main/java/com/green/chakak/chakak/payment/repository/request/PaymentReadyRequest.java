package com.green.chakak.chakak.payment.repository.request;

import lombok.Builder;
import lombok.Data;

// 프론트에서 받는 결제 요청 DTO
@Data
@Builder
public class PaymentReadyRequest {
    private Long bookingInfoId; // 예약 ID
}