package com.green.chakak.chakak.account.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<User, Long> {

    // 이메일로 조회 (로그인, 중복검사 등에 활용)
    Optional<User> findByEmail(String email);

    // 상태별 조회 (필요 시)
    Optional<User> findByEmailAndStatus(String email, UserStatus status);
}