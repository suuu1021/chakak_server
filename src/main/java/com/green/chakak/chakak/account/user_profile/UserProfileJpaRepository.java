package com.green.chakak.chakak.account.user_profile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserProfileJpaRepository extends JpaRepository<UserProfile, Long> {


    @Query("SELECT u FROM UserProfile u WHERE u.user.userId = :userId")
    Optional<UserProfile> findByUserId(Long userId);

    // 중복 닉네임 체크
    @Query("SELECT u FROM UserProfile u WHERE u.nickName = :nickName")
    Optional<UserProfile> findByNickName(String nickName);
}
