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

import java.util.Objects;

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

        // 1. 해당 유저로 이미 생성된 프로필이 있는지 확인 (1인 1프로필 보장)
        userProfileJpaRepository.findByUserId(user.getUserId()).ifPresent(profile -> {
            throw new Exception400("이미 프로필이 존재합니다.");
        });

        // 2. "일반 유저"만 생성 가능한지 확인
        if (!"user".equalsIgnoreCase(user.getUserType().getTypeCode())){
            throw new Exception400("일반 사용자만 프로필을 생성할 수 있습니다.");
        }

        // 3. 닉네임 중복 확인
        userProfileJpaRepository.findByNickName(createDTO.getNickName()).ifPresent(up -> {
            throw new Exception400("이미 사용중인 닉네임입니다.");
        });

        // 4. 프로필 저장
        UserProfile savedProfile = userProfileJpaRepository.save(createDTO.toEntity(user));
        return new UserProfileResponse.DetailDTO(savedProfile);
    }

    @Transactional
    public UserProfileResponse.UpdateDTO updateProfile(UserProfileRequest.UpdateDTO updateDTO, LoginUser loginUser){
        // 1. 수정할 프로필 조회
        UserProfile userProfile = userProfileJpaRepository.findByUserId(loginUser.getId())
                .orElseThrow(() -> new Exception404("수정할 프로필이 존재하지 않습니다."));

        // 2. 닉네임 변경 시, 중복 여부 확인 (버그 수정 완료)
        if (updateDTO.getNickName() != null && !Objects.equals(updateDTO.getNickName(), userProfile.getNickName())) {
            userProfileJpaRepository.findByNickName(updateDTO.getNickName()).ifPresent(foundProfile -> {
                // 내가 아닌 다른 사람이 해당 닉네임을 사용 중인 경우
                if (!foundProfile.getUserProfileId().equals(userProfile.getUserProfileId())) {
                    throw new Exception400("이미 사용중인 닉네임입니다.");
                }
            });
        }

        // 3. 프로필 업데이트
        userProfile.update(updateDTO);
        return new UserProfileResponse.UpdateDTO(userProfile);
    }

    public UserProfileResponse.DetailDTO getMyProfile(LoginUser loginUser) {
        UserProfile userProfile = userProfileJpaRepository.findByUserId(loginUser.getId())
                .orElseThrow(() -> new Exception404("프로필이 존재하지 않습니다."));
        return new UserProfileResponse.DetailDTO(userProfile);
    }

}
