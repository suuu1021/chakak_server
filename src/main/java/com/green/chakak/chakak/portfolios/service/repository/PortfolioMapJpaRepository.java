package com.green.chakak.chakak.portfolios.service.repository;

import com.green.chakak.chakak.portfolios.domain.Portfolio;
import com.green.chakak.chakak.portfolios.domain.PortfolioCategory;
import com.green.chakak.chakak.portfolios.domain.PortfolioMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioMapJpaRepository extends JpaRepository<PortfolioMap, Long> {

	// 특정 포트폴리오의 카테고리들 조회
	List<PortfolioMap> findByPortfolio(Portfolio portfolio);

	// 특정 카테고리의 포트폴리오들 조회 (공개된 것만)
	@Query("SELECT pm FROM PortfolioMap pm WHERE pm.portfolioCategory = :category AND pm.portfolio.isPublic = true")
	List<PortfolioMap> findByPortfolioCategoryAndPublic(@Param("category") PortfolioCategory category);

	// 포트폴리오-카테고리 매핑 존재 여부 확인
	boolean existsByPortfolioAndPortfolioCategory(Portfolio portfolio, PortfolioCategory category);

	// 특정 포트폴리오-카테고리 매핑 조회
	Optional<PortfolioMap> findByPortfolioAndPortfolioCategory(Portfolio portfolio, PortfolioCategory category);

	// 특정 포트폴리오의 모든 매핑 삭제
	void deleteByPortfolio(Portfolio portfolio);

	// 특정 카테고리별 포트폴리오 개수
	@Query("SELECT COUNT(pm) FROM PortfolioMap pm WHERE pm.portfolioCategory = :category AND pm.portfolio.isPublic = true")
	long countByPortfolioCategory(@Param("category") PortfolioCategory category);

	// 카테고리별 포트폴리오 조회 (페이징)
	@Query("SELECT pm.portfolio FROM PortfolioMap pm WHERE pm.portfolioCategory = :category AND pm.portfolio.isPublic = true ORDER BY pm.portfolio.createdAt DESC")
	List<Portfolio> findPortfoliosByCategory(@Param("category") PortfolioCategory category);
}