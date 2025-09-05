package com.green.chakak.chakak.email_verification;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

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
        return verificationRepository.findByEmailAndVerificationCode(email, code)
                .filter(v -> !v.isExpired() && !v.isVerified())
                .map(verification -> {
                    verification.use(); // Dirty Checking으로 상태 변경이 자동 감지됩니다.
                    return true;
                }).orElse(false);
    }

    private String generateCode() {
        // 예측 불가능한 난수를 위해 SecureRandom 사용을 권장합니다.
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000); // 100000 ~ 999999
        return String.valueOf(code);
    }
}
