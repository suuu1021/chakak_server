package com.green.chakak.chakak.booking.service.response;

import com.green.chakak.chakak.booking.domain.BookingInfo;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

public class BookingInfoResponse {


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
            this.photographerCategoryId = bookingInfo.getPhotographerCategory().getCategoryId();
            this.photoServiceInfoId = bookingInfo.getPhotoServiceInfo().getServiceId();
            this.bookingDate = bookingInfo.getBookingDate();
            this.bookingTime = bookingInfo.getBookingTime();
            this.location = bookingInfo.getLocation();
            this.budget = bookingInfo.getBudget();
            this.specialRequest = bookingInfo.getSpecialRequest();
            this.status = bookingInfo.getStatus();
            this.participantCount = bookingInfo.getParticipantCount();
            this.shootingDuration = bookingInfo.getShootingDuration();
        }
    }


    // 예약 리스트(유저 입장)
    @Data
    public static class BookingUserListDTO{
        private Long photographerProfileId;
        private LocalDate bookingDate;
        private LocalTime bookingTime;
        private String location;
        private int budget;

        public BookingUserListDTO(BookingInfo bookingInfo){
         this.photographerProfileId = bookingInfo.getPhotographerProfile().getPhotographerProfileId();
         this.bookingDate = bookingInfo.getBookingDate();
         this.bookingTime = bookingInfo.getBookingTime();
         this.location = bookingInfo.getLocation();
         this.budget = bookingInfo.getBudget();
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
            this.location = bookingInfo.getLocation();
            this.budget = bookingInfo.getBudget();
        }
    }

    // 예약 상세보기
    public static class BookingDetailDTO{
        private Long photographerProfileId;
        private Long userProfileId;
        private LocalDate bookingDate;
        private LocalTime bookingTime;
        private String location;
        private int budget;
        private int participantCount;
        private int shootingDuration;
        private String specialRequest;

        public BookingDetailDTO(BookingInfo bookingInfo) {
            this.photographerProfileId = bookingInfo.getPhotographerProfile().getPhotographerProfileId();
            this.userProfileId = bookingInfo.getUserProfile().getUserProfileId();
            this.bookingDate = bookingInfo.getBookingDate();
            this.bookingTime = bookingInfo.getBookingTime();
            this.location = bookingInfo.getLocation();
            this.budget = bookingInfo.getBudget();
            this.participantCount = bookingInfo.getParticipantCount();
            this.shootingDuration = bookingInfo.getShootingDuration();
            this.specialRequest = bookingInfo.getSpecialRequest();
        }
    }


}