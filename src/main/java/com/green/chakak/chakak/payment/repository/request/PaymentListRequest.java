package com.green.chakak.chakak.payment.repository.request;

import com.green.chakak.chakak.payment.domain.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class PaymentListRequest {
    private PaymentStatus status;       // 결제 상태 필터
    private LocalDate startDate;        // 조회 시작 날짜
    private LocalDate endDate;          // 조회 종료 날짜
    private int page;                   // 페이지 번호 (0부터 시작)
    private int size;                   // 페이지 크기

    // 기본값 설정을 위한 생성자
    public PaymentListRequest(PaymentStatus status, LocalDate startDate, LocalDate endDate,
                              int page, int size) {
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.page = page >= 0 ? page : 0;              // 음수면 0으로 설정
        this.size = size > 0 ? size : 10;              // 0이하면 10으로 설정
    }

    // 날짜 유효성 검증 메서드
    public boolean isValidDateRange() {
        if (startDate == null || endDate == null) {
            return true; // null은 유효한 것으로 간주 (전체 조회)
        }
        return !startDate.isAfter(endDate); // 시작일이 종료일보다 늦으면 false
    }
}
