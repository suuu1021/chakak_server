package com.green.chakak.chakak.photographer.domain;

import com.green.chakak.chakak.account.domain.User;
import com.green.chakak.chakak.photographer.service.request.PhotographerRequest;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@NoArgsConstructor
@Data
@Table(name = "photographer_profile")
@Entity
public class PhotographerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long photographerProfileId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String businessName; // 상호명

    @Column(columnDefinition = "TEXT")
    private String introduction; // 소개글

    private String location; // 활동 지역

    private Integer experienceYears; // 경력 연수

    @Column(nullable = false)
    private String status; // 상태(활성/비활성)

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    @Lob
    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Builder
    public PhotographerProfile(User user, String businessName, String introduction,
                               String location, Integer experienceYears, String status, String profileImageUrl) {
        this.user = user;
        this.businessName = businessName;
        this.introduction = introduction;
        this.location = location;
        this.experienceYears = experienceYears;
        this.status = status;
        this.profileImageUrl = profileImageUrl;
    }

//    public String getTime() {
//        return MydateUtil.timestampFormat(compScrapDate);
//    }

    public void  changeStatus(String newStatus) {
        this.status = newStatus;
    }

    public void update(PhotographerRequest.UpdateProfile updateProfile) {
        if (updateProfile.getBusinessName() != null) {
            this.businessName = updateProfile.getBusinessName();
        }
        if ( updateProfile.getIntroduction() != null) {
            this.introduction = updateProfile.getIntroduction();
        }
        if (updateProfile.getLocation() != null) {
            this.location = updateProfile.getLocation();
        }
        if (updateProfile.getExperienceYears() != null) {
            this.experienceYears = updateProfile.getExperienceYears();
        }
        if (updateProfile.getStatus() != null) {
            this.status = updateProfile.getStatus();
        }
        if (updateProfile.getProfileImageUrl() != null) {
            this.profileImageUrl = updateProfile.getProfileImageUrl();
        }
    }
}
