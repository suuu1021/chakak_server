package com.green.chakak.chakak.account.controller;

import com.green.chakak.chakak.account.service.KakaoAuthService;
import com.green.chakak.chakak.account.service.request.KakaoLoginRequest;
import com.green.chakak.chakak.account.service.response.KakaoLoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/kakao")
public class KakaoAuthController {

    private final KakaoAuthService kakaoAuthService;


    @GetMapping("/callback")
    public ResponseEntity<String> kakaoCallback(@RequestParam String code) {
        // 단순히 code만 확인 (개발 중에만 사용)
        return ResponseEntity.ok("인가 코드: " + code);
    }

    /**
     * Flutter -> 서버
     * 카카오 로그인 진입 API
     */
    @PostMapping("/login")
    public ResponseEntity<KakaoLoginResponse> kakaoLogin(@RequestBody KakaoLoginRequest request){
        KakaoLoginResponse response = kakaoAuthService.kakaoLogin(request);
        return ResponseEntity.ok(response);
    }
}
