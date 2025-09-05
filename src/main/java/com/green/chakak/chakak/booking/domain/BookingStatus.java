package com.green.chakak.chakak.booking.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BookingStatus {
    PENDING("예약대기"),
    CONFIRMED("예약승낙"),
    REJECTED("예약거절"),
    CANCELED("예약취소"),
    COMPLETED("촬영완료"),
    REVIEWED("리뷰작성완료");

    private final String description;

}
