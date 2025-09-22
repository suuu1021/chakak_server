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


    @Query("SELECT b FROM Banner b WHERE b.isActive = true AND (b.expiresAt IS NULL OR b.expiresAt > :now) ORDER BY b.displayOrder ASC, b.createdAt DESC")
    Page<Banner> findActiveAndNotExpiredBanners(@Param("now") LocalDateTime now, Pageable pageable);


    @Query("SELECT b FROM Banner b WHERE b.isActive = true AND (b.expiresAt IS NULL OR b.expiresAt > :now) ORDER BY b.displayOrder ASC, b.createdAt DESC")
    List<Banner> findActiveAndNotExpiredBanners(@Param("now") LocalDateTime now);


    Page<Banner> findAllByOrderByDisplayOrderAscCreatedAtDesc(Pageable pageable);


    @Query("SELECT b FROM Banner b WHERE b.title LIKE %:keyword% OR b.subtitle LIKE %:keyword% ORDER BY b.displayOrder ASC, b.createdAt DESC")
    Page<Banner> findByKeyword(@Param("keyword") String keyword, Pageable pageable);


    @Query("SELECT b FROM Banner b WHERE b.expiresAt IS NOT NULL AND b.expiresAt <= :now")
    List<Banner> findExpiredBanners(@Param("now") LocalDateTime now);


    @Query("SELECT COALESCE(MAX(b.displayOrder), -1) FROM Banner b")
    Integer findMaxDisplayOrder();


    long countByIsActive(Boolean isActive);


    List<Banner> findByDisplayOrderGreaterThanEqualOrderByDisplayOrderAsc(Integer displayOrder);


    @Query("SELECT b FROM Banner b WHERE b.bannerId = :id")
    java.util.Optional<Banner> findByBannerId(@Param("id") Long id);
}