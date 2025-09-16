package com.green.chakak.chakak.admin.service.response;

import com.green.chakak.chakak.account.domain.User;
import com.green.chakak.chakak.account.domain.UserProfile;
import com.green.chakak.chakak.account.domain.UserType;
import com.green.chakak.chakak.account.service.response.UserResponse;
import com.green.chakak.chakak.admin.domain.Admin;
import com.green.chakak.chakak.admin.domain.AdminProfile;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Data
public class AdminResponse {


    @Data
    @Builder
    public static class AdminUserListDto {

        private Long userId;
        private String email;
        private String status;
        private boolean emailVerified;

        // 프로필 최소 정보
        private String nickName;


        public static AdminUserListDto from(User user) {
            return AdminUserListDto.builder()
                    .userId(user.getUserId())
                    .email(user.getEmail())
                    .status(user.getStatus().name())
                    .emailVerified(user.isEmailVerified())
                    .nickName(user.getUserProfile() != null ? user.getUserProfile().getNickName() : null)
                    .build();
        }
    }

    @Builder
    @Data
    public static class AdminUserDetailDto {

        private Long userId;
        private String email;
        private String status;
        private boolean emailVerified;
        private Timestamp createdAt;
        private Timestamp updatedAt;

        // Profile 전체
        private Long userProfileId;
        private String nickName;
        private String introduce;
        private String imageData;


        public static AdminUserDetailDto from(User user) {
            UserProfile profile = user.getUserProfile();

            return AdminUserDetailDto.builder()
                    .userId(user.getUserId())
                    .email(user.getEmail())
                    .status(user.getStatus().name())
                    .emailVerified(user.isEmailVerified())
                    .createdAt(user.getCreatedAt())
                    .updatedAt(user.getUpdatedAt())
                    .userProfileId(profile != null ? profile.getUserProfileId() : null)
                    .nickName(profile != null ? profile.getNickName() : null)
                    .introduce(profile != null ? profile.getIntroduce() : null)
                    .imageData(profile != null ? profile.getImageData() : null)
                    .build();
        }
    }

    @Builder
    @Data
    public static class UserListResponseDto {
        private Long userId;
        private String email;
        private String status;
        private UserType userType;
        private Timestamp createdAt;
        private Timestamp updatedAt;

        public static UserListResponseDto from(User user) {

            return UserListResponseDto.builder()
                    .userId(user.getUserId())
                    .email(user.getEmail())
                    .status(user.getStatus().name())
                    .userType(user.getUserType())
                    .createdAt(user.getCreatedAt())
                    .updatedAt(user.getUpdatedAt())
                    .build();
        }


    }

    @Builder
    @Data
    public static class AdminLoginDto {

        private String tokenType;
        private String accessToken;
        private Long adminId;
        private String adminName;

        public static AdminLoginDto adminSet(Admin admin, String token, String adminName) {
            return AdminLoginDto.builder()
                    .adminId(admin.getAdminId())
                    .adminName(adminName)
                    .tokenType("Bearer")
                    .accessToken(token)
                    .build();
        }


    }


}
