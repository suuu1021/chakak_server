package com.green.chakak.chakak.account.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    
    @Column(unique = true, nullable = false, length = 100)
    private String email;
    
    @Column(length = 255)
    private String password;
    
    @Column(unique = true, length = 50)
    private String nickname;
    
    @Column(length = 500)
    private String profileImageUrl;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType userType = UserType.CUSTOMER;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SocialType socialType = SocialType.KAKAO;
    
    @Column(length = 100)
    private String socialId;
    
    @Column(length = 20)
    private String phoneNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private userStatus status = userStatus.ACTIVE;
    
    @Column(length = 500)
    private String refreshToken;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    public enum UserType {
        CUSTOMER, PHOTOGRAPHER
    }
    
    public enum SocialType {
        KAKAO
    }
    
    public enum userStatus {
        ACTIVE, INACTIVE, SUSPENDED, DELETED
    }
}
