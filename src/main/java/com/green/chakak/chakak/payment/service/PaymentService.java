package com.green.chakak.chakak.payment.service;


import com.green.chakak.chakak._global.errors.exception.Exception404;
import com.green.chakak.chakak.account.domain.LoginUser;
import com.green.chakak.chakak.booking.domain.BookingInfo;
import com.green.chakak.chakak.booking.domain.BookingStatus;
import com.green.chakak.chakak.booking.service.repository.BookingInfoJpaRepository;
import com.green.chakak.chakak.payment.domain.Payment;
import com.green.chakak.chakak.payment.domain.PaymentStatus;
import com.green.chakak.chakak.payment.repository.PaymentJpaRepository;
import com.green.chakak.chakak.payment.repository.response.KakaoPaymentApproveResponse;
import com.green.chakak.chakak.payment.repository.response.KakaoPaymentReadyResponse;
import com.green.chakak.chakak.payment.repository.response.PaymentApproveCompleteResponse;
import com.green.chakak.chakak.payment.repository.response.PaymentReadyResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentService {

    private final WebClient webClient;
    private final BookingInfoJpaRepository bookingInfoJpaRepository;
    private final PaymentJpaRepository paymentJpaRepository;

    @Value("${kakao.pay.secret-key}")
    private String secretKey;

    @Value("${kakao.pay.cid}")
    private String cid;

    @Value("${kakao.pay.api-url}")
    private String apiUrl;

    @Value("${server.domain}")
    private String serverDomain; // 예: http://localhost:8080

    public PaymentReadyResponse paymentReady(Long bookingInfoId) {

        // 1. 예약 정보 조회
        BookingInfo bookingInfo = bookingInfoJpaRepository.findById(bookingInfoId)
                .orElseThrow(() -> new RuntimeException("예약 정보를 찾을 수 없습니다."));

        // 2. 주문번호 생성
        String partnerOrderId = generatePartnerOrderId(bookingInfoId);
        String partnerUserId = bookingInfo.getUserProfile().getUserProfileId().toString();

        // 3. 상품명 생성
        String itemName = "사진촬영 서비스 - " +
                bookingInfo.getPhotoServiceInfo().getTitle();

        // 3.1 가격 정보 설정
        int price = bookingInfo.getPriceInfo().getPrice();

        // 4. 카카오페이 결제준비 API 호출
        KakaoPaymentReadyResponse kakaoResponse = callKakaoPaymentReady(
                partnerOrderId, partnerUserId, itemName, price
        );

        // 5. Payment 엔티티 생성 및 저장
        Payment payment = Payment.builder()
                .tid(kakaoResponse.getTid())
                .partnerOrderId(partnerOrderId)
                .partnerUserId(partnerUserId)
                .itemName(itemName)
                .totalAmount(price)           // price 변수 사용
                .vatAmount(calculateVat(price))  // price 변수 사용
                .taxFreeAmount(0)
                .status(PaymentStatus.READY)
                .build();

        Payment savedPayment = paymentJpaRepository.save(payment);

        // 6. BookingInfo에 Payment 연결
        bookingInfo.setPayment(savedPayment);
        //bookingInfo.setStatus(BookingStatus.PENDING); // enum 추가 필요
        //bookingInfoJpaRepository.save(bookingInfo);

        // 8. 응답 DTO 생성
        return PaymentReadyResponse.builder()
                .tid(kakaoResponse.getTid())
                .nextRedirectMobileUrl(kakaoResponse.getNext_redirect_mobile_url())
                .nextRedirectPcUrl(kakaoResponse.getNext_redirect_pc_url())
                .androidAppScheme(kakaoResponse.getAndroid_app_scheme())
                .iosAppScheme(kakaoResponse.getIos_app_scheme())
                .partnerOrderId(partnerOrderId)
                .itemName(itemName)
                .price(price)
                .build();
    }

    private KakaoPaymentReadyResponse callKakaoPaymentReady(String partnerOrderId,
                                                            String partnerUserId,
                                                            String itemName,
                                                            int totalAmount) {

        String approvalUrl = serverDomain + "/api/payment/success?partner_order_id=" + partnerOrderId;
        String failUrl = serverDomain + "/api/payment/fail?partner_order_id=" + partnerOrderId;
        String cancelUrl = serverDomain + "/api/payment/cancel?partner_order_id=" + partnerOrderId;

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("cid", cid);
        requestBody.put("partner_order_id", partnerOrderId);
        requestBody.put("partner_user_id", partnerUserId);
        requestBody.put("item_name", itemName);
        requestBody.put("quantity", 1);
        requestBody.put("total_amount", totalAmount);
        requestBody.put("vat_amount", calculateVat(totalAmount));
        requestBody.put("tax_free_amount", 0);
        requestBody.put("approval_url", approvalUrl);  // 변수 사용
        requestBody.put("fail_url", failUrl);          // 변수 사용
        requestBody.put("cancel_url", cancelUrl);      // 변수 사용

        // 로그 추가
        log.info("=== 카카오페이 API 요청 파라미터 ===");
        log.info("cid: {}", cid);
        log.info("secretKey: {}", secretKey != null ? "설정됨(" + secretKey.length() + "자리)" : "null");
        log.info("partner_order_id: {}", partnerOrderId);
        log.info("partner_user_id: {}", partnerUserId);
        log.info("item_name: {}", itemName);
        log.info("total_amount: {}", totalAmount);
        log.info("approval_url: {}", serverDomain + "/api/payment/success");

        // 로그 추가
        log.info("=== 카카오페이로 전송하는 URL들 ===");
        log.info("approval_url: {}", approvalUrl);
        log.info("fail_url: {}", failUrl);
        log.info("cancel_url: {}", cancelUrl);
        log.info("=====================================");

        try {
            return webClient.post()
                    .uri(apiUrl + "/online/v1/payment/ready")
                    .headers(headers -> {
                        headers.remove(HttpHeaders.AUTHORIZATION);
                        headers.set(HttpHeaders.AUTHORIZATION, "SECRET_KEY " + secretKey);
                    })
                    .header(HttpHeaders.CONTENT_TYPE, "application/json")  // JSON으로 변경
                    .bodyValue(requestBody)  // JSON 본문으로 전송
                    .retrieve()
                    .onStatus(status -> status.isError(), response -> {
                        return response.bodyToMono(String.class)
                                .doOnNext(errorBody -> log.error("카카오페이 에러 응답: {}", errorBody))
                                .then(Mono.error(new RuntimeException("카카오페이 API 에러")));
                    })
                    .bodyToMono(KakaoPaymentReadyResponse.class)
                    .block();

        } catch (Exception e) {
            log.error("카카오페이 결제준비 API 호출 실패", e);
            throw new RuntimeException("결제 준비 중 오류가 발생했습니다.");
        }
    }

    private String generatePartnerOrderId(Long bookingInfoId) {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "ORD" + today + "_" + bookingInfoId;
    }

    private int calculateVat(int totalAmount) {
        return (int) Math.round(totalAmount / 11.0); // 부가세 10%
    }

    /**
     * 결제 승인 처리 (주문번호로 TID 조회)
     * 카카오페이 권장 방식
     */
    public PaymentApproveCompleteResponse paymentApproveByOrderId(String pgToken, String partnerOrderId) {

        log.info("결제 승인 시작 (주문번호) - partnerOrderId: {}, pgToken: {}", partnerOrderId, pgToken);

        // 1. partner_order_id로 Payment 조회
        Payment payment = paymentJpaRepository.findByPartnerOrderId(partnerOrderId)
                .orElseThrow(() -> new RuntimeException("결제 정보를 찾을 수 없습니다. 주문번호: " + partnerOrderId));

        log.info("주문번호로 조회된 TID: {}", payment.getTid());

        // 2. 기존 결제 승인 로직 호출
        return paymentApprove(pgToken, payment.getTid());
    }

    /**
     * 결제 승인 처리
     * 카카오페이 success 콜백에서 호출
     */
    public PaymentApproveCompleteResponse paymentApprove(String pgToken, String tid) {

        log.info("결제 승인 시작 - tid: {}, pgToken: {}", tid, pgToken);

        // 1. TID로 Payment 조회
        Payment payment = paymentJpaRepository.findByTid(tid)
                .orElseThrow(() -> new RuntimeException("결제 정보를 찾을 수 없습니다. TID: " + tid));

        // 2. 카카오페이 결제승인 API 호출
        KakaoPaymentApproveResponse kakaoResponse = callKakaoPaymentApprove(
                payment.getPartnerOrderId(),
                payment.getPartnerUserId(),
                pgToken,
                tid
        );

        // 3. Payment 정보 업데이트
        payment.setStatus(PaymentStatus.APPROVED);
        payment.setAid(kakaoResponse.getAid());
        payment.setPaymentMethodType(kakaoResponse.getPayment_method_type());
        payment.setApprovedAt(java.sql.Timestamp.valueOf(LocalDateTime.now()));

        // 4. BookingInfo 상태 업데이트 (Payment와 연결된 BookingInfo 찾기)
        BookingInfo bookingInfo = bookingInfoJpaRepository.findByPayment(payment)
                .orElseThrow(() -> new RuntimeException("예약 정보를 찾을 수 없습니다."));

        bookingInfo.setStatus(BookingStatus.CONFIRMED); // 결제 완료 시 예약 확정

        log.info("결제 승인 완료 - aid: {}, bookingInfoId: {}",
                kakaoResponse.getAid(), bookingInfo.getBookingInfoId());

        // 5. 응답 DTO 생성
        return PaymentApproveCompleteResponse.builder()
                .aid(kakaoResponse.getAid())
                .tid(kakaoResponse.getTid())
                .partnerOrderId(kakaoResponse.getPartner_order_id())
                .itemName(kakaoResponse.getItem_name())
                .totalAmount(kakaoResponse.getAmount().getTotal())
                .paymentMethodType(kakaoResponse.getPayment_method_type())
                .approvedAt(kakaoResponse.getApproved_at())
                .bookingInfoId(bookingInfo.getBookingInfoId())
                .build();
    }

    private KakaoPaymentApproveResponse callKakaoPaymentApprove(String partnerOrderId,
                                                                String partnerUserId,
                                                                String pgToken,
                                                                String tid) {

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("cid", cid);
        requestBody.put("tid", tid);
        requestBody.put("partner_order_id", partnerOrderId);
        requestBody.put("partner_user_id", partnerUserId);
        requestBody.put("pg_token", pgToken);

        log.info("=== 카카오페이 결제승인 API 요청 ===");
        log.info("tid: {}", tid);
        log.info("partner_order_id: {}", partnerOrderId);
        log.info("partner_user_id: {}", partnerUserId);
        log.info("pg_token: {}", pgToken);
        log.info("=====================================");


        try {
            return webClient.post()
                    .uri(apiUrl + "/online/v1/payment/approve")
                    .headers(headers -> {
                        headers.remove(HttpHeaders.AUTHORIZATION);
                        headers.set(HttpHeaders.AUTHORIZATION, "SECRET_KEY " + secretKey);
                    })
                    .header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(status -> status.isError(), response -> {
                        return response.bodyToMono(String.class)
                                .doOnNext(errorBody -> log.error("카카오페이 승인 에러 응답: {}", errorBody))
                                .then(Mono.error(new RuntimeException("카카오페이 결제승인 API 에러")));
                    })
                    .bodyToMono(KakaoPaymentApproveResponse.class)
                    .block();

        } catch (Exception e) {
            log.error("카카오페이 결제승인 API 호출 실패", e);
            throw new RuntimeException("결제 승인 중 오류가 발생했습니다.");
        }
    }

    /**
     * 결제 실패 처리
     */
    public void paymentFail(String partnerOrderId, String errorMessage) {

        log.info("결제 실패 처리 - partnerOrderId: {}, error: {}", partnerOrderId, errorMessage);

        if (partnerOrderId != null) {
            // partner_order_id로 Payment 조회
            Payment payment = paymentJpaRepository.findByPartnerOrderId(partnerOrderId)
                    .orElse(null);

            if (payment != null) {
                // Payment 상태를 FAILED로 변경
                payment.setStatus(PaymentStatus.FAILED);

                // BookingInfo 상태는 PENDING 유지 (다시 결제 시도 가능)
                BookingInfo bookingInfo = bookingInfoJpaRepository.findByPayment(payment)
                        .orElse(null);

                if (bookingInfo != null) {
                    log.info("결제 실패로 인한 예약 상태 유지 - bookingInfoId: {}", bookingInfo.getBookingInfoId());
                }

                log.info("결제 실패 처리 완료 - paymentId: {}, status: FAILED", payment.getPaymentId());
            }
        } else {
            // partner_order_id가 없는 경우 가장 최근 READY 상태 Payment 실패 처리
            Payment payment = paymentJpaRepository.findTopByStatusOrderByCreatedAtDesc(PaymentStatus.READY)
                    .orElse(null);

            if (payment != null) {
                payment.setStatus(PaymentStatus.FAILED);
                log.info("최근 READY 상태 Payment 실패 처리 - paymentId: {}", payment.getPaymentId());
            }
        }
    }

    /**
     * 결제 취소 처리
     */
    public void paymentCancel(String partnerOrderId) {

        log.info("결제 취소 처리 - partnerOrderId: {}", partnerOrderId);

        if (partnerOrderId != null) {
            // partner_order_id로 Payment 조회
            Payment payment = paymentJpaRepository.findByPartnerOrderId(partnerOrderId)
                    .orElse(null);

            if (payment != null) {
                // Payment 상태를 CANCELED로 변경
                payment.setStatus(PaymentStatus.CANCELED);

                // BookingInfo 상태는 PENDING 유지 (다시 결제 시도 가능)
                BookingInfo bookingInfo = bookingInfoJpaRepository.findByPayment(payment)
                        .orElse(null);

                if (bookingInfo != null) {
                    log.info("결제 취소로 인한 예약 상태 유지 - bookingInfoId: {}", bookingInfo.getBookingInfoId());
                }

                log.info("결제 취소 처리 완료 - paymentId: {}, status: CANCELED", payment.getPaymentId());
            }
        } else {
            // partner_order_id가 없는 경우 가장 최근 READY 상태 Payment 취소 처리
            Payment payment = paymentJpaRepository.findTopByStatusOrderByCreatedAtDesc(PaymentStatus.READY)
                    .orElse(null);

            if (payment != null) {
                payment.setStatus(PaymentStatus.CANCELED);
                log.info("최근 READY 상태 Payment 취소 처리 - paymentId: {}", payment.getPaymentId());
            }
        }
    }

    /**
     * 사용자 권한 검증
     */
    public boolean validateUserPermission(String partnerOrderId, LoginUser loginUser) {

        if (partnerOrderId == null || loginUser == null) {
            return false;
        }

        // USER 타입만 결제 처리 가능
        if (!"USER".equals(loginUser.getUserTypeName())) {
            log.warn("권한 없는 사용자 타입: {}", loginUser.getUserTypeName());
            return false;
        }

        // 주문 소유자 검증
        Payment payment = paymentJpaRepository.findByPartnerOrderId(partnerOrderId).orElse(null);
        if (payment == null) {
            log.warn("결제 정보를 찾을 수 없음: {}", partnerOrderId);
            return false;
        }

        if (!payment.getPartnerUserId().equals(loginUser.getId().toString())) {
            log.warn("주문 소유자가 아님. 주문: {}, 요청자: {}", partnerOrderId, loginUser.getId());
            return false;
        }

        return true;
    }

    /**
     * 결제 실패 처리 (사용자 검증 포함)
     */
    public void paymentFailWithUser(String partnerOrderId, String errorMessage, Long userId) {

        log.info("결제 실패 처리 (사용자 검증) - partnerOrderId: {}, userId: {}", partnerOrderId, userId);

        // partner_order_id로 Payment 조회
        Payment payment = paymentJpaRepository.findByPartnerOrderId(partnerOrderId)
                .orElseThrow(() -> new RuntimeException("결제 정보를 찾을 수 없습니다. 주문번호: " + partnerOrderId));

        // 사용자 소유권 검증
        if (!payment.getPartnerUserId().equals(userId.toString())) {
            throw new RuntimeException("해당 결제에 대한 권한이 없습니다.");
        }

        // Payment 상태를 FAILED로 변경
        payment.setStatus(PaymentStatus.FAILED);

        // BookingInfo 상태는 PENDING 유지 (재결제 가능)
        BookingInfo bookingInfo = bookingInfoJpaRepository.findByPayment(payment)
                .orElse(null);

        if (bookingInfo != null) {
            log.info("결제 실패로 인한 예약 상태 유지 - bookingInfoId: {}", bookingInfo.getBookingInfoId());
        }

        log.info("결제 실패 처리 완료 - paymentId: {}, status: FAILED", payment.getPaymentId());
    }

    /**
     * 결제 취소 처리 (사용자 검증 포함)
     */
    public void paymentCancelWithUser(String partnerOrderId, Long userId) {

        log.info("결제 취소 처리 (사용자 검증) - partnerOrderId: {}, userId: {}", partnerOrderId, userId);

        // partner_order_id로 Payment 조회
        Payment payment = paymentJpaRepository.findByPartnerOrderId(partnerOrderId)
                .orElseThrow(() -> new RuntimeException("결제 정보를 찾을 수 없습니다. 주문번호: " + partnerOrderId));

        // 사용자 소유권 검증
        if (!payment.getPartnerUserId().equals(userId.toString())) {
            throw new RuntimeException("해당 결제에 대한 권한이 없습니다.");
        }

        // Payment 상태를 CANCELED로 변경
        payment.setStatus(PaymentStatus.CANCELED);

        // BookingInfo 상태도 CANCELED로 변경
        BookingInfo bookingInfo = bookingInfoJpaRepository.findByPayment(payment)
                .orElse(null);

        if (bookingInfo != null) {
            bookingInfo.setStatus(BookingStatus.CANCELED);
            log.info("예약 취소 완료 - bookingInfoId: {}", bookingInfo.getBookingInfoId());
        }

        log.info("결제 취소 처리 완료 - paymentId: {}, status: CANCELED", payment.getPaymentId());
    }



    /**
     * pg_token만으로 결제 승인 처리
     * 가장 최근의 READY 상태 Payment 조회
     */
    public PaymentApproveCompleteResponse paymentApproveByPgToken(String pgToken) {

        log.info("pg_token만으로 결제 승인 시작 - pgToken: {}", pgToken);

        // 1. 가장 최근의 READY 상태 Payment 조회
        Payment payment = paymentJpaRepository.findTopByStatusOrderByCreatedAtDesc(PaymentStatus.READY)
                .orElseThrow(() -> new RuntimeException("대기 중인 결제 정보를 찾을 수 없습니다."));

        log.info("가장 최근 READY 상태 Payment 조회 - TID: {}, 주문번호: {}",
                payment.getTid(), payment.getPartnerOrderId());

        // 2. 기존 결제 승인 로직 호출
        return paymentApprove(pgToken, payment.getTid());
    }
}