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

	// 특정 포트폴리오의 이미지들 조회 (순서대로)
	List<PortfolioImage> findByPortfolioOrderByImageOrder(Portfolio portfolio);

	// 특정 포트폴리오의 대표 이미지 조회 (첫 번째 이미지)
	Optional<PortfolioImage> findByPortfolioAndImageOrder(Portfolio portfolio, Integer imageOrder);

	// 특정 포트폴리오의 이미지 개수
	long countByPortfolio(Portfolio portfolio);

	// 포트폴리오별 첫 번째 이미지만 조회 (썸네일용)
	@Query("SELECT pi FROM PortfolioImage pi WHERE pi.portfolio = :portfolio AND pi.imageOrder = 1")
	Optional<PortfolioImage> findThumbnailByPortfolio(@Param("portfolio") Portfolio portfolio);

	// 특정 포트폴리오의 마지막 순서 번호 조회
	@Query("SELECT COALESCE(MAX(pi.imageOrder), 0) FROM PortfolioImage pi WHERE pi.portfolio = :portfolio")
	Integer findMaxImageOrderByPortfolio(@Param("portfolio") Portfolio portfolio);

	// URL로 이미지 조회 (중복 업로드 방지용)
	Optional<PortfolioImage> findByImageUrl(String imageUrl);

	// 특정 포트폴리오에서 순서 이후의 이미지들 조회 (순서 재정렬용)
	List<PortfolioImage> findByPortfolioAndImageOrderGreaterThanOrderByImageOrder(
			Portfolio portfolio, Integer imageOrder);
}