package com.green.chakak.chakak.payment.repository.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UserPaymentStatsResponse {
    // 전체 결제 통계
    private int totalPaymentCount;           // 총 결제 건수
    private long totalPaymentAmount;         // 총 결제 금액
    private int successPaymentCount;         // 성공한 결제 건수
    private long successPaymentAmount;       // 성공한 결제 금액
    private int failedPaymentCount;          // 실패한 결제 건수
    private int canceledPaymentCount;        // 취소한 결제 건수

    // 이번 달 결제 통계
    private int thisMonthPaymentCount;       // 이번 달 결제 건수
    private long thisMonthPaymentAmount;     // 이번 달 결제 금액

    // 지난 달 결제 통계
    private int lastMonthPaymentCount;       // 지난 달 결제 건수
    private long lastMonthPaymentAmount;     // 지난 달 결제 금액

    // 평균 결제 정보
    private long averagePaymentAmount;       // 평균 결제 금액

    // 첫 결제 및 최근 결제 일자
    private LocalDate firstPaymentDate;     // 첫 결제 일자
    private LocalDate lastPaymentDate;      // 최근 결제 일자

    // 계산된 필드들을 위한 메서드

    // 결제 성공률 계산 (백분율)
    public double getSuccessRate() {
        if (totalPaymentCount == 0) {
            return 0.0;
        }
        return (double) successPaymentCount / totalPaymentCount * 100.0;
    }

    // 이번 달 대비 지난 달 증감률 계산 (백분율)
    public double getMonthlyGrowthRate() {
        if (lastMonthPaymentAmount == 0) {
            return thisMonthPaymentAmount > 0 ? 100.0 : 0.0;
        }
        return ((double) (thisMonthPaymentAmount - lastMonthPaymentAmount) / lastMonthPaymentAmount) * 100.0;
    }

    // 이번 달이 지난 달보다 증가했는지 여부
    public boolean isThisMonthIncreased() {
        return thisMonthPaymentAmount > lastMonthPaymentAmount;
    }

    // 결제 실패율 계산 (백분율)
    public double getFailureRate() {
        if (totalPaymentCount == 0) {
            return 0.0;
        }
        return (double) (failedPaymentCount + canceledPaymentCount) / totalPaymentCount * 100.0;
    }

    // 월 평균 결제 금액 계산 (첫 결제부터 현재까지의 월 평균)
    public long getMonthlyAverageAmount() {
        if (firstPaymentDate == null || successPaymentAmount == 0) {
            return 0;
        }

        LocalDate now = LocalDate.now();
        long monthsBetween = java.time.temporal.ChronoUnit.MONTHS.between(firstPaymentDate, now) + 1;

        return successPaymentAmount / monthsBetween;
    }

    // 통계 요약 정보 반환
    public String getSummary() {
        return String.format(
                "총 %d건 결제, 성공률 %.1f%%, 총 결제금액 %,d원",
                totalPaymentCount, getSuccessRate(), successPaymentAmount
        );
    }
}
