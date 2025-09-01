package com.green.chakak.chakak.account.user;

import com.green.chakak.chakak.global.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
                .body(new ApiResponse<>(null));
    }
}