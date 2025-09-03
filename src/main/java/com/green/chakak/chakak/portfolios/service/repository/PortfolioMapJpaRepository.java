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

	// 특정 포트폴리오의 모든 카테고리 매핑을 조회합니다. (GET /portfolios/{portfolioId}/categories)
	List<PortfolioMap> findByPortfolio(Portfolio portfolio);

	// 특정 카테고리에 속한 공개된 포트폴리오 목록을 조회합니다. (GET /categories/{categoryId}/portfolios)
	@Query("SELECT pm FROM PortfolioMap pm WHERE pm.portfolioCategory = :category AND pm.portfolio.isPublic = true")
	List<PortfolioMap> findByPortfolioCategoryAndIsPublicTrue(@Param("category") PortfolioCategory category);

	// 특정 포트폴리오와 카테고리 매핑이 존재하는지 확인합니다.
	boolean existsByPortfolioAndPortfolioCategory(Portfolio portfolio, PortfolioCategory category);

	// 특정 포트폴리오와 카테고리 매핑을 조회합니다.
	Optional<PortfolioMap> findByPortfolioAndPortfolioCategory(Portfolio portfolio, PortfolioCategory category);

	// 특정 포트폴리오에 연결된 모든 카테고리 매핑을 삭제합니다. (DELETE /portfolios/{portfolioId}/categories)
	void deleteByPortfolio(Portfolio portfolio);

	// 특정 카테고리에 속한 공개된 포트폴리오의 개수를 조회합니다.
	@Query("SELECT COUNT(pm) FROM PortfolioMap pm WHERE pm.portfolioCategory = :category AND pm.portfolio.isPublic = true")
	long countByPortfolioCategoryAndIsPublicTrue(@Param("category") PortfolioCategory category);

	// 특정 카테고리에 속한 공개된 포트폴리오들을 생성일 역순으로 조회합니다. (GET /categories/{categoryId}/portfolios)
	@Query("SELECT pm.portfolio FROM PortfolioMap pm WHERE pm.portfolioCategory = :category AND pm.portfolio.isPublic = true ORDER BY pm.portfolio.createdAt DESC")
	List<Portfolio> findPortfoliosByCategoryAndIsPublicTrue(@Param("category") PortfolioCategory category);

	void deleteByPortfolio_PortfolioId(Long portfolioId);
}