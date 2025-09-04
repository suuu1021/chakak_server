package com.green.chakak.chakak.photo.service.repository;

import com.green.chakak.chakak.photo.domain.PhotoServiceInfo;
import com.green.chakak.chakak.photo.domain.PriceInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PriceInfoJpaRepository extends JpaRepository<PriceInfo, Long> {

    // 특정 PhotoService에 속한 가격 정보들 조회
    List<PriceInfo> findByPhotoServiceInfo_PhotoServiceInfoId(Long photoServiceInfoId);

    // 특정 PhotoService에 속한 가격 정보들 조회 (PhotoServiceInfo 객체로)
    List<PriceInfo> findByPhotoServiceInfo(PhotoServiceInfo photoServiceInfo);

    // 특정 PhotoService의 가격 정보 개수
    long countByPhotoServiceInfo_PhotoServiceInfoId(Long photoServiceInfoId);

    // 특정 PhotoService에 속한 가격 정보 삭제
    void deleteByPhotoServiceInfo_PhotoServiceInfoId(Long photoServiceInfoId);
}