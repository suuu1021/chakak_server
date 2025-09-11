package com.green.chakak.chakak.payment.controller;

import com.green.chakak.chakak._global.utils.Define;
import com.green.chakak.chakak.account.domain.LoginUser;
import com.green.chakak.chakak.payment.repository.request.PaymentReadyRequest;
import com.green.chakak.chakak.payment.repository.response.PaymentApproveCompleteResponse;
import com.green.chakak.chakak.payment.repository.response.PaymentReadyResponse;
import com.green.chakak.chakak.payment.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Slf4j
public class PaymentRestController {

    private final PaymentService paymentService;

    /**
     * 결제 준비 API
     * 프론트에서 결제하기 버튼 클릭 시 호출
     */
    @PostMapping("/ready")
    public ResponseEntity<PaymentReadyResponse> paymentReady(@RequestBody PaymentReadyRequest request) {

        log.info("결제 준비 요청 - bookingInfoId: {}", request.getBookingInfoId());

        try {
            PaymentReadyResponse response = paymentService.paymentReady(request.getBookingInfoId());

            log.info("결제 준비 성공 - tid: {}, partnerOrderId: {}",
                    response.getTid(), response.getPartnerOrderId());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("결제 준비 실패 - bookingInfoId: {}", request.getBookingInfoId(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 결제 성공 콜백 URL
     * 카카오페이에서 결제 완료 후 호출
     */
    @GetMapping("/success")
    public ResponseEntity<?> paymentSuccess(@RequestParam String pg_token,
                                            @RequestParam(required = false) String partner_order_id,
                                            @RequestParam(required = false) String tid,
                                            HttpServletRequest request) {

        log.info("결제 성공 콜백 - pg_token: {}, partner_order_id: {}, tid: {}", pg_token, partner_order_id, tid);
        log.info("모든 파라미터 출력:");
        request.getParameterMap().forEach((key, values) -> {
            log.info("  {}: {}", key, String.join(",", values));
        });

        try {
            PaymentApproveCompleteResponse response;

            if (partner_order_id != null) {
                // partner_order_id가 있는 경우
                response = paymentService.paymentApproveByOrderId(pg_token, partner_order_id);
            } else if (tid != null) {
                // tid가 있는 경우
                response = paymentService.paymentApprove(pg_token, tid);
            } else {
                // 둘 다 없는 경우 - 가장 최근 READY 상태 Payment 찾기 (임시)
                log.warn("partner_order_id와 tid 모두 없음. 임시 처리 시도");
                throw new RuntimeException("결제 정보를 식별할 수 없습니다.");
            }

            log.info("결제 승인 완료 - aid: {}, bookingInfoId: {}",
                    response.getAid(), response.getBookingInfoId());

            // 프론트로 리다이렉트 (딥링크) - 결제 성공 정보 포함
            String redirectUrl = String.format("myapp://payment/success?aid=%s&bookingInfoId=%s",
                    response.getAid(), response.getBookingInfoId());

            return ResponseEntity.status(302)
                    .header("Location", redirectUrl)
                    .build();

        } catch (Exception e) {
            log.error("결제 승인 실패", e);

            // 결제 승인 실패 시 실패 페이지로 리다이렉트
            return ResponseEntity.status(302)
                    .header("Location", "myapp://payment/fail?error=approve_failed")
                    .build();
        }
    }

    /**
     * 결제 실패 콜백 URL
     * 카카오페이에서 결제 실패 시 호출
     */
    @GetMapping("/fail")
    public ResponseEntity<?> paymentFail(@RequestParam(required = false) String partner_order_id,
                                         @RequestParam(required = false) String error_msg,
                                         @RequestAttribute(value = Define.LOGIN_USER, required = false) LoginUser loginUser,
                                         HttpServletRequest request) {

        log.info("결제 실패 콜백 - partner_order_id: {}, error_msg: {}", partner_order_id, error_msg);
        log.info("실패 콜백 모든 파라미터:");
        request.getParameterMap().forEach((key, values) -> {
            log.info("  {}: {}", key, String.join(",", values));
        });

        try {
            // 로그인 사용자 정보가 있는 경우 권한 검증
            if (loginUser != null && partner_order_id != null) {
                // 사용자 권한 검증
                if (!paymentService.validateUserPermission(partner_order_id, loginUser)) {
                    return ResponseEntity.status(403)
                            .header("Location", "myapp://payment/fail?error=unauthorized")
                            .build();
                }

                // 결제 실패 처리 (사용자 검증 포함)
                paymentService.paymentFailWithUser(partner_order_id, error_msg, loginUser.getId());
            } else {
                // 기존 방식 (권한 검증 없음)
                paymentService.paymentFail(partner_order_id, error_msg);
            }

            // 프론트로 리다이렉트 (딥링크) - 실패 정보 포함
            String redirectUrl = "myapp://payment/fail";
            if (error_msg != null) {
                redirectUrl += "?error=" + error_msg;
            }

            return ResponseEntity.status(302)
                    .header("Location", redirectUrl)
                    .build();

        } catch (Exception e) {
            log.error("결제 실패 처리 중 오류", e);
            return ResponseEntity.status(302)
                    .header("Location", "myapp://payment/fail?error=system_error")
                    .build();
        }
    }

    /**
     * 결제 취소 콜백 URL
     * 카카오페이에서 결제 취소 시 호출
     */
    @GetMapping("/cancel")
    public ResponseEntity<?> paymentCancel(@RequestParam(required = false) String partner_order_id,
                                           @RequestAttribute(value = Define.LOGIN_USER, required = false) LoginUser loginUser,
                                           HttpServletRequest request) {

        log.info("결제 취소 콜백 - partner_order_id: {}", partner_order_id);
        log.info("취소 콜백 모든 파라미터:");
        request.getParameterMap().forEach((key, values) -> {
            log.info("  {}: {}", key, String.join(",", values));
        });

        try {
            // 로그인 사용자 정보가 있는 경우 권한 검증
            if (loginUser != null && partner_order_id != null) {
                // 사용자 권한 검증
                if (!paymentService.validateUserPermission(partner_order_id, loginUser)) {
                    return ResponseEntity.status(403)
                            .header("Location", "myapp://payment/cancel?error=unauthorized")
                            .build();
                }

                // 결제 취소 처리 (사용자 검증 포함)
                paymentService.paymentCancelWithUser(partner_order_id, loginUser.getId());
            } else {
                // 기존 방식 (권한 검증 없음)
                paymentService.paymentCancel(partner_order_id);
            }

            // 프론트로 리다이렉트 (딥링크)
            return ResponseEntity.status(302)
                    .header("Location", "myapp://payment/cancel")
                    .build();

        } catch (Exception e) {
            log.error("결제 취소 처리 중 오류", e);
            return ResponseEntity.status(302)
                    .header("Location", "myapp://payment/cancel?error=system_error")
                    .build();
        }
    }
}