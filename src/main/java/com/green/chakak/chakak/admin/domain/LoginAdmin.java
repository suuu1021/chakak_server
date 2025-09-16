package com.green.chakak.chakak.admin.domain;

import com.green.chakak.chakak.account.domain.User;
import lombok.Builder;
import lombok.Data;

@Data
public class LoginAdmin {


    private Long adminId;
    private String adminName;
    private String userTypeName;

    @Builder
    private LoginAdmin(Long adminId, String adminName, String userTypeName) {
        this.adminId = adminId;
        this.adminName = adminName;
        this.userTypeName = userTypeName;
    }

    // Admin 엔티티로부터 LoginAdmin 생성
    public static LoginAdmin fromEntity(Admin admin) {
        return LoginAdmin.builder()
                .adminId(admin.getAdminId())
                .adminName(admin.getAdminName())
                .userTypeName(admin.getUserType().getTypeCode())
                .build();
    }
}
