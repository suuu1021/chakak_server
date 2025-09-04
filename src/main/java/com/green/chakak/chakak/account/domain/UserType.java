package com.green.chakak.chakak.account.domain;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
//
@Data
@Entity
@Table(name = "USER_TYPE")
public class UserType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_TYPE_ID")
    private Long userTypeId;

    @Column(name = "TYPE_NAME", nullable = false, length = 50)
    private String typeName; // 예: 개인회원, 포토그래퍼

    @Column(name = "TYPE_CODE", nullable = false, length = 20)
    private String typeCode; // 예: user, photographer

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt;
}
