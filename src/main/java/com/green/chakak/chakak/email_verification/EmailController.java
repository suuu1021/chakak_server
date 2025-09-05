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

    @PostMapping("/send")
    public ResponseEntity<?> sendCode(@RequestBody @Valid EmailRequest.SendDTO requestDTO) {
        emailService.sendVerificationEmail(requestDTO.getEmail());
        return ResponseEntity.ok(new ApiUtil<>(null, "인증 코드가 이메일로 전송되었습니다."));
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyCode(@RequestBody @Valid EmailRequest.VerifyDTO requestDTO) {
        boolean isVerified = emailService.verifyCode(requestDTO.getEmail(), requestDTO.getCode());
        if (isVerified) {
            userService.completeEmailVerification(requestDTO.getEmail()); // 변경된 메소드 이름으로 호출
            return ResponseEntity.ok(new ApiUtil<>(true, "이메일 인증에 성공했습니다."));
        } else {
            return ResponseEntity.ok(new ApiUtil<>(false, "인증에 실패했습니다. 코드 또는 이메일을 확인해주세요."));
        }
    }

    public static class EmailRequest {
        @Getter
        public static class SendDTO {
            @NotEmpty @Email private String email;
        }
        @Getter
        public static class VerifyDTO {
            @NotEmpty @Email private String email;
            @NotEmpty private String code;
        }
    }
}
