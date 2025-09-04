package com.green.chakak.chakak.account.user_profile;

import com.green.chakak.chakak.account.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

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

//    @Column(length = 500)
//    private String profileImageURL;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    @Builder
    public UserProfile(Long userProfileId, User user, String nickName, String introduce, Timestamp createdAt, Timestamp updatedAt) {
        this.UserProfileId = userProfileId;
        this.user = user;
        this.nickName = nickName;
        this.introduce = introduce;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    public void update(String nickName, String introduce){
        this.nickName = nickName;
        this.introduce = introduce;

    }
}
