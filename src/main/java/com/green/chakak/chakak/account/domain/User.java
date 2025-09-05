package com.green.chakak.chakak.account.domain;

import com.green.chakak.chakak.photographer.domain.PhotographerProfile;
import com.green.chakak.chakak.photo.domain.PhotoServiceReview;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "user_account")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Builder
    public User(String password, String email, UserType userType, UserStatus status, boolean emailVerified) {
        this.password = password;
        this.email = email;
        this.userType = userType;
        this.status = status;
        this.emailVerified = emailVerified;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(length = 255, nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_type_id", nullable = false)
    private UserType userType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status; // ACTIVE / INACTIVE / PENDING / SUSPENDED

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt;

    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private UserProfile userProfile;

    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private PhotographerProfile photographerProfile;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<PhotoServiceReview> photoServiceReviews = new java.util.ArrayList<>();

    public enum UserStatus {
        ACTIVE,
        INACTIVE,
        PENDING,
        SUSPENDED
    }

    public void changeEmail(String newEmail) {
        this.email = newEmail;
    }

    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    // 이메일 인증 완료 처리
    public void completeEmailVerification() {
        this.emailVerified = true;
        this.status = UserStatus.ACTIVE;
    }
}
