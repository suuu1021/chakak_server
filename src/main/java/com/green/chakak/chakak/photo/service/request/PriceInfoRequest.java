package com.green.chakak.chakak.photo.service.request;

import com.green.chakak.chakak.photo.domain.PhotoServiceInfo;
import com.green.chakak.chakak.photo.domain.PriceInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class PriceInfoRequest {
    // 가격 정보 생성 요청 DTO
    @Data
    @NoArgsConstructor
    public static class CreateDTO {

        @NotNull(message = "참가자 수는 필수입니다")
        @Min(value = 1, message = "참가자 수는 1명 이상이어야 합니다")
        private Integer participantCount;

        @NotNull(message = "촬영 시간은 필수입니다")
        @Min(value = 1, message = "촬영 시간은 1분 이상이어야 합니다")
        private Integer shootingDuration;

        @NotNull(message = "의상 변경 횟수는 필수입니다")
        @Min(value = 0, message = "의상 변경 횟수는 0회 이상이어야 합니다")
        private Integer outfitChanges;

        @NotBlank(message = "특수 장비 정보는 필수입니다")
        private String specialEquipment;

        @NotNull(message = "메이크업 서비스 여부는 필수입니다")
        private Boolean isMakeupService;

        @Builder
        public CreateDTO(Integer participantCount, Integer shootingDuration, Integer outfitChanges, String specialEquipment, Boolean isMakeupService) {
            this.participantCount = participantCount;
            this.shootingDuration = shootingDuration;
            this.outfitChanges = outfitChanges;
            this.specialEquipment = specialEquipment;
            this.isMakeupService = isMakeupService;
        }

        public PriceInfo toEntity(PhotoServiceInfo photoServiceInfo) {
            return PriceInfo.builder()
                    .photoServiceInfo(photoServiceInfo)
                    .participantCount(this.participantCount)
                    .shootingDuration(this.shootingDuration)
                    .outfitChanges(this.outfitChanges)
                    .specialEquipment(this.specialEquipment)
                    .isMakeupService(this.isMakeupService)
                    .build();
        }
    }

    // 가격 정보 수정 요청 DTO
    @Data
    @NoArgsConstructor
    public static class UpdateDTO {

        @NotNull(message = "가격 정보 ID는 필수입니다")
        private Long priceInfoId;

        @NotNull(message = "참가자 수는 필수입니다")
        @Min(value = 1, message = "참가자 수는 1명 이상이어야 합니다")
        private Integer participantCount;

        @NotNull(message = "촬영 시간은 필수입니다")
        @Min(value = 1, message = "촬영 시간은 1분 이상이어야 합니다")
        private Integer shootingDuration;

        @NotNull(message = "의상 변경 횟수는 필수입니다")
        @Min(value = 0, message = "의상 변경 횟수는 0회 이상이어야 합니다")
        private Integer outfitChanges;

        @NotBlank(message = "특수 장비 정보는 필수입니다")
        private String specialEquipment;

        @NotNull(message = "메이크업 서비스 여부는 필수입니다")
        private Boolean isMakeupService;

        @Builder
        public UpdateDTO(Long priceInfoId, Integer participantCount, Integer shootingDuration, Integer outfitChanges, String specialEquipment, Boolean isMakeupService) {
            this.priceInfoId = priceInfoId;
            this.participantCount = participantCount;
            this.shootingDuration = shootingDuration;
            this.outfitChanges = outfitChanges;
            this.specialEquipment = specialEquipment;
            this.isMakeupService = isMakeupService;
        }
    }
}