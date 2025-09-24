package com.green.chakak.chakak.admin.service.repository;

import com.green.chakak.chakak.admin.domain.AdminProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminProfileJpaRepository extends JpaRepository <AdminProfile, Long> {

    // 관리자 ID로 프로필 찾기
    Optional<AdminProfile> findByAdmin_AdminId(Long adminId);

    // 닉네임 중복 체크
    boolean existsByNickName(String nickName);

    // 닉네임으로 프로필 찾기 (선택사항)
    Optional<AdminProfile> findByNickName(String nickName);

}
