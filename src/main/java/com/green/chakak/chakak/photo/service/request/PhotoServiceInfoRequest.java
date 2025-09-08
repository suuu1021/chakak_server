package com.green.chakak.chakak.photo.service.request;

import com.green.chakak.chakak.photo.domain.PhotoServiceInfo;
import com.green.chakak.chakak.photographer.domain.PhotographerProfile;
import jakarta.validation.Valid;
import lombok.Data;

import java.util.List;

public class PhotoServiceInfoRequest {

    @Data
    public static class SaveDTO {
        private String title;
        private String description;
        private String imageData;
        private List<PriceInfoRequest.CreateDTO> priceInfoList;  // 가격정보 리스트
        private List<Long> categoryIdList; // 서비스 카테고리 정보 리스트


        public PhotoServiceInfo toEntity(PhotographerProfile userInfo) {
            return PhotoServiceInfo.builder()
                    .photographerProfile(userInfo)
                    .title(title)
                    .description(description)
                    .imageData(imageData)
                    .build();
        }
    }

    @Data
    public static class UpdateDTO {
        private String title;
        private String description;
        private String imageData;
        private List<PriceInfoRequest.CreateDTO> priceInfoList;  // 가격정보 리스트
        private List<Long> categoryIdList; // 서비스 카테고리 정보 리스트
    }

}