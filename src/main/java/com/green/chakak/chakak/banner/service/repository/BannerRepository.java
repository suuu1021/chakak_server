package com.green.chakak.chakak.banner.service.repository;

import com.green.chakak.chakak.banner.domain.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {
    
    // 활성화되고 만료되지 않은 배너만 조회 (표시 순서대로)
    @Query("SELECT b FROM Banner b WHERE b.isActive = true AND (b.expiresAt IS NULL OR b.expiresAt > :now) ORDER BY b.displayOrder ASC, b.createdAt DESC")
    List<Banner> findActiveAndNotExpiredBanners(LocalDateTime now);
    
    // 활성화된 배너만 조회 (만료 여부 상관없이)
    List<Banner> findByIsActiveTrueOrderByDisplayOrderAscCreatedAtDesc();
    
    // 모든 배너 조회 (표시 순서대로)
    List<Banner> findAllByOrderByDisplayOrderAscCreatedAtDesc();
    
    // 특정 순서 이상의 배너들 조회 (순서 변경시 사용)
    List<Banner> findByDisplayOrderGreaterThanEqualOrderByDisplayOrderAsc(Integer displayOrder);
    
    // 활성화 상태별 배너 개수 조회
    long countByIsActive(Boolean isActive);
    
    // 만료된 배너 조회
    @Query("SELECT b FROM Banner b WHERE b.expiresAt IS NOT NULL AND b.expiresAt <= :now")
    List<Banner> findExpiredBanners(LocalDateTime now);
    
    // 특정 날짜 이후에 만료될 배너들 조회
    @Query("SELECT b FROM Banner b WHERE b.expiresAt IS NOT NULL AND b.expiresAt BETWEEN :start AND :end")
    List<Banner> findBannersExpiringBetween(LocalDateTime start, LocalDateTime end);
    
    // 현재 사용중인 가장 큰 display_order 값 조회
    @Query("SELECT COALESCE(MAX(b.displayOrder), -1) FROM Banner b")
    Integer findMaxDisplayOrder();
    
    // 만료되지 않은 활성 배너 개수 조회
    @Query("SELECT COUNT(b) FROM Banner b WHERE b.isActive = true AND (b.expiresAt IS NULL OR b.expiresAt > :now)")
    long countActiveAndNotExpired(LocalDateTime now);
}