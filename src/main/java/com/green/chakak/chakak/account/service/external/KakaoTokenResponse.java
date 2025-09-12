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

    // Jackson 라이브러리에서 제공하는 어노테이션이다.
    // JSON의 필드 이름과 자바 클래스의 필드 이름이 다를 때, 매핑을 맞춰주기 위해 사용
    // @Column 가 비슷해 보이지만 전혀 다른 계층에서 동작
    // @Column 역할 : 자바 엔티티 필드 <-> DB 컬럼 매핑 /동작 계층 : DB
    // @JsonProperty 역활 : 자바 필드 <-> JSON 키 매핑 / 동작 계층 : JSON(HTTP 요청/응답)
    // 토큰 받기 용 DTO(외부용 DTO) --> 카카오 API 전용
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
