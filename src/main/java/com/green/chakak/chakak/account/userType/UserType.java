package com.green.chakak.chakak.account.userType;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_type")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UserType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_type_id")
    private Long id;

    @Column(name = "type_name", nullable = false, length = 50, unique = true)
    private String typeName;  // 예: 일반 사용자, 사진작가, 관리자

    @Column(name = "type_code", nullable = false, length = 20, unique = true)
    private String typeCode;  // 예: user, photographer, admin

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
