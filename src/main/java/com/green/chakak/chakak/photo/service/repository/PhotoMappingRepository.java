package com.green.chakak.chakak.photo.service.repository;

import com.green.chakak.chakak.photo.domain.PhotoServiceCategory;
import com.green.chakak.chakak.photo.domain.PhotoServiceInfo;
import com.green.chakak.chakak.photo.domain.PhotoServiceMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhotoMappingRepository extends JpaRepository<PhotoServiceMapping, Long> {

    // 서비스 ID로 매핑 조회
    List<PhotoServiceMapping> findByPhotoServiceInfo_ServiceId(Long serviceId);

    // 카테고리 ID로 매핑 조회
    List<PhotoServiceMapping> findByPhotoServiceCategory_CategoryId(Long categoryId);

    // 서비스와 카테고리 조합으로 매핑 조회
    List<PhotoServiceMapping> findByPhotoServiceInfo_ServiceIdAndPhotoServiceCategory_CategoryId(Long serviceId, Long categoryId);

    // 중복 매핑 체크
    boolean existsByPhotoServiceInfoAndPhotoServiceCategory(PhotoServiceInfo photoServiceInfo, PhotoServiceCategory photoServiceCategory);

    // 특정 서비스의 모든 매핑 삭제
    void deleteByPhotoServiceInfo_ServiceId(Long serviceId);

    // 특정 카테고리의 모든 매핑 삭제
    void deleteByPhotoServiceCategory_CategoryId(Long categoryId);

    // 서비스별 매핑 개수
    long countByPhotoServiceInfo_ServiceId(Long serviceId);

    // 카테고리별 매핑 개수
    long countByPhotoServiceCategory_CategoryId(Long categoryId);
}