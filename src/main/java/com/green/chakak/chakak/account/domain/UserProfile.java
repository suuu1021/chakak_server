package com.green.chakak.chakak.account.domain;

import com.green.chakak.chakak.account.service.request.UserProfileRequest;
import com.green.chakak.chakak.booking.domain.BookingCancelInfo;
import com.green.chakak.chakak.booking.domain.BookingInfo;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.List;

//
@Entity
@Table(name = "user_profile")
@Data
@NoArgsConstructor
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long UserProfileId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;


    @Column(length = 50, nullable = false, unique = true)
    private String nickName;

    @Column(length = 50)
    private String introduce;

    @Column(name = "image_data")
    @Lob
    private String imageData;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<BookingInfo> bookingInfos = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<BookingCancelInfo> bookingCancelInfos = new java.util.ArrayList<>();

    @Builder
    public UserProfile(Long userProfileId, User user, String nickName, String introduce, String imageData, Timestamp createdAt, Timestamp updatedAt) {
        this.UserProfileId = userProfileId;
        this.user = user;
        this.nickName = nickName;
        this.introduce = introduce;
        this.imageData = imageData;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    public void update(UserProfileRequest.UpdateDTO dto){

        if (dto.getNickName() != null) {
            this.nickName = dto.getNickName();
        }
        if (dto.getIntroduce() != null) {
            this.introduce = dto.getIntroduce();
        }
        if (dto.getImageData() != null) {
            this.imageData = dto.getImageData();
        }
    }



}