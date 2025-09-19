package com.green.chakak.chakak.email_verification;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailService {

    private static final long EMAIL_VERIFICATION_EXPIRATION_MINUTES = 10L;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private final JavaMailSender mailSender;
    private final EmailVerificationRepository verificationRepository;

    @Transactional
    public void sendVerificationEmail(String email) {
        String code = generateCode();
        LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(EMAIL_VERIFICATION_EXPIRATION_MINUTES);

        // 이메일로 기존 인증 정보가 있다면 삭제하여, 항상 새로운 코드가 유효하도록 보장합니다.
        verificationRepository.findByEmail(email).ifPresent(verificationRepository::delete);

        EmailVerification verification = EmailVerification.builder()
                .email(email)
                .verificationCode(code)
                .expiredAt(expiredAt)
                .isVerified(false)
                .build();
        verificationRepository.save(verification);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setFrom(fromEmail);
        message.setSubject("[Chakak] 이메일 인증 코드");
        message.setText("인증 코드: " + code + "\n유효시간: " + EMAIL_VERIFICATION_EXPIRATION_MINUTES + "분");
        mailSender.send(message);
    }

    @Transactional
    public boolean verifyCode(String email, String code) {
        // 1. 이메일과 코드가 정확히 일치하는 데이터를 찾습니다.
        Optional<EmailVerification> verificationOpt = verificationRepository.findByEmailAndVerificationCode(email, code);

        // 데이터가 없으면 false 반환
        if (verificationOpt.isEmpty()) {
            return false;
        }

        EmailVerification verification = verificationOpt.get();

        // 이미 인증된 코드이면 false 반환
        if (verification.isVerified()) {
            return false;
        }

        // 만료 시간이 지났으면 false 반환
        if (verification.isExpired()) {
            return false;
        }

        // 모든 검증을 통과하면 인증 성공 처리
        verification.use(); // isVerified를 true로 변경
        return true;
    }

    // 이메일이 최종적으로 인증되었는지 확인하는 메서드 추가
    public boolean isEmailFullyVerified(String email) {
        return verificationRepository.findTopByEmailOrderByCreatedAtDesc(email)
                .filter(EmailVerification::isVerified) // isVerified가 true인지 확인
                .filter(v -> !v.isExpired()) // 만료되지 않았는지 확인
                .isPresent(); // 해당 조건을 만족하는 레코드가 존재하는지 반환
    }

    private String generateCode() {
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000); // 100000 ~ 999999
        return String.valueOf(code);
    }
}
