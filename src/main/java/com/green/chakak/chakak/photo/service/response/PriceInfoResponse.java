package com.green.chakak.chakak.photo.service.response;

import com.green.chakak.chakak.photo.domain.PriceInfo;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

public class PriceInfoResponse {

    // 가격 정보 목록 응답 DTO
    @Data
    @NoArgsConstructor
    public static class PriceInfoListDTO {
        private Long priceInfoId;
        private Long photoServiceInfoId;
        private int participantCount;
        private int shootingDuration;
        private int outfitChanges;
        private String specialEquipment;
        private boolean isMakeupService;
        private Timestamp createdAt;
        private Timestamp updatedAt;

        @Builder
        public PriceInfoListDTO(PriceInfo priceInfo) {
            this.priceInfoId = priceInfo.getPriceInfoId();
            this.photoServiceInfoId = priceInfo.getPhotoServiceInfo().getServiceId();
            this.participantCount = priceInfo.getParticipantCount();
            this.shootingDuration = priceInfo.getShootingDuration();
            this.outfitChanges = priceInfo.getOutfitChanges();
            this.specialEquipment = priceInfo.getSpecialEquipment();
            this.isMakeupService = priceInfo.isMakeupService();
            this.createdAt = priceInfo.getCreatedAt();
            this.updatedAt = priceInfo.getUpdatedAt();
        }
    }

    // 가격 정보 상세 응답 DTO
    @Data
    @NoArgsConstructor
    public static class PriceInfoDetailDTO {
        private Long priceInfoId;
        private PhotoServiceResponse.PhotoServiceDetailDTO photoService;
        private int participantCount;
        private int shootingDuration;
        private int outfitChanges;
        private String specialEquipment;
        private boolean isMakeupService;
        private Timestamp createdAt;
        private Timestamp updatedAt;

        @Builder
        public PriceInfoDetailDTO(PriceInfo priceInfo) {
            this.priceInfoId = priceInfo.getPriceInfoId();
            this.photoService = new PhotoServiceResponse.PhotoServiceDetailDTO(priceInfo.getPhotoServiceInfo());
            this.participantCount = priceInfo.getParticipantCount();
            this.shootingDuration = priceInfo.getShootingDuration();
            this.outfitChanges = priceInfo.getOutfitChanges();
            this.specialEquipment = priceInfo.getSpecialEquipment();
            this.isMakeupService = priceInfo.isMakeupService();
            this.createdAt = priceInfo.getCreatedAt();
            this.updatedAt = priceInfo.getUpdatedAt();
        }
    }

    // 가격 정보 목록 컨테이너 DTO
    @Data
    @NoArgsConstructor
    public static class PriceInfosDTO {
        private Long photoServiceInfoId;
        private List<PriceInfoListDTO> priceInfoList;
        private int totalCount;

        @Builder
        public PriceInfosDTO(Long photoServiceInfoId, List<PriceInfo> priceInfoList) {
            this.photoServiceInfoId = photoServiceInfoId;
            this.priceInfoList = priceInfoList.stream()
                    .map(PriceInfoListDTO::new)
                    .collect(Collectors.toList());
            this.totalCount = priceInfoList.size();
        }
    }
}