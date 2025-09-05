package com.green.chakak.chakak.portfolios.service;

import com.green.chakak.chakak._global.errors.exception.Exception400;
import com.green.chakak.chakak._global.errors.exception.Exception404;
import com.green.chakak.chakak._global.errors.exception.Exception500;
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
		try {
			log.info("카테고리 생성 시작: {}", request.getCategoryName());

			// 입력값 검증
			validateCreateRequest(request);

			// 중복 체크
			if (portfolioCategoryRepository.existsByCategoryName(request.getCategoryName())) {
				throw new Exception400("이미 존재하는 카테고리명입니다: " + request.getCategoryName());
			}

			PortfolioCategory category = new PortfolioCategory();
			category.setCategoryName(request.getCategoryName());

			// 부모 카테고리 설정
			if (request.getParentId() != null) {
				validateCategoryId(request.getParentId());
				PortfolioCategory parentCategory = portfolioCategoryRepository.findById(request.getParentId())
						.orElseThrow(() -> new Exception404("존재하지 않는 부모 카테고리입니다: " + request.getParentId()));
				category.setParent(parentCategory);
			}

			// 정렬 순서 설정 (기본값 처리)
			category.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);

			PortfolioCategory savedCategory = portfolioCategoryRepository.save(category);
			log.info("카테고리 생성 완료: ID = {}", savedCategory.getPortfolioCategoryId());

			return PortfolioCategoryResponse.DetailDTO.from(savedCategory);

		} catch (Exception400 | Exception404 e) {
			throw e; // 이미 정의된 예외는 그대로 전파
		} catch (Exception e) {
			log.error("카테고리 생성 중 예상치 못한 오류 발생", e);
			throw new Exception500("카테고리 생성 중 서버 오류가 발생했습니다");
		}
	}

	/**
	 * 카테고리 수정
	 */
	@Transactional
	public PortfolioCategoryResponse.DetailDTO updateCategory(Long categoryId, PortfolioCategoryRequest.UpdateDTO request) {
		try {
			log.info("카테고리 수정 시작: ID = {}", categoryId);

			// 입력값 검증
			validateCategoryId(categoryId);
			validateUpdateRequest(request);

			PortfolioCategory category = portfolioCategoryRepository.findById(categoryId)
					.orElseThrow(() -> new Exception404("존재하지 않는 카테고리입니다: " + categoryId));

			// 카테고리명 중복 체크 (자기 자신 제외)
			if (request.getCategoryName() != null &&
					!request.getCategoryName().equals(category.getCategoryName()) &&
					portfolioCategoryRepository.existsByCategoryName(request.getCategoryName())) {
				throw new Exception400("이미 존재하는 카테고리명입니다: " + request.getCategoryName());
			}

			// 필드 업데이트
			if (request.getCategoryName() != null) {
				category.setCategoryName(request.getCategoryName());
			}

			// 부모 카테고리 설정
			if (request.getParentId() != null) {
				// 자기 자신을 부모로 설정하는 것을 방지
				if (request.getParentId().equals(categoryId)) {
					throw new Exception400("자신을 부모 카테고리로 설정할 수 없습니다");
				}

				validateCategoryId(request.getParentId());
				PortfolioCategory parentCategory = portfolioCategoryRepository.findById(request.getParentId())
						.orElseThrow(() -> new Exception404("존재하지 않는 부모 카테고리입니다: " + request.getParentId()));
				category.setParent(parentCategory);
			} else if (request.getParentId() == null && category.getParent() != null) {
				// 명시적으로 null을 전달한 경우 부모 카테고리 제거
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

		} catch (Exception400 | Exception404 e) {
			throw e;
		} catch (Exception e) {
			log.error("카테고리 수정 중 예상치 못한 오류 발생", e);
			throw new Exception500("카테고리 수정 중 서버 오류가 발생했습니다");
		}
	}

	/**
	 * 카테고리 단건 조회
	 */
	public PortfolioCategoryResponse.DetailDTO getCategory(Long categoryId) {
		try {
			log.info("카테고리 조회: ID = {}", categoryId);

			validateCategoryId(categoryId);

			PortfolioCategory category = portfolioCategoryRepository.findById(categoryId)
					.orElseThrow(() -> new Exception404("존재하지 않는 카테고리입니다: " + categoryId));

			return PortfolioCategoryResponse.DetailDTO.from(category);

		} catch (Exception400 | Exception404 e) {
			throw e;
		} catch (Exception e) {
			log.error("카테고리 조회 중 예상치 못한 오류 발생", e);
			throw new Exception500("카테고리 조회 중 서버 오류가 발생했습니다");
		}
	}

	/**
	 * 활성화된 카테고리 목록 조회
	 */
	public List<PortfolioCategoryResponse.DetailDTO> getActiveCategories() {
		try {
			log.info("활성화된 카테고리 목록 조회");

			List<PortfolioCategory> categories = portfolioCategoryRepository.findByIsActiveTrue();

			return categories.stream()
					.map(PortfolioCategoryResponse.DetailDTO::from)
					.collect(Collectors.toList());

		} catch (Exception e) {
			log.error("활성화된 카테고리 목록 조회 중 예상치 못한 오류 발생", e);
			throw new Exception500("카테고리 목록 조회 중 서버 오류가 발생했습니다");
		}
	}

	/**
	 * 전체 카테고리 목록 조회 (관리자용)
	 */
	public List<PortfolioCategoryResponse.DetailDTO> getAllCategories() {
		try {
			log.info("전체 카테고리 목록 조회");

			List<PortfolioCategory> categories = portfolioCategoryRepository.findAll();

			return categories.stream()
					.map(PortfolioCategoryResponse.DetailDTO::from)
					.collect(Collectors.toList());

		} catch (Exception e) {
			log.error("전체 카테고리 목록 조회 중 예상치 못한 오류 발생", e);
			throw new Exception500("카테고리 목록 조회 중 서버 오류가 발생했습니다");
		}
	}

	/**
	 * 카테고리 삭제 (비활성화)
	 */
	@Transactional
	public void deleteCategory(Long categoryId) {
		try {
			log.info("카테고리 비활성화: ID = {}", categoryId);

			validateCategoryId(categoryId);

			PortfolioCategory category = portfolioCategoryRepository.findById(categoryId)
					.orElseThrow(() -> new Exception404("존재하지 않는 카테고리입니다: " + categoryId));

			// 이미 비활성화된 카테고리인지 확인
			if (!category.getIsActive()) {
				throw new Exception400("이미 비활성화된 카테고리입니다: " + categoryId);
			}

			category.setIsActive(false);
			portfolioCategoryRepository.save(category);

			log.info("카테고리 비활성화 완료: ID = {}", categoryId);

		} catch (Exception400 | Exception404 e) {
			throw e;
		} catch (Exception e) {
			log.error("카테고리 비활성화 중 예상치 못한 오류 발생", e);
			throw new Exception500("카테고리 비활성화 중 서버 오류가 발생했습니다");
		}
	}

	// ============ 유효성 검증 메서드 ============

	/**
	 * 카테고리 생성 요청 유효성 검증
	 */
	private void validateCreateRequest(PortfolioCategoryRequest.CreateDTO request) {
		if (request == null) {
			throw new Exception400("카테고리 생성 요청 데이터가 없습니다");
		}
		if (request.getCategoryName() == null || request.getCategoryName().trim().isEmpty()) {
			throw new Exception400("카테고리명은 필수입니다");
		}
		if (request.getCategoryName().length() > 50) {
			throw new Exception400("카테고리명은 50자를 초과할 수 없습니다");
		}
		if (request.getSortOrder() != null && request.getSortOrder() < 0) {
			throw new Exception400("정렬 순서는 0 이상이어야 합니다");
		}
	}

	/**
	 * 카테고리 수정 요청 유효성 검증
	 */
	private void validateUpdateRequest(PortfolioCategoryRequest.UpdateDTO request) {
		if (request == null) {
			throw new Exception400("카테고리 수정 요청 데이터가 없습니다");
		}
		if (request.getCategoryName() != null) {
			if (request.getCategoryName().trim().isEmpty()) {
				throw new Exception400("카테고리명은 빈 값일 수 없습니다");
			}
			if (request.getCategoryName().length() > 50) {
				throw new Exception400("카테고리명은 50자를 초과할 수 없습니다");
			}
		}
		if (request.getSortOrder() != null && request.getSortOrder() < 0) {
			throw new Exception400("정렬 순서는 0 이상이어야 합니다");
		}
	}

	/**
	 * 카테고리 ID 유효성 검증
	 */
	private void validateCategoryId(Long categoryId) {
		if (categoryId == null || categoryId <= 0) {
			throw new Exception400("유효하지 않은 카테고리 ID입니다: " + categoryId);
		}
	}
}