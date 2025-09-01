package com.green.chakak.chakak.account.user_profile;

import com.green.chakak.chakak.account.user.LoginUser;
import com.green.chakak.chakak.account.user.User;
import com.green.chakak.chakak.global.util.Define;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserProfileRestController {

    private final UserProfileService userProfileService;
    //private final UserService userService;

    // 프로필 생성
    @GetMapping("/profile")
    public ResponseEntity<?> createProfile(@Valid @RequestBody UserProfileRequest.CreateDTO createDTO,
                                         Errors errors,
                                         @RequestAttribute(value = Define.LOGIN_USER, required = false) LoginUser loginUser){
        UserProfileResponse.DetailDTO userProfileDetail = userProfileService.createdProfile(createDTO,loginUser);
        return ResponseEntity.ok(userProfileDetail);
    }
}
