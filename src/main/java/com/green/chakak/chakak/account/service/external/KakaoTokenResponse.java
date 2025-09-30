package com.green.chakak.chakak.account.service.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 역할: Flutter에서 받은 인가 코드(code)를 카카오에 전달하면 , 카카오가 내려주는
 * AccessToken/ RefreshToken
 * 범위 : 카카오 API 호출할 때만 사용 (예 : 사용자 정보 조회)
 */
@Data
public class KakaoTokenResponse {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("expires_in")
    private int expiresIn;

    @JsonProperty("token_type")
    private String tokenType;

    private String scope;

    @JsonProperty("refresh_token_expires_in")
    private int refreshTokenExpiresIn;
}
