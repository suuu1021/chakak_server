package com.green.chakak.chakak.payment.repository.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class PaymentIncomeStatsResponse {
    // 전체 수익 통계
    private int totalBookingCount;           // 총 예약 건수
    private long totalIncomeAmount;          // 총 수익 금액 (수수료 제외 전)
    private long totalNetIncomeAmount;       // 총 실수익 금액 (수수료 제외 후)
    private long totalPlatformFeeAmount;     // 총 플랫폼 수수료 금액
    private int averagePlatformFeeRate;      // 평균 플랫폼 수수료율

    // 이번 달 수익 통계
    private int thisMonthBookingCount;       // 이번 달 예약 건수
    private long thisMonthIncomeAmount;      // 이번 달 수익 금액
    private long thisMonthNetIncomeAmount;   // 이번 달 실수익 금액

    // 지난 달 수익 통계
    private int lastMonthBookingCount;       // 지난 달 예약 건수
    private long lastMonthIncomeAmount;      // 지난 달 수익 금액
    private long lastMonthNetIncomeAmount;   // 지난 달 실수익 금액

    // 평균 수익 정보
    private long averageIncomePerBooking;    // 예약 당 평균 수익
    private long averageNetIncomePerBooking; // 예약 당 평균 실수익

    // 최고/최저 수익 정보
    private long highestIncomeAmount;        // 최고 수익 금액
    private long lowestIncomeAmount;         // 최저 수익 금액
    private LocalDate highestIncomeDate;     // 최고 수익 발생 일자

    // 활동 기간 정보
    private LocalDate firstBookingDate;      // 첫 예약 일자
    private LocalDate lastBookingDate;       // 최근 예약 일자
    private int activeDays;                  // 활동 일수

    // 고객 관련 통계
    private int uniqueCustomerCount;         // 고유 고객 수
    private int repeatCustomerCount;         // 재방문 고객 수

    // 계산된 필드들을 위한 메서드

    // 실제 수수료율 계산 (전체 수익 대비)
    public double getActualFeeRate() {
        if (totalIncomeAmount == 0) {
            return 0.0;
        }
        return ((double) totalPlatformFeeAmount / totalIncomeAmount) * 100.0;
    }

    // 이번 달 대비 지난 달 수익 증감률 계산 (실수익 기준)
    public double getMonthlyGrowthRate() {
        if (lastMonthNetIncomeAmount == 0) {
            return thisMonthNetIncomeAmount > 0 ? 100.0 : 0.0;
        }
        return ((double) (thisMonthNetIncomeAmount - lastMonthNetIncomeAmount) / lastMonthNetIncomeAmount) * 100.0;
    }

    // 이번 달이 지난 달보다 증가했는지 여부
    public boolean isThisMonthIncreased() {
        return thisMonthNetIncomeAmount > lastMonthNetIncomeAmount;
    }

    // 일 평균 수익 계산
    public long getDailyAverageIncome() {
        if (activeDays == 0) {
            return 0;
        }
        return totalNetIncomeAmount / activeDays;
    }

    // 월 평균 수익 계산
    public long getMonthlyAverageIncome() {
        if (firstBookingDate == null || totalNetIncomeAmount == 0) {
            return 0;
        }

        LocalDate now = LocalDate.now();
        long monthsBetween = java.time.temporal.ChronoUnit.MONTHS.between(firstBookingDate, now) + 1;

        return totalNetIncomeAmount / monthsBetween;
    }

    // 고객 재방문율 계산
    public double getCustomerRetentionRate() {
        if (uniqueCustomerCount == 0) {
            return 0.0;
        }
        return ((double) repeatCustomerCount / uniqueCustomerCount) * 100.0;
    }

    // 예약 전환율 계산 (전체 예약 대비 완료된 예약)
    public double getBookingCompletionRate() {
        if (totalBookingCount == 0) {
            return 0.0;
        }
        // 실제로는 완료된 예약 수를 별도로 받아야 하지만, 여기서는 수익이 발생한 예약을 완료된 것으로 간주
        return 100.0; // 수익 통계는 이미 완료된 예약만 집계하므로
    }

    // 수익 집중도 계산 (최고 수익이 평균 수익의 몇 배인지)
    public double getIncomeConcentrationRatio() {
        if (averageIncomePerBooking == 0) {
            return 0.0;
        }
        return (double) highestIncomeAmount / averageIncomePerBooking;
    }

    // 수익 안정성 지수 계산 (최저 수익 / 평균 수익)
    public double getIncomeStabilityIndex() {
        if (averageIncomePerBooking == 0) {
            return 0.0;
        }
        return (double) lowestIncomeAmount / averageIncomePerBooking;
    }

    // 통계 요약 정보 반환
    public String getSummary() {
        return String.format(
                "총 %d건 완료, 실수익 %,d원 (수수료 %.1f%%), 고객 재방문율 %.1f%%",
                totalBookingCount, totalNetIncomeAmount, getActualFeeRate(), getCustomerRetentionRate()
        );
    }
}