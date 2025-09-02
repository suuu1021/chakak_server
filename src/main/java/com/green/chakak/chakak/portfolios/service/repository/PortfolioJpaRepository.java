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

	// 특정 사진작가의 공개 포트폴리오만 조회
	List<Portfolio> findByPhotographerProfileAndIsPublicTrue(PhotographerProfile photographerProfile);

	// 공개 포트폴리오 페이징 조회 (최신순)
	Page<Portfolio> findByIsPublicTrueOrderByCreatedAtDesc(Pageable pageable);

	// 인기 포트폴리오 조회 (좋아요 수 기준)
	Page<Portfolio> findByIsPublicTrueOrderByLikeCountDescCreatedAtDesc(Pageable pageable);

	// 조회수 많은 순으로 조회
	Page<Portfolio> findByIsPublicTrueOrderByViewCountDescCreatedAtDesc(Pageable pageable);

	// 제목으로 검색 (공개 포트폴리오만)
	@Query("SELECT p FROM Portfolio p WHERE p.isPublic = true AND p.portfolioTitle LIKE %:keyword%")
	Page<Portfolio> findByTitleContaining(@Param("keyword") String keyword, Pageable pageable);

	// 위치로 검색 (공개 포트폴리오만)
	@Query("SELECT p FROM Portfolio p WHERE p.isPublic = true AND p.shootingLocation LIKE %:location%")
	Page<Portfolio> findByLocationContaining(@Param("location") String location, Pageable pageable);

	// 특정 기간 내 포트폴리오 조회
	@Query("SELECT p FROM Portfolio p WHERE p.isPublic = true AND p.shootingDate BETWEEN :startDate AND :endDate")
	List<Portfolio> findByShootingDateBetween(@Param("startDate") LocalDateTime startDate,
											  @Param("endDate") LocalDateTime endDate);

	// 조회수 증가
	@Modifying
	@Query("UPDATE Portfolio p SET p.viewCount = p.viewCount + 1 WHERE p.portfolioId = :portfolioId")
	void incrementViewCount(@Param("portfolioId") Long portfolioId);

	// 좋아요 수 증가
	@Modifying
	@Query("UPDATE Portfolio p SET p.likeCount = p.likeCount + 1 WHERE p.portfolioId = :portfolioId")
	void incrementLikeCount(@Param("portfolioId") Long portfolioId);

	// 좋아요 수 감소
	@Modifying
	@Query("UPDATE Portfolio p SET p.likeCount = p.likeCount - 1 WHERE p.portfolioId = :portfolioId AND p.likeCount > 0")
	void decrementLikeCount(@Param("portfolioId") Long portfolioId);

	// 사진작가별 포트폴리오 개수
	@Query("SELECT COUNT(p) FROM Portfolio p WHERE p.photographerProfile = :photographerProfile AND p.isPublic = true")
	long countByPhotographerProfile(@Param("photographerProfile") PhotographerProfile photographerProfile);
}