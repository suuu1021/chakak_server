package com.green.chakak.chakak.account.service;

import com.green.chakak.chakak._global.errors.exception.Exception400;
import com.green.chakak.chakak._global.errors.exception.Exception401;
import com.green.chakak.chakak._global.errors.exception.Exception403;
import com.green.chakak.chakak._global.errors.exception.Exception404;
import com.green.chakak.chakak._global.utils.JwtUtil;
import com.green.chakak.chakak.account.domain.LoginUser;
import com.green.chakak.chakak.account.domain.User;
import com.green.chakak.chakak.account.domain.UserType;
import com.green.chakak.chakak.account.service.repository.UserJpaRepository;
import com.green.chakak.chakak.account.service.repository.UserProfileJpaRepository;
import com.green.chakak.chakak.account.service.repository.UserTypeRepository;
import com.green.chakak.chakak.account.service.request.UserRequest;
import com.green.chakak.chakak.account.service.response.UserResponse;
import com.green.chakak.chakak.email_verification.EmailService;
import com.green.chakak.chakak.email_verification.EmailVerificationRepository; // Import EmailVerificationRepository
import com.green.chakak.chakak.email_verification.EmailVerification; // Import EmailVerification
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserJpaRepository userJpaRepository;
    private final UserTypeRepository userTypeRepository;
    private final UserProfileJpaRepository userProfileJpaRepository;
    private final EmailService emailService;
    private final EmailVerificationRepository emailVerificationRepository; // Inject EmailVerificationRepository

    // 회원가입
    public UserResponse.SignupResponse signup(UserRequest.SignupRequest req) {

        if (userJpaRepository.existsByEmail(req.getEmail())) {
            throw new Exception400("이미 사용 중인 이메일입니다.");
        }

        UserType userType = userTypeRepository.findByTypeCode(req.getUserTypeCode())
                .orElseThrow(() -> new Exception400("존재하지 않는 사용자 유형 코드입니다."));

        // 이메일 인증 상태 확인
        Optional<EmailVerification> verificationOpt = emailVerificationRepository.findByEmail(req.getEmail());
        boolean isEmailVerified = verificationOpt.isPresent() && verificationOpt.get().isVerified();

        User savedUser = userJpaRepository.save(req.toEntity(userType, isEmailVerified)); // Pass isEmailVerified
        // TODO(테스트) User savedUser = userJpaRepository.save(req.toEntity(userType)); // Pass isEmailVerified

        // 인증 이메일 발송 (회원가입 시점에 발송하도록 유지)
        emailService.sendVerificationEmail(req.getEmail());

        return UserResponse.SignupResponse.from(savedUser);
    }


    // 이메일 인증 완료 처리 (이 메서드는 EmailController에서 더 이상 호출되지 않습니다)
    @Transactional
    public void completeEmailVerification(String email) {

        User user = userJpaRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));
        user.completeEmailVerification();
        userJpaRepository.save(user);
    }


    // 로그인 (JWT)
    @Transactional(readOnly = true)
    public UserResponse.LoginResponse login(UserRequest.LoginRequest req) {
        User user = userJpaRepository.findByEmailAndUserPassword(req.getEmail(), req.getPassword())
                .orElseThrow(() -> new Exception401("이메일 또는 비밀번호가 올바르지 않습니다."));

        UserType userType = userTypeRepository.findByTypeCode(user.getUserType().getTypeCode())
                .orElseThrow(() -> new Exception401("존재하지 않는 사용자 유형 코드입니다."));

        if (user.getStatus() == User.UserStatus.SUSPENDED || user.getStatus() == User.UserStatus.INACTIVE) {
            throw new IllegalStateException("현재 상태로는 로그인할 수 없습니다. (정지/비활성)");
        }

        LoginUser loginUser = LoginUser.fromEntity(user);
        String token = JwtUtil.create(loginUser);

        String nickname = userProfileJpaRepository.findByUserId(user.getUserId())
                .map(profile -> profile.getNickName())
                .orElse("");

        return UserResponse.LoginResponse.of(user, token, nickname, userType);
    }
    // 회원 정보 수정

    public UserResponse.UpdateResponse updateUser(Long userId, UserRequest.UpdateRequest req, LoginUser loginUser) {
        if (!loginUser.getId().equals(userId)) {
            throw new Exception403("본인만 수정할 수 있습니다.");
        }
        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new Exception404("존재하지 않는 사용자입니다."));
        if (req.getEmail() != null && !req.getEmail().isEmpty()) {
            if (!user.getEmail().equals(req.getEmail()) && userJpaRepository.existsByEmail(req.getEmail())) {
                throw new Exception400("이미 사용 중인 이메일입니다.");
            }
            user.changeEmail(req.getEmail());
        }
        if (req.getPassword() != null && !req.getPassword().isEmpty()) {
            user.setPassword(req.getPassword());
        }
        User updatedUser = userJpaRepository.save(user);
        return UserResponse.UpdateResponse.from(updatedUser);
    }

    public void deleteUser(Long id, LoginUser loginUser) {
        if (!loginUser.getId().equals(id)) {
            throw new Exception403("본인만 탈퇴할 수 있습니다.");
        }
        if (!userJpaRepository.existsById(id)) {
            throw new Exception404("존재하지 않는 사용자입니다.");
        }
        userJpaRepository.deleteById(id);
    }
}
