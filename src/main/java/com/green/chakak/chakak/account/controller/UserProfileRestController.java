package com.green.chakak.chakak.account.controller;

import com.green.chakak.chakak.account.domain.LoginUser;
import com.green.chakak.chakak.account.domain.User;
import com.green.chakak.chakak.account.service.request.UserProfileRequest;
import com.green.chakak.chakak.account.service.response.UserProfileResponse;
import com.green.chakak.chakak.account.service.UserProfileService;
import com.green.chakak.chakak._global.utils.ApiUtil;
import com.green.chakak.chakak._global.utils.Define;
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
    @PostMapping("/profile")
    public ResponseEntity<?> createProfile(@Valid @RequestBody UserProfileRequest.CreateDTO createDTO,
                                         Errors errors){
        UserProfileResponse.DetailDTO userProfileDetail = userProfileService.createdProfile(createDTO);
        return ResponseEntity.ok(new ApiUtil<>("처리가 완료 되었습니다."));
    }

    // 프로필 수정
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody UserProfileRequest.UpdateDTO updateDTO,
                                           Errors errors,
                                           @RequestAttribute(Define.LOGIN_USER)LoginUser loginuser){
        UserProfileResponse.UpdateDTO updateProfileDTO = userProfileService.updateProfile(updateDTO, loginuser);
        return ResponseEntity.ok(new ApiUtil<>("처리가 완료 되었습니다."));
    }
}
