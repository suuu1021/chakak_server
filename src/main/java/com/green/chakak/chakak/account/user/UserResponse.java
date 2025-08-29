package com.green.chakak.chakak.account.user;

public class UserResponse {

    public record Summary(
            Long id,
            String email,
            String nickname,
            UserStatus status
    ) {}

    public record Detail(
            Long id,
            String email,
            String nickname,
            UserStatus status,
            Long userTypeId,
            String userTypeCode,   // UserType 확장 시 사용(없으면 null)
            String userTypeName
    ) {}
}
