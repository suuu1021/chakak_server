package com.green.chakak.chakak.payment.repository.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentReadyResponse {
    private String tid;
    private String nextRedirectMobileUrl;
    private String nextRedirectPcUrl;
    private String androidAppScheme;
    private String iosAppScheme;
    private String partnerOrderId;              // 주문번호도 함께 전달
    private String itemName;                    // 상품명
    private int price;                    // 결제 금액
}
