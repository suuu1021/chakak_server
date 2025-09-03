package com.green.chakak.chakak.portfolios.service.repository;

import com.green.chakak.chakak.portfolios.domain.Portfolio;
import com.green.chakak.chakak.photographer.domain.PhotographerProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PortfolioJpaRepository extends JpaRepository<Portfolio, Long> {

	// 특정 사진작가의 포트폴리오 조회
	List<Portfolio> findByPhotographerProfile(PhotographerProfile photographerProfile);

	// 공개 포트폴리오 페이징 조회 (최신순)
	Page<Portfolio> findAllByOrderByCreatedAtDesc(Pageable pageable);

	// 제목으로 검색
	@Query("SELECT p FROM Portfolio p WHERE p.title LIKE %:keyword%")
	Page<Portfolio> findByTitleContaining(@Param("keyword") String keyword, Pageable pageable);

	// 사진작가별 포트폴리오 개수
	@Query("SELECT COUNT(p) FROM Portfolio p WHERE p.photographerProfile = :photographerProfile")
	long countByPhotographerProfile(@Param("photographerProfile") PhotographerProfile photographerProfile);

	// 특정 사진작가의 포트폴리오 조회 (페이징)
	Page<Portfolio> findByPhotographerProfile(PhotographerProfile photographerProfile, Pageable pageable);

	// 최신순으로 포트폴리오 조회
	List<Portfolio> findByPhotographerProfileOrderByCreatedAtDesc(PhotographerProfile photographerProfile);

	// 썸네일 URL로 포트폴리오 조회 (중복 체크용)
	boolean existsByThumbnailUrl(String thumbnailUrl);

	// 제목과 사진작가로 포트폴리오 조회 (중복 체크용)
	boolean existsByTitleAndPhotographerProfile(String title, PhotographerProfile photographerProfile);

	// 특정 기간 내 생성된 포트폴리오 조회
	@Query("SELECT p FROM Portfolio p WHERE p.createdAt BETWEEN :startDate AND :endDate")
	List<Portfolio> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate,
										   @Param("endDate") LocalDateTime endDate);

	// 사진작가 이름으로 포트폴리오 검색
	@Query("SELECT p FROM Portfolio p WHERE p.photographerProfile.businessName LIKE %:businessName%")
	Page<Portfolio> findByPhotographerBusinessNameContaining(@Param("businessName") String businessName, Pageable pageable);

	// 제목과 설명에서 키워드 검색
	@Query("SELECT p FROM Portfolio p WHERE p.title LIKE %:keyword% OR p.description LIKE %:keyword%")
	Page<Portfolio> findByTitleOrDescriptionContaining(@Param("keyword") String keyword, Pageable pageable);

	// 특정 사진작가의 최신 포트폴리오 N개 조회
	@Query("SELECT p FROM Portfolio p WHERE p.photographerProfile = :photographerProfile ORDER BY p.createdAt DESC")
	List<Portfolio> findTopNByPhotographerProfile(@Param("photographerProfile") PhotographerProfile photographerProfile, Pageable pageable);
}