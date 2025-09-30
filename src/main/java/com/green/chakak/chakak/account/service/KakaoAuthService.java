package com.green.chakak.chakak.account.service;

import com.green.chakak.chakak._global.config.KakaoApiConfig;
import com.green.chakak.chakak._global.errors.exception.Exception500;
import com.green.chakak.chakak._global.utils.JwtUtil;
import com.green.chakak.chakak.account.domain.LoginUser;
import com.green.chakak.chakak.account.domain.User;
import com.green.chakak.chakak.account.domain.UserType;
import com.green.chakak.chakak.account.service.external.KakaoTokenResponse;
import com.green.chakak.chakak.account.service.external.KakaoUserInfo;
import com.green.chakak.chakak.account.service.repository.UserJpaRepository;
import com.green.chakak.chakak.account.service.repository.UserTypeRepository;
import com.green.chakak.chakak.account.service.request.KakaoLoginRequest;
import com.green.chakak.chakak.account.service.response.KakaoLoginResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoAuthService {
    private final KakaoApiConfig kakaoApiConfig;
    private final UserJpaRepository userJpaRepository;
    private final UserTypeRepository userTypeRepository;
    private final RestTemplate restTemplate;

    @Transactional
    public KakaoLoginResponse kakaoLogin (KakaoLoginRequest request) {

        // 1. 인가 코드로 AccessToken 요청
        KakaoTokenResponse tokenResponse = getAccessToken(request.getCode());

        // 2. AccessToken으로 카카오 유저 정보 요청
        KakaoUserInfo kakaoUserInfo = getUserInfo(tokenResponse.getAccessToken());

        String email = null;
        //String nickname = null;

        if (kakaoUserInfo.getKakaoAccount() != null){
            email = kakaoUserInfo.getKakaoAccount().getEmail();
        }

        // 이메일이 아예 없으면 providerId 기반으로 대체
        if (!StringUtils.hasText(email)) {
            email = kakaoUserInfo.getId() + "@kakao.com";
        }

        String finalEmail = email;

        // 3. typeCode 분기
        String typeCode = request.getTypeCode();
        if (!StringUtils.hasText(typeCode)){
            typeCode = "user"; // 기본값은 user
        }

         switch (typeCode){
             case "user":
                 log.info("일반 사용자 로그인");
                 break;
             case "photographer":
                 log.info("포토그래퍼 로그인");
                 break;
             default:
                 throw new Exception500("허용되지 않는 userType입니다 (user/photographer)만 가능합니다.");
         }

        String finalTypeCode = typeCode;

        // 회원 가입 및 자동 로그인을 담당하고 있다.
        User user = userJpaRepository.findByEmail(email)
                .orElseGet(() -> {
                    UserType userType = userTypeRepository.findByTypeCode(finalTypeCode)
                            .orElseThrow(() -> new Exception500("기본 USER 타입이 없습니다."));
                    return userJpaRepository.save(
                            User.fromKakao(
                                    finalEmail,
                                    kakaoUserInfo.getId().toString(),
                                    userType
                            )
                    );
                });
        // 4. JWT 발급
        LoginUser loginUser = LoginUser.fromEntity(user); // 기존 로직에 맞춰서

        String jwt = JwtUtil.create(loginUser);


        return new KakaoLoginResponse(jwt, user.getEmail());

    }

    private KakaoTokenResponse getAccessToken(String code){

        String tokenUri = kakaoApiConfig.getTokenUri();
        if(!StringUtils.hasText(tokenUri)){
            log.error("카카오 API 설정 오류: 'kakao.api.token-uri'가 application.yml에 설정되지 않았습니다.");
            throw new Exception500("카카오 API 설정(token-uri)이 올바르지 않습니다.");

        }

        String url = UriComponentsBuilder.fromUriString(tokenUri)
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", kakaoApiConfig.getClientId())
                .queryParam("redirect_uri", kakaoApiConfig.getRedirectUri())
                .queryParam("code", code)
                .queryParam("prompt","consent")
                .toUriString();


        try {
            return restTemplate.postForObject(url, null, KakaoTokenResponse.class);
        } catch (HttpClientErrorException e) {
            log.error("카카오 AccessToken 요청 실패: URL={}, 응답={}", url, e.getResponseBodyAsString());
            throw new Exception500("카카오 인증 토큰을 받아오는 중 오류가 발생했습니다." + e.getResponseBodyAsString());
        }
    }

    private KakaoUserInfo getUserInfo(String accessToken){

        String userInfoUri = kakaoApiConfig.getUserInfoUri();
        if(!StringUtils.hasText(userInfoUri)) {
            log.error("카카오 API 설정 오류: 'kakao.api.user-info-uri'가 application.yml에 설정되지 않았습니다");
            throw new Exception500("카카오 API 설정(user-info-uri)이 올바르지 않습니다.");
        }

        String url = UriComponentsBuilder.fromUriString(userInfoUri)
                .queryParam("access_token", accessToken)
                .toUriString();

        try {
           return restTemplate.getForObject(url, KakaoUserInfo.class);
        } catch (HttpClientErrorException e) {
            log.error("카카오 사용자 정보 요청 실패: URL={}, 응답={}",url, e.getResponseBodyAsString());
            throw new Exception500("카카오 사용자 정보를 가져오는 중 오류가 발생했습니다" + e.getResponseBodyAsString());
        }
    }
}
