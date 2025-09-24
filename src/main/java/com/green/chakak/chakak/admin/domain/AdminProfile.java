package com.green.chakak.chakak.admin.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "admin_profile")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long profileId;

    @OneToOne
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin;

    @Column(length = 50, nullable = false, unique = true)
    private String nickName;

    @Column(name = "email")
    private String email;

    @Column(name = "image_url")
    private String imageUrl;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    @Builder
    public AdminProfile(Long profileId, String nickName, Admin admin, String email, String imageUrl ) {
        this.profileId = profileId;
        this.nickName = nickName;
        this.admin = admin;
        this.email = email;
        this.imageUrl = imageUrl;
    }


}
