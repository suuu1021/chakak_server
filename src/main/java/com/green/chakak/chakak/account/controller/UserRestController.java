package com.green.chakak.chakak.account.controller;

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
    public ResponseEntity<?> login(@RequestBody UserRequest.LoginRequest req) {
        String jwtToken = userService.login(req);
        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + jwtToken)
                .body(new ApiUtil<>(null));
    }



}