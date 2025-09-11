package com.green.chakak.chakak.payment.domain;

public enum PaymentStatus {
    READY("결제준비"),           // 결제 준비 완료 상태
    APPROVED("결제승인완료"),     // 결제 승인 완료 상태
    CANCELED("결제취소"),        // 결제 취소 상태
    FAILED("결제실패");          // 결제 실패 상태

    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}