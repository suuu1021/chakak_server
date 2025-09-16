package com.green.chakak.chakak.photographer.service.response;

import com.green.chakak.chakak.photographer.domain.PhotographerCategory;
import com.green.chakak.chakak.photographer.domain.PhotographerMap;
import com.green.chakak.chakak.photographer.domain.PhotographerProfile;
import lombok.Data;

import java.util.List;

public class PhotographerResponse {

    @Data
    public static class SaveDTO {
        private Long photographerId;
        private Long userId;
        private String businessName;
        private String introduction;
        private String location;
        private Integer experienceYears;
        private String status;
        private String profileImageUrl;
        private List<CategoryDTO> categories;

        public SaveDTO(PhotographerProfile profile, List<PhotographerMap> maps) {
            this.photographerId = profile.getPhotographerProfileId();
            this.userId = profile.getUser().getUserId();
            this.businessName = profile.getBusinessName();
            this.introduction = profile.getIntroduction();
            this.location = profile.getLocation();
            this.experienceYears = profile.getExperienceYears();
            this.status = profile.getStatus();
            this.profileImageUrl = profile.getProfileImageUrl();
            // 전달받은 매핑 목록(maps)을 CategoryDTO 리스트로 변환
            this.categories = maps.stream()
                    .map(map -> new CategoryDTO(map.getPhotographerCategory()))
                    .collect(java.util.stream.Collectors.toList());
        }
    }

    @Data
    public static class UpdateDTO {
        private String businessName;
        private String introduction;
        private String location;
        private Integer experienceYears;
        private String status;
        private String profileImageUrl;
        private List<CategoryDTO> categories;

        public UpdateDTO(PhotographerProfile profile, List<PhotographerMap> maps) {
            this.businessName = profile.getBusinessName();
            this.introduction = profile.getIntroduction();
            this.location = profile.getLocation();
            this.experienceYears = profile.getExperienceYears();
            this.status = profile.getStatus();
            this.profileImageUrl = profile.getProfileImageUrl();
            // 전달받은 매핑 목록(maps)을 CategoryDTO 리스트로 변환
            this.categories = maps.stream()
                    .map(map -> new CategoryDTO(map.getPhotographerCategory()))
                    .collect(java.util.stream.Collectors.toList());
        }
    }

    @Data
    public static class DetailDTO {
        private Long photographerId;
        private Long userId;
        private String businessName;
        private String introduction;
        private String location;
        private Integer experienceYears;
        private String status;
        private String profileImageUrl;
        private List<CategoryDTO> categories;

        public DetailDTO(PhotographerProfile profile, List<PhotographerMap> maps) {
            this.photographerId = profile.getPhotographerProfileId();
            this.userId = profile.getUser().getUserId();
            this.businessName = profile.getBusinessName();
            this.introduction = profile.getIntroduction();
            this.location = profile.getLocation();
            this.experienceYears = profile.getExperienceYears();
            this.status = profile.getStatus();
            this.profileImageUrl = profile.getProfileImageUrl();
            // 전달받은 매핑 목록(maps)을 CategoryDTO 리스트로 변환
            this.categories = maps.stream()
                    .map(map -> new CategoryDTO(map.getPhotographerCategory()))
                    .collect(java.util.stream.Collectors.toList());
        }
    }

    @Data
    public static class ListDTO {
        private Long photographerId;
        private String businessName;
        private String location;
        private Integer experienceYears;
        private String status;
        private String imageUrl;

        public ListDTO(PhotographerProfile profile) {
            this.photographerId = profile.getPhotographerProfileId();
            this.businessName = profile.getBusinessName();
            this.location = profile.getLocation();
            this.experienceYears = profile.getExperienceYears();
            this.status = profile.getStatus();
            this.imageUrl = profile.getProfileImageUrl();
        }
    }

    @Data
    public static class CategoryDTO {
        private final Long categoryId;
        private final String categoryName;

        public CategoryDTO(PhotographerCategory category) {
            this.categoryId = category.getCategoryId();
            this.categoryName = category.getCategoryName();
        }
    }

    @Data
    public static class mapDTO {
        private Long mappingId;
        private Long photographerId;
        private Long categoryId;
        private String categoryName;

        public mapDTO(PhotographerMap map) {
            this.mappingId = map.getMappingId();
            this.photographerId = map.getPhotographerProfile().getPhotographerProfileId();
            PhotographerCategory category = map.getPhotographerCategory();
            this.categoryId = category.getCategoryId();
            this.categoryName = category.getCategoryName();
        }
    }
}
