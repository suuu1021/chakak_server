package com.green.chakak.chakak.payment.repository.response;

import com.green.chakak.chakak.payment.domain.Payment;
import com.green.chakak.chakak.payment.domain.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class PaymentIncomeResponse {
    // 기본 결제 정보
    private Long paymentId;              // 결제 ID
    private String tid;                  // 카카오페이 거래 ID
    private String partnerOrderId;       // 가맹점 주문번호
    private String itemName;             // 상품명
    private int totalAmount;             // 총 결제금액
    private PaymentStatus status;        // 결제 상태
    private String paymentMethodType;    // 결제 수단
    private String aid;                  // 결제 승인 번호
    private Timestamp approvedAt;        // 결제 승인 시간

    // 예약 관련 정보
    private Long bookingInfoId;          // 예약 ID
    private String bookingStatus;        // 예약 상태
    private Timestamp shootingDate;      // 촬영 예정 일시

    // 고객 정보
    private String customerName;         // 고객 이름
    private String customerEmail;        // 고객 이메일

    // 서비스 정보
    private String serviceTitle;         // 서비스 제목
    private String serviceLocation;      // 촬영 장소
    private int serviceDuration;         // 서비스 소요 시간 (분)

    // 수수료 정보 (향후 확장용)
    private int platformFeeRate;         // 플랫폼 수수료율 (%)
    private int platformFeeAmount;       // 플랫폼 수수료 금액
    private int photographerIncome;      // 포토그래퍼 실제 수익

    // Payment 엔티티로부터 기본 정보 PaymentIncomeResponse 생성
    public static PaymentIncomeResponse of(Payment payment) {
        return PaymentIncomeResponse.builder()
                .paymentId(payment.getPaymentId())
                .tid(payment.getTid())
                .partnerOrderId(payment.getPartnerOrderId())
                .itemName(payment.getItemName())
                .totalAmount(payment.getTotalAmount())
                .status(payment.getStatus())
                .paymentMethodType(payment.getPaymentMethodType())
                .aid(payment.getAid())
                .approvedAt(payment.getApprovedAt())
                .build();
    }

    // 전체 정보를 포함한 PaymentIncomeResponse 생성 (조인 쿼리 결과 사용)
    public static PaymentIncomeResponse ofFull(Payment payment,
                                               Long bookingInfoId, String bookingStatus, Timestamp shootingDate,
                                               String customerName, String customerEmail,
                                               String serviceTitle, String serviceLocation, int serviceDuration,
                                               int platformFeeRate) {
        PaymentIncomeResponse response = of(payment);

        // 예약 정보 설정
        response.setBookingInfoId(bookingInfoId);
        response.setBookingStatus(bookingStatus);
        response.setShootingDate(shootingDate);

        // 고객 정보 설정
        response.setCustomerName(customerName);
        response.setCustomerEmail(customerEmail);

        // 서비스 정보 설정
        response.setServiceTitle(serviceTitle);
        response.setServiceLocation(serviceLocation);
        response.setServiceDuration(serviceDuration);

        // 수수료 계산 설정
        response.setPlatformFeeRate(platformFeeRate);
        response.setPlatformFeeAmount(calculatePlatformFee(payment.getTotalAmount(), platformFeeRate));
        response.setPhotographerIncome(calculatePhotographerIncome(payment.getTotalAmount(), platformFeeRate));

        return response;
    }

    // 플랫폼 수수료 계산
    private static int calculatePlatformFee(int totalAmount, int feeRate) {
        return (int) Math.round(totalAmount * feeRate / 100.0);
    }

    // 포토그래퍼 실제 수익 계산
    private static int calculatePhotographerIncome(int totalAmount, int feeRate) {
        return totalAmount - calculatePlatformFee(totalAmount, feeRate);
    }

    // 결제 상태에 따른 한글 설명 반환
    public String getStatusDescription() {
        return status != null ? status.getDescription() : "";
    }

    // 승인된 결제인지 확인 (수익으로 인정되는 결제)
    public boolean isIncomeEligible() {
        return PaymentStatus.APPROVED.equals(status) && aid != null && !aid.trim().isEmpty();
    }

    // 촬영 완료 여부 확인 (예약 상태 기반)
    public boolean isShootingCompleted() {
        return "COMPLETED".equals(bookingStatus);
    }

    // 수수료율 정보 포함한 요약
    public String getIncomeSummary() {
        if (!isIncomeEligible()) {
            return "수익 대상 아님";
        }
        return String.format(
                "총 %,d원 (수수료 %d%% 제외 시 %,d원)",
                totalAmount, platformFeeRate, photographerIncome
        );
    }
}
