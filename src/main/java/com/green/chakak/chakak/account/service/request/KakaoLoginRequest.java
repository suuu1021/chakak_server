package com.green.chakak.chakak.account.service.request;

import lombok.Data;

@Data
// 내부용 DTO() --> Flutter와 우리 서버 전용
public class KakaoLoginRequest {
    private String code; // Flutter에서 전달받은 인가 코드
    private String typeCode; // "user" or "photographer"
}
