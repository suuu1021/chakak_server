//package com.green.chakak.chakak.portfolios.repository;
//
//import com.green.chakak.chakak.portfolios.domain.PortfolioCategory;
//import com.green.chakak.chakak.portfolios.service.repository.PortfolioCategoryJpaRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.*;
//
///**
// * PortfolioCategoryJpaRepository 단위 테스트
// *
// * 테스트 대상:
// * - 기본 CRUD 작업
// * - 활성화된 카테고리 조회
// * - 카테고리명 중복 체크
// * - 계층구조 관련 메서드
// * - 정렬 기능
// */
//@DataJpaTest
//@DisplayName("PortfolioCategory Repository 테스트")
//class PortfolioCategoryJpaRepositoryTest {
//
//	@Autowired
//	private PortfolioCategoryJpaRepository categoryRepository;
//
//	@Autowired
//	private TestEntityManager entityManager;
//
//	private PortfolioCategory parentCategory;
//	private PortfolioCategory activeCategory;
//	private PortfolioCategory inactiveCategory;
//
//	@BeforeEach
//	void setUp() {
//		// 부모 카테고리 생성
//		parentCategory = new PortfolioCategory();
//		parentCategory.setCategoryName("웨딩촬영");
//		parentCategory.setSortOrder(1);
//		parentCategory.setIsActive(true);
//
//		// 활성화된 카테고리 생성
//		activeCategory = new PortfolioCategory();
//		activeCategory.setCategoryName("야외촬영");
//		activeCategory.setSortOrder(2);
//		activeCategory.setIsActive(true);
//
//		// 비활성화된 카테고리 생성
//		inactiveCategory = new PortfolioCategory();
//		inactiveCategory.setCategoryName("폐기카테고리");
//		inactiveCategory.setSortOrder(3);
//		inactiveCategory.setIsActive(false);
//
//		// 데이터베이스에 저장
//		entityManager.persistAndFlush(parentCategory);
//		entityManager.persistAndFlush(activeCategory);
//		entityManager.persistAndFlush(inactiveCategory);
//
//		entityManager.clear();
//	}
//
//	@Test
//	@DisplayName("카테고리 저장 및 조회 테스트")
//	void testSaveAndFindById() {
//		// Given
//		PortfolioCategory newCategory = new PortfolioCategory();
//		newCategory.setCategoryName("스튜디오촬영");
//		newCategory.setSortOrder(4);
//		newCategory.setIsActive(true);
//
//		// When
//		PortfolioCategory savedCategory = categoryRepository.save(newCategory);
//		Optional<PortfolioCategory> foundCategory = categoryRepository.findById(savedCategory.getPortfolioCategoryId());
//
//		// Then
//		assertThat(foundCategory).isPresent();
//		assertThat(foundCategory.get().getCategoryName()).isEqualTo("스튜디오촬영");
//		assertThat(foundCategory.get().getSortOrder()).isEqualTo(4);
//		assertThat(foundCategory.get().getIsActive()).isTrue();
//		assertThat(foundCategory.get().getCreatedAt()).isNotNull();
//		assertThat(foundCategory.get().getUpdatedAt()).isNotNull();
//	}
//
//	@Test
//	@DisplayName("활성화된 카테고리만 조회 테스트")
//	void testFindByIsActiveTrue() {
//		// When
//		List<PortfolioCategory> activeCategories = categoryRepository.findByIsActiveTrue();
//
//		// Then
//		assertThat(activeCategories).hasSize(2);
//		assertThat(activeCategories)
//				.extracting(PortfolioCategory::getCategoryName)
//				.containsExactlyInAnyOrder("웨딩촬영", "야외촬영");
//
//		// 비활성화된 카테고리는 포함되지 않아야 함
//		assertThat(activeCategories)
//				.extracting(PortfolioCategory::getCategoryName)
//				.doesNotContain("폐기카테고리");
//	}
//
//	@Test
//	@DisplayName("활성화된 카테고리를 정렬 순서대로 조회 테스트")
//	void testFindByIsActiveTrueOrderBySortOrderAsc() {
//		// When
//		List<PortfolioCategory> sortedCategories = categoryRepository.findByIsActiveTrueOrderBySortOrderAsc();
//
//		// Then
//		assertThat(sortedCategories).hasSize(2);
//		assertThat(sortedCategories.get(0).getCategoryName()).isEqualTo("웨딩촬영");
//		assertThat(sortedCategories.get(0).getSortOrder()).isEqualTo(1);
//		assertThat(sortedCategories.get(1).getCategoryName()).isEqualTo("야외촬영");
//		assertThat(sortedCategories.get(1).getSortOrder()).isEqualTo(2);
//	}
//
//	@Test
//	@DisplayName("카테고리명으로 활성화된 카테고리 조회 테스트")
//	void testFindByCategoryNameAndIsActiveTrue() {
//		// When
//		Optional<PortfolioCategory> foundCategory = categoryRepository.findByCategoryNameAndIsActiveTrue("웨딩촬영");
//		Optional<PortfolioCategory> notFoundCategory = categoryRepository.findByCategoryNameAndIsActiveTrue("폐기카테고리");
//		Optional<PortfolioCategory> nonExistentCategory = categoryRepository.findByCategoryNameAndIsActiveTrue("존재하지않는카테고리");
//
//		// Then
//		assertThat(foundCategory).isPresent();
//		assertThat(foundCategory.get().getCategoryName()).isEqualTo("웨딩촬영");
//
//		// 비활성화된 카테고리는 조회되지 않아야 함
//		assertThat(notFoundCategory).isEmpty();
//
//		// 존재하지 않는 카테고리는 조회되지 않아야 함
//		assertThat(nonExistentCategory).isEmpty();
//	}
//
//	@Test
//	@DisplayName("카테고리명 중복 체크 테스트")
//	void testExistsByCategoryName() {
//		// When & Then
//		assertThat(categoryRepository.existsByCategoryName("웨딩촬영")).isTrue();
//		assertThat(categoryRepository.existsByCategoryName("야외촬영")).isTrue();
//		assertThat(categoryRepository.existsByCategoryName("폐기카테고리")).isTrue(); // 비활성화되어도 존재는 함
//		assertThat(categoryRepository.existsByCategoryName("존재하지않는카테고리")).isFalse();
//	}
//
//	@Test
//	@DisplayName("부모-자식 카테고리 계층구조 테스트")
//	void testParentChildHierarchy() {
//		// Given
//		PortfolioCategory childCategory = new PortfolioCategory();
//		childCategory.setCategoryName("야외웨딩촬영");
//		childCategory.setParent(parentCategory);
//		childCategory.setSortOrder(1);
//		childCategory.setIsActive(true);
//
//		// When
//		PortfolioCategory savedChild = categoryRepository.save(childCategory);
//		entityManager.flush();
//		entityManager.clear();
//
//		// Then
//		Optional<PortfolioCategory> foundChild = categoryRepository.findById(savedChild.getPortfolioCategoryId());
//		assertThat(foundChild).isPresent();
//		assertThat(foundChild.get().getParent()).isNotNull();
//		assertThat(foundChild.get().getParent().getPortfolioCategoryId()).isEqualTo(parentCategory.getPortfolioCategoryId());
//		assertThat(foundChild.get().getParent().getCategoryName()).isEqualTo("웨딩촬영");
//	}
//
//	@Test
//	@DisplayName("특정 부모의 하위 카테고리 조회 테스트")
//	void testFindByParent_PortfolioCategoryIdOrderBySortOrderAsc() {
//		// Given - 부모 카테고리에 여러 자식 카테고리 생성
//		PortfolioCategory child1 = new PortfolioCategory();
//		child1.setCategoryName("야외웨딩촬영");
//		child1.setParent(parentCategory);
//		child1.setSortOrder(2);
//		child1.setIsActive(true);
//
//		PortfolioCategory child2 = new PortfolioCategory();
//		child2.setCategoryName("실내웨딩촬영");
//		child2.setParent(parentCategory);
//		child2.setSortOrder(1);
//		child2.setIsActive(true);
//
//		categoryRepository.save(child1);
//		categoryRepository.save(child2);
//		entityManager.flush();
//
//		// When
//		List<PortfolioCategory> children = categoryRepository.findByParent_PortfolioCategoryIdOrderBySortOrderAsc(
//				parentCategory.getPortfolioCategoryId());
//
//		// Then
//		assertThat(children).hasSize(2);
//		assertThat(children.get(0).getCategoryName()).isEqualTo("실내웨딩촬영"); // sortOrder 1
//		assertThat(children.get(1).getCategoryName()).isEqualTo("야외웨딩촬영"); // sortOrder 2
//	}
//
//	@Test
//	@DisplayName("최상위(루트) 카테고리 조회 테스트")
//	void testFindByParentIsNullOrderBySortOrderAsc() {
//		// When
//		List<PortfolioCategory> rootCategories = categoryRepository.findByParentIsNullOrderBySortOrderAsc();
//
//		// Then
//		assertThat(rootCategories).hasSize(3); // parentCategory, activeCategory, inactiveCategory
//
//		// sortOrder 순으로 정렬되어야 함
//		assertThat(rootCategories.get(0).getSortOrder()).isEqualTo(1);
//		assertThat(rootCategories.get(1).getSortOrder()).isEqualTo(2);
//		assertThat(rootCategories.get(2).getSortOrder()).isEqualTo(3);
//	}
//
//	@Test
//	@DisplayName("카테고리 수정 테스트")
//	void testUpdateCategory() {
//		// Given
//		Long categoryId = parentCategory.getPortfolioCategoryId();
//
//		// When
//		Optional<PortfolioCategory> foundCategory = categoryRepository.findById(categoryId);
//		assertThat(foundCategory).isPresent();
//
//		PortfolioCategory categoryToUpdate = foundCategory.get();
//		categoryToUpdate.setCategoryName("수정된웨딩촬영");
//		categoryToUpdate.setSortOrder(10);
//		categoryToUpdate.setIsActive(false);
//
//		PortfolioCategory updatedCategory = categoryRepository.save(categoryToUpdate);
//		entityManager.flush();
//		entityManager.clear();
//
//		// Then
//		Optional<PortfolioCategory> reloadedCategory = categoryRepository.findById(categoryId);
//		assertThat(reloadedCategory).isPresent();
//		assertThat(reloadedCategory.get().getCategoryName()).isEqualTo("수정된웨딩촬영");
//		assertThat(reloadedCategory.get().getSortOrder()).isEqualTo(10);
//		assertThat(reloadedCategory.get().getIsActive()).isFalse();
//		assertThat(reloadedCategory.get().getUpdatedAt()).isNotNull();
//	}
//
//	@Test
//	@DisplayName("카테고리 삭제 테스트")
//	void testDeleteCategory() {
//		// Given
//		Long categoryId = activeCategory.getPortfolioCategoryId();
//
//		// When
//		categoryRepository.deleteById(categoryId);
//		entityManager.flush();
//
//		// Then
//		Optional<PortfolioCategory> deletedCategory = categoryRepository.findById(categoryId);
//		assertThat(deletedCategory).isEmpty();
//
//		// 다른 카테고리들은 여전히 존재해야 함
//		List<PortfolioCategory> remainingCategories = categoryRepository.findAll();
//		assertThat(remainingCategories).hasSize(2);
//	}
//}