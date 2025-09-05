package com.green.chakak.chakak.booking.service.request;

import com.green.chakak.chakak.account.domain.UserProfile;
import com.green.chakak.chakak.booking.domain.BookingInfo;
import com.green.chakak.chakak.booking.domain.BookingStatus;
import com.green.chakak.chakak.photo.domain.PhotoServiceInfo;
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

        private Long photographerProfileId;   // 포토그래퍼 선택
        private Long photographerCategoryId;  // 카테고리 선택
        private Long photoServiceId;   // 서비스 선택

        @NotNull(message = "촬영 희망일을 선택하세요")
        private LocalDate bookingDate;             // 촬영 희망일

        @NotNull(message = "촬영 희망시간을 선택하세요")
        private LocalTime bookingTime;             // 촬영 희망 시간

        @NotEmpty(message = "촬영 장소를 선택하세요")
        private String location;              // 촬영 장소

        @PositiveOrZero(message = "금액은 0 이상이어야 합니다.")
        private int budget;                   // 예산

        private String specialRequest;        // 특별 요청사항

        @Min(value = 1, message = "최소 인원수는 1명 이상이어야 합니다")
        private int participantCount;         // 참여 인원수

        @Min(value = 30, message = "촬영 시간은 최소 30분 이상이어야 합니다.")
        private int shootingDuration;         // 촬영 시간(분)

        public BookingInfo toEntity(UserProfile userProfile, PhotographerProfile photographerProfile,
                                    PhotographerCategory photographerCategory,
                                    PhotoServiceInfo photoServiceInfo){
         return BookingInfo.builder()
                 .userProfile(userProfile)                         // 로그인 유저
                 .photographerProfile(photographerProfile)         // 포토그래퍼
                 .photographerCategory(photographerCategory)       // 카테고리
                 .photoServiceInfo(photoServiceInfo)         // 서비스
                 .bookingDate(this.bookingDate)                        // DTO 값
                 .bookingTime(this.bookingTime)                        // DTO 값
                 .location(this.location)                              // DTO 값
                 .budget(this.budget)                                  // DTO 값
                 .specialRequest(this.specialRequest)                  // DTO 값
                 .status(BookingStatus.PENDING)                               // 예약 초기 상태 (기본값)
                 .participantCount(this.participantCount)              // DTO 값
                 .shootingDuration(this.shootingDuration)              // DTO 값
                 .build();
        }


    }


}
