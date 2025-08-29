package com.green.chakak.chakak.account.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UserRequest {

    public record Create(
            @Email @NotBlank String email,
            @NotBlank String password,
            @NotBlank String nickname,
            @NotNull UserStatus status,
            @NotNull Long userTypeId
    ) {}

    public record Update(
            String password,
            String nickname,
            UserStatus status,
            Long userTypeId
    ) {}
}
