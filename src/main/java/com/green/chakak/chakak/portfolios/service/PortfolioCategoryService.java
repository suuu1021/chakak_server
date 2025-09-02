package com.green.chakak.chakak.portfolios.service;

import com.green.chakak.chakak.portfolios.domain.PortfolioCategory;
import com.green.chakak.chakak.portfolios.service.repository.PortfolioCategoryJpaRepository;
import com.green.chakak.chakak.portfolios.service.request.PortfolioRequest;
import com.green.chakak.chakak.portfolios.service.response.PortfolioResponse;
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
	public PortfolioResponse.CategoryDTO createCategory(PortfolioRequest.CategoryDTO request) {
		log.info("카테고리 생성 시작: {}", request.getCategoryName());

		// 중복 체크
		if (portfolioCategoryRepository.existsByCategoryName(request.getCategoryName())) {
			throw new IllegalArgumentException("이미 존재하는 카테고리명입니다: " + request.getCategoryName());
		}

		// 엔티티 생성 및 저장
		PortfolioCategory category = new PortfolioCategory();
		category.setCategoryName(request.getCategoryName());
		category.setCategoryDescription(request.getCategoryDescription());
		category.setIsActive(request.getIsActive());

		PortfolioCategory savedCategory = portfolioCategoryRepository.save(category);
		log.info("카테고리 생성 완료: ID = {}", savedCategory.getCategoryId());

		return PortfolioResponse.CategoryDTO.from(savedCategory);
	}

	/**
	 * 카테고리 수정
	 */
	@Transactional
	public PortfolioResponse.CategoryDTO updateCategory(Long categoryId, PortfolioRequest.CategoryDTO request) {
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
		if (request.getCategoryDescription() != null) {
			category.setCategoryDescription(request.getCategoryDescription());
		}
		if (request.getIsActive() != null) {
			category.setIsActive(request.getIsActive());
		}

		PortfolioCategory updatedCategory = portfolioCategoryRepository.save(category);
		log.info("카테고리 수정 완료: ID = {}", categoryId);

		return PortfolioResponse.CategoryDTO.from(updatedCategory);
	}

	/**
	 * 카테고리 단건 조회
	 */
	public PortfolioResponse.CategoryDTO getCategory(Long categoryId) {
		log.info("카테고리 조회: ID = {}", categoryId);

		PortfolioCategory category = portfolioCategoryRepository.findById(categoryId)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다: " + categoryId));

		return PortfolioResponse.CategoryDTO.from(category);
	}

	/**
	 * 활성화된 카테고리 목록 조회
	 */
	public List<PortfolioResponse.CategoryDTO> getActiveCategories() {
		log.info("활성화된 카테고리 목록 조회");

		List<PortfolioCategory> categories = portfolioCategoryRepository.findByIsActiveTrue();

		return categories.stream()
				.map(PortfolioResponse.CategoryDTO::from)
				.collect(Collectors.toList());
	}

	/**
	 * 전체 카테고리 목록 조회 (관리자용)
	 */
	public List<PortfolioResponse.CategoryDTO> getAllCategories() {
		log.info("전체 카테고리 목록 조회");

		List<PortfolioCategory> categories = portfolioCategoryRepository.findAll();

		return categories.stream()
				.map(PortfolioResponse.CategoryDTO::from)
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

		// 논리 삭제 (비활성화)
		category.setIsActive(false);
		portfolioCategoryRepository.save(category);

		log.info("카테고리 비활성화 완료: ID = {}", categoryId);
	}
}