package com.green.chakak.chakak.account.service.request;

import lombok.Data;

@Data
public class KakaoLoginRequest {
    private String code;
    private String typeCode;
}
