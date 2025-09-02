package com.green.chakak.chakak.account.user;



import com.green.chakak.chakak.account.user_type.UserType;
import com.green.chakak.chakak.account.user_type.UserTypeRepository;
import com.green.chakak.chakak.global.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserJpaRepository userJpaRepository;
    private final UserTypeRepository userTypeRepository;


    // 회원가입
    public UserResponse.SignupResponse signup(UserRequest.SignupRequest req) {

        if (userJpaRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        UserType userType = userTypeRepository.findByTypeCode(req.getUserTypeCode())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 유형 코드입니다."));


        User user = User.builder()
                .email(req.getEmail())
                .password(req.getPassword())
                .userType(userType)
                .status(User.UserStatus.ACTIVE)
                .build();

        User saved = userJpaRepository.save(user);

        return UserResponse.SignupResponse.builder()
                .userId(saved.getUserId())
                .email(saved.getEmail())
                .userTypeCode(saved.getUserType().getTypeCode())
                .status(saved.getStatus())
                .build();
    }

    // 로그인 (JWT)
    @Transactional(readOnly = true)
    public String login(UserRequest.LoginRequest req) {
        User user = userJpaRepository.findByEmailAndUserPassword(req.getEmail(), req.getPassword())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));

        if (user.getStatus() == User.UserStatus.SUSPENDED || user.getStatus() == User.UserStatus.INACTIVE) {
            throw new IllegalStateException("현재 상태로는 로그인할 수 없습니다. (정지/비활성)");
        }

        LoginUser loginUser = LoginUser.fromEntity(user); // (id, email, typeCode, status)
        return JwtUtil.create(loginUser);
    }
}