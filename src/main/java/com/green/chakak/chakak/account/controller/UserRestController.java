package com.green.chakak.chakak.account.controller;

import com.green.chakak.chakak._global.utils.Define;
import com.green.chakak.chakak.account.domain.LoginUser;
import com.green.chakak.chakak.account.service.request.UserRequest;
import com.green.chakak.chakak.account.service.response.UserResponse;
import com.green.chakak.chakak.account.service.UserService;
import com.green.chakak.chakak._global.utils.ApiUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserRestController {

    private final UserService userService;

    //회원 가입
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody UserRequest.SignupRequest req) {
        UserResponse.SignupResponse response = userService.signup(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserRequest.LoginRequest req) {
        UserResponse.LoginResponse response = userService.login(req); // 토큰 + 유저정보
        return ResponseEntity.ok()
                .header(Define.AUTH, Define.BEARER + response.getAccessToken())
                .body(new ApiUtil<>(response));
    }
    
    // [수정] 회원 정보 수정 (ArgumentResolver 사용)
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequest.UpdateRequest req,
                                        LoginUser loginUser) { // LoginUser 자동 주입
        if (!loginUser.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("본인만 수정할 수 있습니다.");
        }
        UserResponse.UpdateResponse response = userService.updateUser(id, req, loginUser);
        return ResponseEntity.ok(response);
    }

    // [수정] 회원 탈퇴 (ArgumentResolver 사용)
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, LoginUser loginUser) { // LoginUser 자동 주입
        if (!loginUser.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("본인만 탈퇴할 수 있습니다.");
        }
        userService.deleteUser(id, loginUser);
        return ResponseEntity.ok(new ApiUtil<>("회원 탈퇴가 완료되었습니다."));
    }
}
