package com.green.chakak.chakak.payment.repository.response;

import lombok.Data;

@Data
public class KakaoPaymentReadyResponse {
    private String tid;                          // 결제 고유번호
    private String next_redirect_mobile_url;     // 모바일 결제 페이지 URL
    private String next_redirect_pc_url;         // PC 결제 페이지 URL
    private String android_app_scheme;           // 안드로이드 앱 스킴
    private String ios_app_scheme;              // iOS 앱 스킴
    private String created_at;                  // 결제 준비 요청 시각
}

