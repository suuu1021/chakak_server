package com.green.chakak.chakak.account.user;

import com.green.chakak.chakak.account.user.User.UserStatus;
import lombok.Builder;
import lombok.Getter;


@Getter

public class LoginUser {

    private Long id;              // 사용자 PK
    private String email;         // 로그인 식별자
    private String userTypeName;  // UserType 엔티티의 이름 (예: CUSTOMER, PHOTOGRAPHER)
    private UserStatus status;    // 계정 상태

    @Builder
    private LoginUser(Long id, String email, String userTypeName, UserStatus status) {
        this.id = id;
        this.email = email;
        this.userTypeName = userTypeName;
        this.status = status;
    }

    public static LoginUser fromEntity(User user) {
        return LoginUser.builder()
                .id(user.getId())
                .email(user.getEmail())
                .userTypeName(user.getUserType().getTypeCode())
                .status(user.getStatus())
                .build();
    }

}
