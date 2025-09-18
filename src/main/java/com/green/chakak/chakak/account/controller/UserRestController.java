package com.green.chakak.chakak.account.controller;

import com.green.chakak.chakak.account.domain.LoginUser;
import com.green.chakak.chakak.account.service.request.UserRequest;
import com.green.chakak.chakak.account.service.response.UserResponse;
import com.green.chakak.chakak.account.service.UserService;
import com.green.chakak.chakak._global.utils.ApiUtil;
import com.green.chakak.chakak._global.utils.JwtUtil;
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

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody UserRequest.SignupRequest req) {
        UserResponse.SignupResponse response = userService.signup(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserRequest.LoginRequest req) {
        UserResponse.LoginResponse response = userService.login(req);
        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + response.getAccessToken())
                .body(new ApiUtil<>(response));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequest.UpdateRequest req,
                                        @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        LoginUser loginUser = JwtUtil.verify(token);
        if (!loginUser.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("본인만 수정할 수 있습니다.");
        }
        UserResponse.UpdateResponse response = userService.updateUser(id, req, loginUser);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        LoginUser loginUser = JwtUtil.verify(token);
        if (!loginUser.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("본인만 탈퇴할 수 있습니다.");
        }
        userService.deleteUser(id, loginUser);
        return ResponseEntity.ok(new ApiUtil<>("회원 탈퇴가 완료되었습니다."));
    }
}
