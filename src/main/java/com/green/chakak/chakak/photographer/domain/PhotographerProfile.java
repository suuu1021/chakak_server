package com.green.chakak.chakak.photographer.domain;

import com.green.chakak.chakak.account.domain.User;
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
    private Long photographerId;

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
    private Timestamp updateAt;


    @Builder
    public PhotographerProfile(User user, String businessName, String introduction,
                               String location, Integer experienceYears, String status) {
        this.user = user;
        this.businessName = businessName;
        this.introduction = introduction;
        this.location = location;
        this.experienceYears = experienceYears;
        this.status = status;
    }

//    public String getTime() {
//        return MydateUtil.timestampFormat(compScrapDate);
//    }

    public void  changeStatus(String newStatus) {
        this.status = newStatus;
    }

    public void update(String businessName, String introduction,
                       String location, Integer experienceYears, String status) {
        if (businessName != null) {
            this.businessName = businessName;
        }
        if (introduction != null) {
            this.introduction = introduction;
        }
        if (location != null) {
            this.location = location;
        }
        if (experienceYears != null) {
            this.experienceYears = experienceYears;
        }
        if (status != null) {
            this.status = status;
        }
    }
}
