package com.green.chakak.chakak.booking.service.request;

import com.green.chakak.chakak.account.domain.UserProfile;
import com.green.chakak.chakak.booking.domain.BookingInfo;
import com.green.chakak.chakak.booking.domain.BookingStatus;
import com.green.chakak.chakak.photo.domain.PhotoServiceInfo;
import com.green.chakak.chakak.photo.domain.PriceInfo;
import com.green.chakak.chakak.photographer.domain.PhotographerCategory;
import com.green.chakak.chakak.photographer.domain.PhotographerProfile;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

public class BookingInfoRequest {

    @Data
    public static class CreateDTO{

        private Long userProfileId;  // 유저 프로필 ID
        private Long photographerProfileId;   // 포토그래퍼 프로필 ID
        private Long photoServiceId;   // 서비스 ID
        private Long priceInfoId; // 가격 정보 ID

        @NotNull(message = "촬영 희망일을 선택하세요")
        private LocalDate bookingDate;             // 촬영 희망일

        @NotNull(message = "촬영 희망시간을 선택하세요")
        private LocalTime bookingTime;             // 촬영 희망 시간

        public BookingInfo toEntity(UserProfile userProfile, PhotographerProfile photographerProfile,
                                    PriceInfo priceInfo,
                                    PhotoServiceInfo photoServiceInfo){
         return BookingInfo.builder()
                 .userProfile(userProfile)                         // 로그인 유저
                 .photographerProfile(photographerProfile)         // 포토그래퍼
                 .photoServiceInfo(photoServiceInfo)         // 서비스
                 .priceInfo(priceInfo)
                 .payment(null)       // 가격정보
                 .bookingDate(this.bookingDate)                        // DTO 값
                 .bookingTime(this.bookingTime)                        // DTO 값
                 .status(BookingStatus.PENDING)                               // 예약 초기 상태 (기본값)
                 .build();
        }


    }


}
