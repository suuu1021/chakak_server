package com.green.chakak.chakak.booking.booking_cancel_info;

import lombok.Getter;

@Getter
public class BookingCancelInfoRequest {

    //단일 조회


    @Getter
    public static class BookingCancelInfoListRequest {
        private Long userId;
        private Long photographerId;
        private int page = 0;
        private int size = 10;
        private String sort = "createdAt,desc";
    }
}