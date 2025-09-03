package com.green.chakak.chakak.portfolios.service.repository;

import com.green.chakak.chakak.portfolios.domain.PortfolioCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioCategoryJpaRepository extends JpaRepository<PortfolioCategory, Long> {

	// 활성화된 모든 카테고리를 정렬 순서대로 조회합니다.
	List<PortfolioCategory> findByIsActiveTrueOrderBySortOrderAsc();

	// 특정 카테고리명으로 활성화된 카테고리를 조회합니다.
	Optional<PortfolioCategory> findByCategoryNameAndIsActiveTrue(String categoryName);

	// 카테고리명 중복 여부를 확인합니다.
	boolean existsByCategoryName(String categoryName);

	// 활성화된 카테고리 목록 조회
	List<PortfolioCategory> findByIsActiveTrue();

	// 부모 카테고리 ID를 통해 하위 카테고리 목록을 정렬 순서대로 조회합니다.
	List<PortfolioCategory> findByParent_PortfolioCategoryIdOrderBySortOrderAsc(Long parentId);

	// 부모가 없는 최상위(루트) 카테고리 목록을 정렬 순서대로 조회합니다.
	List<PortfolioCategory> findByParentIsNullOrderBySortOrderAsc();
}