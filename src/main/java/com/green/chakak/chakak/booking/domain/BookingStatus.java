package com.green.chakak.chakak.booking.domain;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BookingStatus {
    PENDING("예약대기"),
    CONFIRMED("예약확정"),
    COMPLETED("촬영완료"),
    REVIEWED("리뷰작성완료"),
    CANCELED("예약취소");

    private final String description;
    private List<BookingStatus> nextStatus;

    static {
        PENDING.nextStatus = Arrays.asList(CONFIRMED, CANCELED);
        CONFIRMED.nextStatus = Collections.singletonList(COMPLETED);
        COMPLETED.nextStatus = Collections.singletonList(REVIEWED);
        REVIEWED.nextStatus = Collections.emptyList();
        CANCELED.nextStatus = Collections.emptyList();
    }

    /**
     * 현재 상태에서 다음 상태로 변경이 가능한지 확인하는 메서드
     */
    public boolean canChangeTo(BookingStatus nextStatusToChange) {
        return this.nextStatus.contains(nextStatusToChange);
    }
}