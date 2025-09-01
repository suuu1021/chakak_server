package com.green.chakak.chakak.account.user;

import com.green.chakak.chakak.account.userType.UserType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "user_account")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    //빌더 생성자 (필수값만) 아마 회원가입때 사용
    @Builder
    public User(String password, String email, UserType userType, UserStatus status) {
        this.password = password;
        this.email = email;
        this.userType = userType;
        this.status = status;
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

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt;

    public enum UserStatus {
        ACTIVE,
        INACTIVE,
        PENDING,
        SUSPENDED
    }

}

