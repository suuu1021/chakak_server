package com.green.chakak.chakak.booking.service.response;

import com.green.chakak.chakak.booking.domain.BookingInfo;
import com.green.chakak.chakak.booking.domain.BookingStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

public class BookingInfoResponse {

    /**/
    @Data
    public static class SaveDTO{
        private Long bookingInfoId;
        private Long userProfileId;
        private Long photographerProfileId;
        private Long photographerCategoryId;
        private Long photoServiceInfoId;
        private LocalDate bookingDate;
        private LocalTime bookingTime;
        private String location;
        private int budget;
        private String specialRequest;
        private String status;
        private int participantCount;
        private int shootingDuration;

        public SaveDTO(BookingInfo bookingInfo) {
            this.bookingInfoId = bookingInfo.getBookingInfoId();
            this.userProfileId = bookingInfo.getUserProfile().getUserProfileId();
            this.photographerProfileId = bookingInfo.getPhotographerProfile().getPhotographerProfileId();
            this.photoServiceInfoId = bookingInfo.getPhotoServiceInfo().getServiceId();
            this.bookingDate = bookingInfo.getBookingDate();
            this.bookingTime = bookingInfo.getBookingTime();
            this.status = bookingInfo.getStatus().name();
        }
    }

    // 예약 리스트(유저 입장)
    @Data
    public static class BookingUserListDTO{
        private Long bookingInfoId;
        private Long photoServiceInfoId;
        private String photoServiceImageData;
        private String title;
        private String photographerNickname;
        private double review;
        private BookingStatus status;
        private int price;
        private LocalDate bookingDate;
        private LocalTime bookingTime;

        public BookingUserListDTO(BookingInfo bookingInfo){
            this.bookingInfoId = bookingInfo.getBookingInfoId();
            this.photoServiceInfoId = bookingInfo.getPhotoServiceInfo().getServiceId();
            this.photoServiceImageData = bookingInfo.getPhotographerProfile().getProfileImageUrl();
            this.title = bookingInfo.getPhotoServiceInfo().getTitle();
            this.photographerNickname = bookingInfo.getPhotographerProfile().getBusinessName();
            // TODO - review 작업 필요
            this.review = 0.0;
            this.status = bookingInfo.getStatus();
            this.price = bookingInfo.getPriceInfo().getPrice();
            this.bookingDate = bookingInfo.getBookingDate();
            this.bookingTime = bookingInfo.getBookingTime();
        }
    }

    // 예약 리스트(프토그래퍼 입장)
    @Data
    public static class BookingPhotographerListDTO{
        private Long userProfileId;
        private LocalDate bookingDate;
        private LocalTime bookingTime;
        private String location;
        private int budget;

        public BookingPhotographerListDTO(BookingInfo bookingInfo) {
            this.userProfileId = bookingInfo.getUserProfile().getUserProfileId();
            this.bookingDate = bookingInfo.getBookingDate();
            this.bookingTime = bookingInfo.getBookingTime();
        }
    }

    // 예약 상세보기
    @Data
    public static class BookingDetailDTO{
        private Long photographerProfileId;
        private Long userProfileId;
        private String photoServiceTitle;
        private String priceInfoTitle;
        private int price;
        private LocalDate bookingDate;
        private LocalTime bookingTime;
        private BookingStatus status;

        public BookingDetailDTO(BookingInfo bookingInfo) {
            this.photographerProfileId = bookingInfo.getPhotographerProfile().getPhotographerProfileId();
            this.userProfileId = bookingInfo.getUserProfile().getUserProfileId();
            this.photoServiceTitle = bookingInfo.getPhotoServiceInfo().getTitle();
            this.priceInfoTitle = bookingInfo.getPriceInfo().getTitle();
            this.price = bookingInfo.getPriceInfo().getPrice();
            this.bookingDate = bookingInfo.getBookingDate();
            this.bookingTime = bookingInfo.getBookingTime();
            this.status = bookingInfo.getStatus();
        }
    }


}