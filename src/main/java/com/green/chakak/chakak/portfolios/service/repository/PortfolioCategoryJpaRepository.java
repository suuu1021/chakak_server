package com.green.chakak.chakak.portfolios.service.repository;

import com.green.chakak.chakak.portfolios.domain.PortfolioCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioCategoryJpaRepository extends JpaRepository<PortfolioCategory, Long> {

	// 활성화된 카테고리만 조회
	List<PortfolioCategory> findByIsActiveTrue();

	// 카테고리명으로 조회
	Optional<PortfolioCategory> findByCategoryName(String categoryName);

	// 카테고리명 중복 체크
	boolean existsByCategoryName(String categoryName);

	// 활성화된 카테고리명으로 조회
	Optional<PortfolioCategory> findByCategoryNameAndIsActiveTrue(String categoryName);
}