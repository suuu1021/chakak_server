package com.green.chakak.chakak.photographer.service.request;

import com.green.chakak.chakak.account.domain.User;
import com.green.chakak.chakak.photographer.domain.PhotographerProfile;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

public class PhotographerRequest {

    // 포토그래퍼 프로필 저장
    @Data
    @NoArgsConstructor
    public static class SaveProfile {

        @NotNull(message = "회원 id값은 필수 입니다.")
        private Long userId;

        @Size(max = 100, message = "상호명은 100자를 초과할 수 없습니다.")
        private String businessName;

        @Size(max = 1000, message = "소개글은 1000자를 초과할 수 없습니다.")
        private String introduction;

        @Size(max = 100, message = "활동 지역은 100자를 초과할 수 없습니다.")
        private String location;

        @Min(value = 0, message = "경력 연수는 0년 이상이어야 합니다.")
        @Max(value = 50, message = "경력 연수는 50년을 초과할 수 없습니다.")
        private Integer experienceYears;

        @NotBlank(message = "상태는 필수입니다.")
        @Pattern(regexp = "^(ACTIVE|INACTIVE)$", message = "상태는 ACTIVE 또는 INACTIVE만 가능합니다.")
        private String status;

        public PhotographerProfile toEntity(User searchUser) {
            return PhotographerProfile.builder()
                    .user(searchUser)
                    .businessName(this.businessName)
                    .introduction(this.introduction)
                    .location(this.location)
                    .experienceYears(this.experienceYears)
                    .status(this.status)
                    .build();
        }
    }

    // 포토그래퍼 프로필 수정
    @Data
    @NoArgsConstructor
    public static class UpdateProfile {

        @Size(max = 100, message = "상호명은 100자를 초과할 수 없습니다.")
        private String businessName;

        @Size(max = 1000, message = "소개글은 1000자를 초과할 수 없습니다.")
        private String introduction;

        @Size(max = 100, message = "활동 지역은 100자를 초과할 수 없습니다.")
        private String location;

        @Min(value = 0, message = "경력 연수는 0년 이상이어야 합니다.")
        @Max(value = 50, message = "경력 연수는 50년을 초과할 수 없습니다.")
        private Integer experienceYears;

        @Pattern(regexp = "^(ACTIVE|INACTIVE)$", message = "상태는 ACTIVE 또는 INACTIVE만 가능합니다.")
        private String status;
    }
}
