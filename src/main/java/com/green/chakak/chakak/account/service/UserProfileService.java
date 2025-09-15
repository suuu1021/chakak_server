package com.green.chakak.chakak.account.service;

import com.green.chakak.chakak.account.domain.LoginUser;
import com.green.chakak.chakak.account.domain.User;
import com.green.chakak.chakak.account.domain.UserProfile;
import com.green.chakak.chakak.account.service.repository.UserJpaRepository;
import com.green.chakak.chakak.account.service.repository.UserProfileJpaRepository;
import com.green.chakak.chakak.account.service.request.UserProfileRequest;
import com.green.chakak.chakak.account.service.response.UserProfileResponse;
import com.green.chakak.chakak._global.errors.exception.Exception400;
import com.green.chakak.chakak._global.errors.exception.Exception404;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
@Slf4j
public class UserProfileService {

    private final UserProfileJpaRepository userProfileJpaRepository;
    private final UserJpaRepository userJpaRepository;


    @Transactional
    public UserProfileResponse.DetailDTO createdProfile(UserProfileRequest.CreateDTO createDTO){
        User user = userJpaRepository.findById(createDTO.getUserInfoId()).orElseThrow(
                () -> new Exception404("존재하지 않는 유저입니다.")
        );
        log.info("user.getUserType(): {}", user.getUserType());
        if (user.getUserType().getTypeCode() == null || !"user".equalsIgnoreCase(user.getUserType().getTypeCode())){
            throw new Exception400("일반 사용자만 프로필을 생성할 수 있습니다.");
        }

        userProfileJpaRepository.findByNickName(createDTO.getNickName()).ifPresent(up -> {
            throw new Exception400("이미 사용중인 닉네임입니다.");
        });
        UserProfile savedProfile = userProfileJpaRepository.save(createDTO.toEntity(user));
        return new UserProfileResponse.DetailDTO(savedProfile);
    }

    @Transactional
    public UserProfileResponse.UpdateDTO updateProfile(UserProfileRequest.UpdateDTO updateDTO, LoginUser loginUser){
        UserProfile userProfile = userProfileJpaRepository.findByUserId(loginUser.getId())
                .orElseThrow(() -> new Exception404("수정할 프로필이 존재하지 않습니다."));

        userProfileJpaRepository.findByNickName(updateDTO.getNickName()).ifPresent(userProfile1 -> {
            if(!userProfile1.getUserProfileId().equals(userProfile.getUserProfileId()))
                throw new Exception400("이미 사용중인 닉네임입니다.");
        });

        userProfile.update(updateDTO);
        return new UserProfileResponse.UpdateDTO(userProfile);
    }

    public UserProfileResponse.DetailDTO getMyProfile(LoginUser loginUser) {
        UserProfile userProfile = userProfileJpaRepository.findByUserId(loginUser.getId())
                .orElseThrow(() -> new Exception404("프로필이 존재하지 않습니다."));
        return new UserProfileResponse.DetailDTO(userProfile);
    }

}