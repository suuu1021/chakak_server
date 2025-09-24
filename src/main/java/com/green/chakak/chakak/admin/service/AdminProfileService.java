package com.green.chakak.chakak.admin.service;

import com.green.chakak.chakak.admin.domain.Admin;
import com.green.chakak.chakak.admin.domain.AdminProfile;
import com.green.chakak.chakak.admin.service.repository.AdminJpaRepository;
import com.green.chakak.chakak.admin.service.repository.AdminProfileJpaRepository;
import com.green.chakak.chakak.admin.service.request.AdminProfileRequest;
import com.green.chakak.chakak.admin.service.response.AdminProfileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AdminProfileService {

    private final AdminJpaRepository adminJpaRepository;
    private final AdminProfileJpaRepository adminProfileJpaRepository;

    @Transactional
    public AdminProfileResponse.DetailDTO createAdminProfile(Long adminId, AdminProfileRequest.CreateDTO dto) {
        log.info("관리자 프로필 생성 시작 - adminId: {}", adminId);

        // 관리자 조회
        Admin admin = adminJpaRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 관리자입니다."));


        if (adminProfileJpaRepository.findByAdmin_AdminId(adminId).isPresent()) {
            throw new RuntimeException("이미 프로필이 존재합니다.");
        }

        AdminProfile adminProfileEntity = dto.toEntity(admin);


        if (adminProfileJpaRepository.existsByNickName(adminProfileEntity.getNickName())) {
            throw new RuntimeException("이미 사용 중인 닉네임입니다.");
        }

        AdminProfile savedProfile = adminProfileJpaRepository.save(adminProfileEntity);

        log.info("관리자 프로필 생성 완료 - profileId: {}", savedProfile.getProfileId());
        return new AdminProfileResponse.DetailDTO(savedProfile);
    }


    @Transactional(readOnly = true)
    public AdminProfileResponse.DetailDTO getAdminProfile(Long adminId) {
        log.info("관리자 프로필 조회 - adminId: {}", adminId);

        AdminProfile adminProfile = adminProfileJpaRepository.findByAdmin_AdminId(adminId)
                .orElseThrow(() -> new RuntimeException("관리자 프로필을 찾을 수 없습니다."));

        return new AdminProfileResponse.DetailDTO(adminProfile);
    }


    @Transactional
    public AdminProfileResponse.UpdateDTO updateAdminProfile(Long adminId, AdminProfileRequest.UpdateDTO dto) {
        log.info("관리자 프로필 수정 시작 - adminId: {}", adminId);


        AdminProfile adminProfile = adminProfileJpaRepository.findByAdmin_AdminId(adminId)
                .orElseThrow(() -> new RuntimeException("관리자 프로필을 찾을 수 없습니다."));

        if (!adminProfile.getNickName().equals(dto.getNickName())
                && adminProfileJpaRepository.existsByNickName(dto.getNickName())) {
            throw new RuntimeException("이미 사용 중인 닉네임입니다.");
        }

        updateProfileFields(adminProfile, dto);

        log.info("관리자 프로필 수정 완료 - profileId: {}", adminProfile.getProfileId());
        return new AdminProfileResponse.UpdateDTO(adminProfile);
    }


    @Transactional
    public void deleteAdminProfile(Long adminId) {
        log.info("관리자 프로필 삭제 시작 - adminId: {}", adminId);

        AdminProfile adminProfile = adminProfileJpaRepository.findByAdmin_AdminId(adminId)
                .orElseThrow(() -> new RuntimeException("관리자 프로필을 찾을 수 없습니다."));

        adminProfileJpaRepository.delete(adminProfile);
        log.info("관리자 프로필 삭제 완료 - adminId: {}", adminId);
    }

    private void updateProfileFields(AdminProfile adminProfile, AdminProfileRequest.UpdateDTO dto) {
        if (dto.getNickName() != null) {
            adminProfile.setNickName(dto.getNickName());
        }
        if (dto.getEmail() != null) {
            adminProfile.setEmail(dto.getEmail());
        }
        if (dto.getImageUrl() != null) {
            adminProfile.setImageUrl(dto.getImageUrl());
        }
    }
}
