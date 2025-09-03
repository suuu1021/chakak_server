package com.green.chakak.chakak.portfolios.service;

import com.green.chakak.chakak.portfolios.domain.Portfolio;
import com.green.chakak.chakak.portfolios.domain.PortfolioCategory;
import com.green.chakak.chakak.portfolios.domain.PortfolioMap;
import com.green.chakak.chakak.portfolios.domain.PortfolioImage;
import com.green.chakak.chakak.portfolios.service.repository.PortfolioJpaRepository;
import com.green.chakak.chakak.portfolios.service.repository.PortfolioCategoryJpaRepository;
import com.green.chakak.chakak.portfolios.service.repository.PortfolioMapJpaRepository;
import com.green.chakak.chakak.portfolios.service.repository.PortfolioImageJpaRepository;
import com.green.chakak.chakak.portfolios.service.request.PortfolioRequest;
import com.green.chakak.chakak.portfolios.service.response.PortfolioResponse;
import com.green.chakak.chakak.photographer.domain.PhotographerProfile;
import com.green.chakak.chakak.photographer.service.repository.PhotographerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 포트폴리오 통합 서비스
 *
 * [포트폴리오 CRUD]
 * - createPortfolio()         : 포트폴리오 생성
 * - updatePortfolio()         : 포트폴리오 수정
 * - getPortfolioDetail()      : 상세 조회 (조회수 증가)
 * - deletePortfolio()         : 포트폴리오 삭제
 *
 * [포트폴리오 조회/검색]
 * - getPortfolioList()        : 목록 조회 (페이징, 정렬)
 * - searchPortfolios()        : 검색 (키워드 기반)
 * - getPhotographerPortfolios(): 사진작가별 조회
 *
 * [이미지 관리]
 * - addImage()                : 이미지 추가 (순서 자동 설정)
 * - deleteImage()             : 이미지 삭제
 *
 * [카테고리 매핑]
 * - addCategoryToPortfolio()  : 카테고리 추가
 *
 * [기타]
 * - increaseLike()            : 좋아요 증가
 */

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PortfolioService {

	private final PortfolioJpaRepository portfolioRepository;
	private final PhotographerRepository photographerRepository;
	private final PortfolioCategoryJpaRepository categoryRepository;
	private final PortfolioMapJpaRepository portfolioMapRepository;
	private final PortfolioImageJpaRepository portfolioImageRepository;

	// ============ 포트폴리오 CRUD ============

	/**
	 * 포트폴리오 생성
	 */
	@Transactional
	public PortfolioResponse.DetailDTO createPortfolio(PortfolioRequest.CreateDTO request) {
		log.info("포트폴리오 생성 시작: photographerId = {}", request.getPhotographerId());

		PhotographerProfile photographer = photographerRepository.findById(request.getPhotographerId())
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사진작가입니다: " + request.getPhotographerId()));

		Portfolio portfolio = request.toEntity(photographer);
		Portfolio savedPortfolio = portfolioRepository.save(portfolio);

		// 카테고리 매핑 생성
		if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
			createCategoryMappings(savedPortfolio, request.getCategoryIds());
		}

		log.info("포트폴리오 생성 완료: ID = {}", savedPortfolio.getPortfolioId());
		return PortfolioResponse.DetailDTO.from(savedPortfolio);
	}

	/**
	 * 포트폴리오 수정
	 */
	@Transactional
	public PortfolioResponse.DetailDTO updatePortfolio(Long portfolioId, PortfolioRequest.UpdateDTO request) {
		log.info("포트폴리오 수정 시작: portfolioId = {}", portfolioId);

		Portfolio portfolio = portfolioRepository.findById(portfolioId)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 포트폴리오입니다: " + portfolioId));

		// 필드 업데이트
		if (request.getTitle() != null) {
			portfolio.setTitle(request.getTitle());
		}
		if (request.getDescription() != null) {
			portfolio.setDescription(request.getDescription());
		}
		if (request.getThumbnailUrl() != null) {
			portfolio.setThumbnailUrl(request.getThumbnailUrl());
		}

		// 카테고리 매핑 업데이트
		if (request.getCategoryIds() != null) {
			updateCategoryMappings(portfolio, request.getCategoryIds());
		}

		log.info("포트폴리오 수정 완료: portfolioId = {}", portfolioId);
		return PortfolioResponse.DetailDTO.from(portfolio);
	}

	/**
	 * 포트폴리오 상세 조회 (조회수 증가)
	 */
	@Transactional
	public PortfolioResponse.DetailDTO getPortfolioDetail(Long portfolioId) {
		log.info("포트폴리오 상세 조회: portfolioId = {}", portfolioId);

		Portfolio portfolio = portfolioRepository.findById(portfolioId)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 포트폴리오입니다: " + portfolioId));

		// 조회수 증가 로직은 엔티티에 없으므로 주석 처리
		// portfolioRepository.incrementViewCount(portfolioId);

		return PortfolioResponse.DetailDTO.from(portfolio);
	}

	/**
	 * 포트폴리오 삭제
	 */
	@Transactional
	public void deletePortfolio(Long portfolioId) {
		log.info("포트폴리오 삭제 시작: portfolioId = {}", portfolioId);

		// 연관된 매핑과 이미지부터 삭제
		// PortfolioMapJpaRepository에 추가된 deleteByPortfolio 메서드 활용
		portfolioMapRepository.deleteByPortfolio_PortfolioId(portfolioId);
		// PortfolioImageJpaRepository에 추가된 deleteByPortfolio_PortfolioId 메서드 활용
		portfolioImageRepository.deleteByPortfolio_PortfolioId(portfolioId);

		portfolioRepository.deleteById(portfolioId);
		log.info("포트폴리오 삭제 완료: portfolioId = {}", portfolioId);
	}

	// ============ 포트폴리오 조회/검색 ============

	/**
	 * 포트폴리오 목록 조회 (페이징)
	 */
	public Page<PortfolioResponse.ListDTO> getPortfolioList(PortfolioRequest.SearchDTO searchRequest) {
		log.info("포트폴리오 목록 조회: sortBy = {}", searchRequest.getSortBy());

		Pageable pageable = PageRequest.of(searchRequest.getPage(), searchRequest.getSize());
		Page<Portfolio> portfolioPage;

		// 엔티티에 viewCount, likeCount, isPublic 필드가 없으므로, 최신순으로만 조회하도록 수정
		portfolioPage = portfolioRepository.findAllByOrderByCreatedAtDesc(pageable);

		return portfolioPage.map(PortfolioResponse.ListDTO::from);
	}

	/**
	 * 포트폴리오 검색
	 */
	public Page<PortfolioResponse.ListDTO> searchPortfolios(PortfolioRequest.SearchDTO searchRequest) {
		log.info("포트폴리오 검색: keyword = {}", searchRequest.getKeyword());

		Pageable pageable = PageRequest.of(searchRequest.getPage(), searchRequest.getSize());

		if (searchRequest.getKeyword() != null && !searchRequest.getKeyword().trim().isEmpty()) {
			return portfolioRepository.findByTitleContaining(searchRequest.getKeyword(), pageable)
					.map(PortfolioResponse.ListDTO::from);
		}
		return getPortfolioList(searchRequest);
	}

	/**
	 * 사진작가별 포트폴리오 조회
	 */
	public List<PortfolioResponse.ListDTO> getPhotographerPortfolios(Long photographerId) {
		log.info("사진작가 포트폴리오 조회: photographerId = {}", photographerId);

		PhotographerProfile photographer = photographerRepository.findById(photographerId)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사진작가입니다: " + photographerId));

		List<Portfolio> portfolios = portfolioRepository.findByPhotographerProfile(photographer);

		return portfolios.stream()
				.map(PortfolioResponse.ListDTO::from)
				.collect(Collectors.toList());
	}

	// ============ 이미지 관리 ============

	/**
	 * 포트폴리오에 이미지 추가
	 */
	@Transactional
	public PortfolioResponse.ImageDTO addImage(PortfolioRequest.AddImageDTO request) {
		log.info("이미지 추가: portfolioId = {}", request.getPortfolioId());

		Portfolio portfolio = portfolioRepository.findById(request.getPortfolioId())
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 포트폴리오입니다: " + request.getPortfolioId()));

		// isMain이 null일 경우 false로 설정
		Boolean isMain = (request.getIsMain() != null) ? request.getIsMain() : false;

		// 이미 메인 이미지가 존재하면 해제
		if (isMain) {
			portfolioImageRepository.findByPortfolioAndIsMainTrue(portfolio)
					.ifPresent(mainImage -> mainImage.setIsMain(false));
		}

		PortfolioImage image = new PortfolioImage();
		image.setPortfolio(portfolio);
		image.setImageUrl(request.getImageUrl());
		image.setIsMain(isMain);

		PortfolioImage savedImage = portfolioImageRepository.save(image);
		log.info("이미지 추가 완료: imageId = {}", savedImage.getPortfolioImageId());

		return PortfolioResponse.ImageDTO.from(savedImage);
	}

	/**
	 * 이미지 삭제
	 */
	@Transactional
	public void deleteImage(Long imageId) {
		log.info("이미지 삭제: imageId = {}", imageId);

		portfolioImageRepository.deleteById(imageId);
		log.info("이미지 삭제 완료: imageId = {}", imageId);
	}

	// ============ 카테고리 매핑 관리 ============

	/**
	 * 포트폴리오에 카테고리 추가
	 */
	@Transactional
	public void addCategoryToPortfolio(Long portfolioId, Long categoryId) {
		log.info("카테고리 추가: portfolioId = {}, categoryId = {}", portfolioId, categoryId);

		Portfolio portfolio = portfolioRepository.findById(portfolioId)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 포트폴리오입니다: " + portfolioId));

		PortfolioCategory category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다: " + categoryId));

		// existsByPortfolioAndPortfolioCategory 메서드 활용
		if (!portfolioMapRepository.existsByPortfolioAndPortfolioCategory(portfolio, category)) {
			PortfolioMap mapping = new PortfolioMap();
			mapping.setPortfolio(portfolio);
			mapping.setPortfolioCategory(category);
			portfolioMapRepository.save(mapping);
		}
	}

	// ============ Private 헬퍼 메서드 ============
	private void createCategoryMappings(Portfolio portfolio, List<Long> categoryIds) {
		for (Long categoryId : categoryIds) {
			PortfolioCategory category = categoryRepository.findById(categoryId)
					.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다: " + categoryId));

			// existsByPortfolioAndPortfolioCategory 메서드 활용
			if (!portfolioMapRepository.existsByPortfolioAndPortfolioCategory(portfolio, category)) {
				PortfolioMap mapping = new PortfolioMap();
				mapping.setPortfolio(portfolio);
				mapping.setPortfolioCategory(category);
				portfolioMapRepository.save(mapping);
			}
		}
	}

	private void updateCategoryMappings(Portfolio portfolio, List<Long> categoryIds) {
		// deleteByPortfolio 메서드 활용
		portfolioMapRepository.deleteByPortfolio(portfolio);
		if (categoryIds != null && !categoryIds.isEmpty()) {
			createCategoryMappings(portfolio, categoryIds);
		}
	}
}