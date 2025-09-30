package com.green.chakak.chakak.account.service.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KakaoLoginResponse {
    private String jwt;
    private String email;
}
