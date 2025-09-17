package com.green.chakak.chakak.email_verification;

import com.green.chakak.chakak._global.utils.ApiUtil;
import com.green.chakak.chakak.account.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;
    private final UserService userService;

    // 인증 코드 발송
    @PostMapping("/send")
    public ResponseEntity<?> sendCode(@RequestBody @Valid EmailRequest.SendDTO requestDTO) {
        emailService.sendVerificationEmail(requestDTO.getEmail());
        return ResponseEntity.ok(new ApiUtil<>(null, "인증 코드가 이메일로 전송되었습니다."));
    }

    // 인증 코드 검증
    @PostMapping("/verify")
    public ResponseEntity<?> verifyCode(@RequestBody @Valid EmailRequest.VerifyDTO requestDTO) {
        boolean isVerified = emailService.verifyCode(requestDTO.getEmail(), requestDTO.getCode());
        if (isVerified) {
            userService.completeEmailVerification(requestDTO.getEmail());
            return ResponseEntity.ok(new ApiUtil<>(true, "이메일 인증에 성공했습니다."));
        } else {
            // 실패 시 HTTP 상태코드를 400으로 내려줌
            return ResponseEntity
                    .status(400)
                    .body(new ApiUtil<>(false, "인증에 실패했습니다. 코드 또는 이메일을 확인해주세요."));
        }
    }

    // DTO 정의
    public static class EmailRequest {
        @Getter
        public static class SendDTO {
            @NotEmpty(message = "이메일은 필수입니다.")
            @Email(message = "올바른 이메일 형식이 아닙니다.")
            private String email;
        }
        @Getter
        public static class VerifyDTO {
            @NotEmpty(message = "이메일은 필수입니다.")
            @Email(message = "올바른 이메일 형식이 아닙니다.")
            private String email;

            @NotEmpty(message = "인증 코드는 필수입니다.")
            private String code;
        }
    }
}
