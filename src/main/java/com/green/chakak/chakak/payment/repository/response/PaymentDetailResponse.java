package com.green.chakak.chakak.payment.repository.response;

import com.green.chakak.chakak.payment.domain.Payment;
import com.green.chakak.chakak.payment.domain.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class PaymentDetailResponse {
    // 기본 결제 정보
    private Long paymentId;              // 결제 ID
    private String tid;                  // 카카오페이 거래 ID
    private String partnerOrderId;       // 가맹점 주문번호
    private String partnerUserId;        // 가맹점 회원 ID
    private String itemName;             // 상품명
    private int totalAmount;             // 총 결제금액
    private int vatAmount;               // 부가세
    private int taxFreeAmount;           // 비과세 금액
    private PaymentStatus status;        // 결제 상태
    private String paymentMethodType;    // 결제 수단
    private String aid;                  // 결제 승인 번호
    private Timestamp createdAt;         // 생성 일시
    private Timestamp approvedAt;        // 결제 승인 시간

    // 예약 관련 정보
    private Long bookingInfoId;          // 예약 ID
    private String bookingStatus;        // 예약 상태
    private Timestamp bookingDate;       // 예약 일시

    // 포토그래퍼 정보
    private Long photographerId;         // 포토그래퍼 ID
    private String photographerName;     // 포토그래퍼 이름
    private String photographerEmail;    // 포토그래퍼 이메일

    // 서비스 정보
    private String serviceTitle;         // 서비스 제목
    private String serviceDescription;   // 서비스 설명
    private String serviceLocation;      // 촬영 장소

    // 사용자 정보
    private Long userId;                 // 사용자 ID
    private String userName;             // 사용자 이름
    private String userEmail;            // 사용자 이메일

    // Payment 엔티티로부터 기본 정보만 포함한 PaymentDetailResponse 생성
    public static PaymentDetailResponse of(Payment payment) {
        return PaymentDetailResponse.builder()
                .paymentId(payment.getPaymentId())
                .tid(payment.getTid())
                .partnerOrderId(payment.getPartnerOrderId())
                .partnerUserId(payment.getPartnerUserId())
                .itemName(payment.getItemName())
                .totalAmount(payment.getTotalAmount())
                .vatAmount(payment.getVatAmount())
                .taxFreeAmount(payment.getTaxFreeAmount())
                .status(payment.getStatus())
                .paymentMethodType(payment.getPaymentMethodType())
                .aid(payment.getAid())
                .createdAt(payment.getCreatedAt())
                .approvedAt(payment.getApprovedAt())
                .build();
    }

    // 전체 정보를 포함한 PaymentDetailResponse 생성 (조인 쿼리 결과 사용)
    public static PaymentDetailResponse ofFull(Payment payment,
                                               Long bookingInfoId, String bookingStatus, Timestamp bookingDate,
                                               Long photographerId, String photographerName, String photographerEmail,
                                               String serviceTitle, String serviceDescription, String serviceLocation,
                                               Long userId, String userName, String userEmail) {
        PaymentDetailResponse response = of(payment);

        // 예약 정보 설정
        response.setBookingInfoId(bookingInfoId);
        response.setBookingStatus(bookingStatus);
        response.setBookingDate(bookingDate);

        // 포토그래퍼 정보 설정
        response.setPhotographerId(photographerId);
        response.setPhotographerName(photographerName);
        response.setPhotographerEmail(photographerEmail);

        // 서비스 정보 설정
        response.setServiceTitle(serviceTitle);
        response.setServiceDescription(serviceDescription);
        response.setServiceLocation(serviceLocation);

        // 사용자 정보 설정
        response.setUserId(userId);
        response.setUserName(userName);
        response.setUserEmail(userEmail);

        return response;
    }

    // 결제 상태에 따른 한글 설명 반환
    public String getStatusDescription() {
        return status != null ? status.getDescription() : "";
    }

    // 결제 승인 여부 확인
    public boolean isApproved() {
        return PaymentStatus.APPROVED.equals(status);
    }

    // 환불 가능 여부 확인 (승인된 결제만 환불 가능)
    public boolean isRefundable() {
        return isApproved() && aid != null && !aid.trim().isEmpty();
    }

    // 실제 결제 금액 (총액 - 비과세)
    public int getTaxableAmount() {
        return totalAmount - taxFreeAmount;
    }
}
