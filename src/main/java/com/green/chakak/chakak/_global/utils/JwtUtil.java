package com.green.chakak.chakak._global.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.green.chakak.chakak.account.domain.LoginUser;
import com.green.chakak.chakak.account.domain.User;
import com.green.chakak.chakak.admin.domain.LoginAdmin;

import java.util.Date;

public class JwtUtil {

    private static final String SECRET_KEY = "chakak"; // 운영은 환경변수/설정 분리
    private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 1시간
    private static final String SUBJECT = "chakak_snap";

    // 토큰 생성
    public static String create(LoginUser loginUser) {
        Date expiresAt = new Date(System.currentTimeMillis() + EXPIRATION_TIME);
        return JWT.create()
                .withSubject(SUBJECT)
                .withExpiresAt(expiresAt)
                .withIssuedAt(new Date())
                .withClaim("id", loginUser.getId())
                .withClaim("email", loginUser.getEmail())
                .withClaim("userTypeName", loginUser.getUserTypeName())
                .withClaim("status", loginUser.getStatus().name())
                .sign(Algorithm.HMAC512(SECRET_KEY));
    }

    public static String createForAdmin(LoginAdmin loginAdmin) {
        Date expiresAt = new Date(System.currentTimeMillis() + EXPIRATION_TIME);
        return JWT.create()
                .withSubject(SUBJECT)
                .withExpiresAt(expiresAt)
                .withIssuedAt(new Date())
                .withClaim("adminId", loginAdmin.getAdminId())
                .withClaim("adminName", loginAdmin.getAdminName())
                .withClaim("userTypeName", loginAdmin.getUserTypeName())
                .sign(Algorithm.HMAC512(SECRET_KEY));
    }

    // 토큰 → LoginUser
    public static LoginUser verify(String jwt) {
        DecodedJWT decoded = JWT.require(Algorithm.HMAC512(SECRET_KEY))
                .withSubject(SUBJECT)
                .build()
                .verify(jwt);

        Long id = decoded.getClaim("id").asLong();
        String email = decoded.getClaim("email").asString();
        String userTypeName = decoded.getClaim("userTypeName").asString();
        String statusStr = decoded.getClaim("status").asString();
        User.UserStatus status =
                User.UserStatus.valueOf(statusStr);

        return LoginUser.builder()
                .id(id)
                .email(email)
                .userTypeName(userTypeName)
                .status(status)
                .build();
    }

    // 토큰 → LoginAdmin
    public static LoginAdmin verifyAdmin(String jwt) {
        DecodedJWT decoded = JWT.require(Algorithm.HMAC512(SECRET_KEY))
                .withSubject(SUBJECT)
                .build()
                .verify(jwt);

        Long adminId = decoded.getClaim("adminId").asLong();
        String adminName = decoded.getClaim("adminName").asString();
        String userTypeName = decoded.getClaim("userTypeName").asString();

        return LoginAdmin.builder()
                .adminId(adminId)
                .adminName(adminName)
                .userTypeName(userTypeName)
                .build();
    }

    // 토큰에서 userTypeName만 추출 (서명 검증 X)
    public static String getUserTypeName(String jwt) {
        try {
            return JWT.decode(jwt).getClaim("userTypeName").asString();
        } catch (Exception e) {
            return null;
        }
    }


    // Authorization 헤더추출
    public static String resolveToken(String authHeader) {
        if (authHeader == null) return null;
        if (!authHeader.startsWith("Bearer ")) return null;
        return authHeader.substring(7);
    }

    // 유효성만 체크
    public static boolean isValid(String jwt) {
        try {
            JWT.require(Algorithm.HMAC512(SECRET_KEY))
                    .withSubject(SUBJECT)
                    .build()
                    .verify(jwt);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
