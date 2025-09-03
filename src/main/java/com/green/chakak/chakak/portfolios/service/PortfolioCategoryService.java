package com.green.chakak.chakak.portfolios.service;

import com.green.chakak.chakak.portfolios.domain.PortfolioCategory;
import com.green.chakak.chakak.portfolios.service.repository.PortfolioCategoryJpaRepository;
import com.green.chakak.chakak.portfolios.service.request.PortfolioCategoryRequest;
import com.green.chakak.chakak.portfolios.service.response.PortfolioCategoryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 포트폴리오 카테고리 서비스
 *
 * [카테고리 CRUD]
 * - createCategory()      : 카테고리 생성
 * - updateCategory()      : 카테고리 수정
 * - getCategory()         : 카테고리 단건 조회
 * - deleteCategory()      : 카테고리 삭제 (비활성화)
 *
 * [카테고리 조회]
 * - getActiveCategories() : 활성화된 카테고리 목록
 * - getAllCategories()    : 전체 카테고리 목록
 */

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PortfolioCategoryService {

	private final PortfolioCategoryJpaRepository portfolioCategoryRepository;

	/**
	 * 카테고리 생성
	 */
	@Transactional
	public PortfolioCategoryResponse.DetailDTO createCategory(PortfolioCategoryRequest.CreateDTO request) {
		log.info("카테고리 생성 시작: {}", request.getCategoryName());

		// 중복 체크
		if (portfolioCategoryRepository.existsByCategoryName(request.getCategoryName())) {
			throw new IllegalArgumentException("이미 존재하는 카테고리명입니다: " + request.getCategoryName());
		}

		PortfolioCategory category = new PortfolioCategory();
		category.setCategoryName(request.getCategoryName());

		if (request.getParentId() != null) {
			PortfolioCategory parentCategory = portfolioCategoryRepository.findById(request.getParentId())
					.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 부모 카테고리입니다: " + request.getParentId()));
			category.setParent(parentCategory);
		}
		category.setSortOrder(request.getSortOrder());

		PortfolioCategory savedCategory = portfolioCategoryRepository.save(category);
		log.info("카테고리 생성 완료: ID = {}", savedCategory.getPortfolioCategoryId());

		return PortfolioCategoryResponse.DetailDTO.from(savedCategory);
	}

	/**
	 * 카테고리 수정
	 */
	@Transactional
	public PortfolioCategoryResponse.DetailDTO updateCategory(Long categoryId, PortfolioCategoryRequest.UpdateDTO request) {
		log.info("카테고리 수정 시작: ID = {}", categoryId);

		PortfolioCategory category = portfolioCategoryRepository.findById(categoryId)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다: " + categoryId));

		// 카테고리명 중복 체크 (자기 자신 제외)
		if (request.getCategoryName() != null &&
				!request.getCategoryName().equals(category.getCategoryName()) &&
				portfolioCategoryRepository.existsByCategoryName(request.getCategoryName())) {
			throw new IllegalArgumentException("이미 존재하는 카테고리명입니다: " + request.getCategoryName());
		}

		// 필드 업데이트
		if (request.getCategoryName() != null) {
			category.setCategoryName(request.getCategoryName());
		}
		if (request.getParentId() != null) {
			if (request.getParentId().equals(categoryId)) {
				throw new IllegalArgumentException("자신을 부모 카테고리로 설정할 수 없습니다.");
			}
			PortfolioCategory parentCategory = portfolioCategoryRepository.findById(request.getParentId())
					.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 부모 카테고리입니다: " + request.getParentId()));
			category.setParent(parentCategory);
		} else if (request.getParentId() == null && category.getParent() != null) {
			category.setParent(null);
		}
		if (request.getSortOrder() != null) {
			category.setSortOrder(request.getSortOrder());
		}
		if (request.getIsActive() != null) {
			category.setIsActive(request.getIsActive());
		}

		PortfolioCategory updatedCategory = portfolioCategoryRepository.save(category);
		log.info("카테고리 수정 완료: ID = {}", categoryId);

		return PortfolioCategoryResponse.DetailDTO.from(updatedCategory);
	}

	/**
	 * 카테고리 단건 조회
	 */
	public PortfolioCategoryResponse.DetailDTO getCategory(Long categoryId) {
		log.info("카테고리 조회: ID = {}", categoryId);

		PortfolioCategory category = portfolioCategoryRepository.findById(categoryId)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다: " + categoryId));

		return PortfolioCategoryResponse.DetailDTO.from(category);
	}

	/**
	 * 활성화된 카테고리 목록 조회
	 */
	public List<PortfolioCategoryResponse.DetailDTO> getActiveCategories() {
		log.info("활성화된 카테고리 목록 조회");

		List<PortfolioCategory> categories = portfolioCategoryRepository.findByIsActiveTrue();

		return categories.stream()
				.map(PortfolioCategoryResponse.DetailDTO::from)
				.collect(Collectors.toList());
	}

	/**
	 * 전체 카테고리 목록 조회 (관리자용)
	 */
	public List<PortfolioCategoryResponse.DetailDTO> getAllCategories() {
		log.info("전체 카테고리 목록 조회");

		List<PortfolioCategory> categories = portfolioCategoryRepository.findAll();

		return categories.stream()
				.map(PortfolioCategoryResponse.DetailDTO::from)
				.collect(Collectors.toList());
	}

	/**
	 * 카테고리 삭제 (비활성화)
	 */
	@Transactional
	public void deleteCategory(Long categoryId) {
		log.info("카테고리 비활성화: ID = {}", categoryId);

		PortfolioCategory category = portfolioCategoryRepository.findById(categoryId)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다: " + categoryId));

		category.setIsActive(false);
		portfolioCategoryRepository.save(category);

		log.info("카테고리 비활성화 완료: ID = {}", categoryId);
	}
}