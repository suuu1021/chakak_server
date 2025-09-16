package com.green.chakak.chakak.admin.domain;

import com.green.chakak.chakak.account.domain.UserType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@Entity
@Table(name = "admin")
@Builder
public class Admin {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private Long adminId;

    @Column(name = "admin_name")
    private String adminName;
    @Column(name = "password")
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_type_id", nullable = false)
    private UserType userType;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @OneToOne(mappedBy = "admin", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private AdminProfile adminProfile;

    public Admin(Long adminId, String adminName, String password, UserType userType, Timestamp createdAt, AdminProfile adminProfile) {
        this.adminId = adminId;
        this.adminName = adminName;
        this.password = password;
        this.userType = userType;
        this.createdAt = createdAt;
        this.adminProfile = adminProfile;
    }

}
