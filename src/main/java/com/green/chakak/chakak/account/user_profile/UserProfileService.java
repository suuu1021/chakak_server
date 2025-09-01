package com.green.chakak.chakak.account.user_profile;

import com.green.chakak.chakak.account.user.LoginUser;
import com.green.chakak.chakak.account.user.User;
import com.green.chakak.chakak.account.user.UserJpaRepository;
import com.green.chakak.chakak.global.errors.exception.Exception400;
import com.green.chakak.chakak.global.errors.exception.Exception404;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserProfileService {

    private final UserProfileJpaRepository userProfileJpaRepository;
    private final UserJpaRepository userJpaRepository;

    @Transactional
    public UserProfileResponse.DetailDTO createdProfile(UserProfileRequest.CreateDTO createDTO, LoginUser loginUser){
        User user = userJpaRepository.findById(loginUser.getId())
                .orElseThrow(() -> new Exception404("존재하지 않는 유저입니다."));

        userProfileJpaRepository.findByUserId(loginUser.getId()).ifPresent(up -> {
            throw new Exception400("이미 프로필을 작성하셨습니다.");
        });
        userProfileJpaRepository.findByNickName(createDTO.getNickName()).ifPresent(up -> {
            throw new Exception400("이미 존재하는 닉네임입니다.");
        });
        UserProfile savedProfile = userProfileJpaRepository.save(createDTO.toEntity(user));

        return new UserProfileResponse.DetailDTO(savedProfile);
    }

}
