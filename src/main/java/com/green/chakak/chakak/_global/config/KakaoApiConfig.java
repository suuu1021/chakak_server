package com.green.chakak.chakak._global.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration // 스프링 컨테이너가 실행될 때 이 클래스를 빈(Bean)으로 등록
/**
 * application.yml(또는 application.properties)에 적힌 설정 값을
 * 자바 객체로 바인딩해주는 역활을 합니다.
 */
@ConfigurationProperties(prefix = "kakao.api")
@Data
public class KakaoApiConfig {

    private String clientId;
    private String redirectUri;
    private String tokenUri;
    private String userInfoUri;

}
