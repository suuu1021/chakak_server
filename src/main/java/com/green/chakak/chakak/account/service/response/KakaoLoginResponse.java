package com.green.chakak.chakak.account.service.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
// 내부용 DTO() --> Flutter와 우리 서버 전용
public class KakaoLoginResponse {
    private String jwt;
    private String email;
    //private String nickname;
}
