package com.green.chakak.chakak.portfolios.service.repository;

import com.green.chakak.chakak.portfolios.domain.Portfolio;
import com.green.chakak.chakak.portfolios.domain.PortfolioImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioImageJpaRepository extends JpaRepository<PortfolioImage, Long> {

	// 특정 포트폴리오의 모든 이미지들 조회 (생성일순)
	List<PortfolioImage> findByPortfolioOrderByCreatedAt(Portfolio portfolio);

	// 특정 포트폴리오의 대표 이미지 조회
	Optional<PortfolioImage> findByPortfolioAndIsMainTrue(Portfolio portfolio);

	// 특정 포트폴리오의 일반 이미지들 조회 (대표 이미지 제외)
	List<PortfolioImage> findByPortfolioAndIsMainFalseOrderByCreatedAt(Portfolio portfolio);

	// 특정 포트폴리오의 이미지 개수
	long countByPortfolio(Portfolio portfolio);

	// URL로 이미지 조회 (중복 업로드 방지용)
	Optional<PortfolioImage> findByImageUrl(String imageUrl);

	// 특정 포트폴리오에 대표 이미지가 있는지 확인
	boolean existsByPortfolioAndIsMainTrue(Portfolio portfolio);

	// 특정 포트폴리오의 대표가 아닌 이미지 개수
	long countByPortfolioAndIsMainFalse(Portfolio portfolio);

	// 이미지 URL로 중복 체크
	boolean existsByImageUrl(String imageUrl);

	// 특정 포트폴리오의 첫 번째 이미지 조회 (대표 이미지가 없을 경우 백업용)
	@Query("SELECT pi FROM PortfolioImage pi WHERE pi.portfolio = :portfolio ORDER BY pi.createdAt ASC")
	Optional<PortfolioImage> findFirstImageByPortfolio(@Param("portfolio") Portfolio portfolio);

	// 특정 포트폴리오의 최신 이미지 N개 조회
	@Query("SELECT pi FROM PortfolioImage pi WHERE pi.portfolio = :portfolio ORDER BY pi.createdAt DESC")
	List<PortfolioImage> findTopNByPortfolioOrderByCreatedAtDesc(@Param("portfolio") Portfolio portfolio);

	// 모든 대표 이미지들 조회 (갤러리 표시용)
	@Query("SELECT pi FROM PortfolioImage pi WHERE pi.isMain = true ORDER BY pi.createdAt DESC")
	List<PortfolioImage> findAllMainImages();

	// 특정 사진작가의 모든 대표 이미지들 조회
	@Query("SELECT pi FROM PortfolioImage pi WHERE pi.portfolio.photographerProfile = :photographerProfile AND pi.isMain = true ORDER BY pi.createdAt DESC")
	List<PortfolioImage> findMainImagesByPhotographer(@Param("photographerProfile") com.green.chakak.chakak.photographer.domain.PhotographerProfile photographerProfile);

	void deleteByPortfolio_PortfolioId(Long portfolioId);
}