package com.green.chakak.chakak.banner.service.repository;

import com.green.chakak.chakak.banner.domain.Banner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {

    // 활성화되고 만료되지 않은 배너 조회 (페이징)
    @Query("SELECT b FROM Banner b WHERE b.isActive = true AND (b.expiresAt IS NULL OR b.expiresAt > :now) ORDER BY b.displayOrder ASC, b.createdAt DESC")
    Page<Banner> findActiveAndNotExpiredBanners(@Param("now") LocalDateTime now, Pageable pageable);

    // 활성화되고 만료되지 않은 배너 조회 (리스트)
    @Query("SELECT b FROM Banner b WHERE b.isActive = true AND (b.expiresAt IS NULL OR b.expiresAt > :now) ORDER BY b.displayOrder ASC, b.createdAt DESC")
    List<Banner> findActiveAndNotExpiredBanners(@Param("now") LocalDateTime now);

    // 전체 배너 조회 (페이징)
    Page<Banner> findAllByOrderByDisplayOrderAscCreatedAtDesc(Pageable pageable);

    // 키워드로 배너 검색 (제목, 부제목)
    @Query("SELECT b FROM Banner b WHERE b.title LIKE %:keyword% OR b.subtitle LIKE %:keyword% ORDER BY b.displayOrder ASC, b.createdAt DESC")
    Page<Banner> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // 만료된 배너 조회
    @Query("SELECT b FROM Banner b WHERE b.expiresAt IS NOT NULL AND b.expiresAt <= :now")
    List<Banner> findExpiredBanners(@Param("now") LocalDateTime now);

    // 최대 표시 순서 조회
    @Query("SELECT COALESCE(MAX(b.displayOrder), -1) FROM Banner b")
    Integer findMaxDisplayOrder();

    // 활성화된 배너 개수
    long countByIsActive(Boolean isActive);

    // 특정 표시 순서 이상의 배너들 조회
    List<Banner> findByDisplayOrderGreaterThanEqualOrderByDisplayOrderAsc(Integer displayOrder);

    // ID로 배너 조회 (존재 여부 확인용)
    @Query("SELECT b FROM Banner b WHERE b.bannerId = :id")
    java.util.Optional<Banner> findByBannerId(@Param("id") Long id);
}