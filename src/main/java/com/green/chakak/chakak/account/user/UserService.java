package com.green.chakak.chakak.account.user;

import com.green.chakak.chakak.account.userType.UserType;
import com.green.chakak.chakak.account.userType.UserTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserJpaRepository userJpaRepository;
    private final UserTypeRepository userTypeRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse.UserSignupResponse signup(UserRequest.UserSignupRequest req) {

        if (userJpaRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }


        UserType userType = userTypeRepository.findByTypeCode(req.getUserTypeCode())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 유형 코드입니다."));

        //  비밀번호 해시
        String encoded = passwordEncoder.encode(req.getPassword());


        User user = User.builder()
                .email(req.getEmail())
                .password(encoded)
                .userType(userType)
                .status(User.UserStatus.ACTIVE)
                .build();


        User saved = userJpaRepository.save(user);


        return UserResponse.UserSignupResponse.builder()
                .userId(saved.getId())
                .email(saved.getEmail())
                .userTypeCode(saved.getUserType().getTypeCode())
                .status(saved.getStatus())
                .build();
    }
}