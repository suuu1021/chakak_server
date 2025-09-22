package com.green.chakak.chakak.portfolios.service;

import com.green.chakak.chakak._global.errors.exception.Exception400;
import com.green.chakak.chakak._global.errors.exception.Exception403;
import com.green.chakak.chakak._global.errors.exception.Exception404;
import com.green.chakak.chakak._global.errors.exception.Exception500;
import com.green.chakak.chakak._global.utils.FileUploadUtil;
import com.green.chakak.chakak.account.domain.LoginUser;
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

import java.util.ArrayList;
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
	private final FileUploadUtil fileUploadUtil; // Base64 처리용 유틸리티 추가

	// ============ 포트폴리오 CRUD ============

	/**
	 * 포트폴리오 생성
	 */
	@Transactional
	public PortfolioResponse.DetailDTO createPortfolio(PortfolioRequest.CreateDTO request, LoginUser loginUser) {
		try {
			log.info("포트폴리오 생성 시작: userId = {}", loginUser.getId());

			// 입력값 검증
			validateCreateRequest(request);
			validateLoginUser(loginUser);

			// 로그인한 사용자의 사진작가 프로필 조회
			PhotographerProfile photographer = photographerRepository.findByUser_UserId(loginUser.getId())
					.orElseThrow(() -> new Exception404("사진작가 프로필이 존재하지 않습니다. 사진작가 등록을 먼저 해주세요."));

			// 썸네일 Base64를 파일로 저장
			String thumbnailUrl = fileUploadUtil.saveBase64Image(request.getThumbnailUrl(), "thumbnail");

			// Portfolio 엔티티 생성 (실제 파일 URL로 설정)
			Portfolio portfolio = new Portfolio();
			portfolio.setPhotographerProfile(photographer);
			portfolio.setTitle(request.getTitle());
			portfolio.setDescription(request.getDescription());
			portfolio.setThumbnailUrl(thumbnailUrl); // 변환된 URL 설정

			Portfolio savedPortfolio = portfolioRepository.save(portfolio);

			// 카테고리 매핑 생성
			if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
				createCategoryMappings(savedPortfolio, request.getCategoryIds());
			}

			// 이미지 등록
			if (request.getImageInfoList() != null && !request.getImageInfoList().isEmpty()) {
				createPortfolioImages(savedPortfolio, request.getImageInfoList());
			}

			// 최종 포트폴리오 조회 (이미지와 카테고리 포함)
			Portfolio finalPortfolio = portfolioRepository.findById(savedPortfolio.getPortfolioId())
					.orElseThrow(() -> new Exception500("생성된 포트폴리오 조회 실패"));

			log.info("포트폴리오 생성 완료: ID = {}", savedPortfolio.getPortfolioId());
			return PortfolioResponse.DetailDTO.from(finalPortfolio);

		} catch (Exception404 | Exception400 e) {
			throw e;
		} catch (Exception e) {
			log.error("포트폴리오 생성 중 예상치 못한 오류 발생", e);
			throw new Exception500("포트폴리오 생성 중 서버 오류가 발생했습니다");
		}
	}

	/**
	 * 포트폴리오 이미지들 생성
	 */
	private void createPortfolioImages(Portfolio portfolio, List<PortfolioRequest.AddImageDTO> imageInfoList) {
		boolean hasMainImage = false;

		for (PortfolioRequest.AddImageDTO imageInfo : imageInfoList) {
			// 입력값 검증
			if (imageInfo.getImageData() == null || imageInfo.getImageData().trim().isEmpty()) {
				log.warn("빈 이미지 데이터 건너뜀");
				continue;
			}

			try {
				// Base64 이미지를 파일로 저장하고 URL 획득
				String imageUrl = fileUploadUtil.saveBase64Image(
						imageInfo.getImageData(),
						imageInfo.getOriginalFileName()
				);

				// 메인 이미지 중복 체크
				boolean isMain = imageInfo.getIsMain() != null ? imageInfo.getIsMain() : false;
				if (isMain && hasMainImage) {
					log.warn("메인 이미지가 이미 존재하므로 일반 이미지로 설정");
					isMain = false;
				}

				PortfolioImage image = new PortfolioImage();
				image.setPortfolio(portfolio);
				image.setImageUrl(imageUrl); // 변환된 파일 URL 설정
				image.setIsMain(isMain);

				portfolioImageRepository.save(image);

				if (isMain) {
					hasMainImage = true;
				}

				log.debug("이미지 등록 완료: {} (메인: {})", imageUrl, isMain);

			} catch (Exception e) {
				log.error("이미지 저장 실패: {}", e.getMessage());
				// 개별 이미지 실패는 전체 트랜잭션을 롤백하지 않고 건너뜀
			}
		}

		log.info("총 {}개 이미지 처리 완료 (메인 이미지 포함: {})", imageInfoList.size(), hasMainImage);
	}

	/**
	 * 포트폴리오 수정
	 */
	@Transactional
	public PortfolioResponse.DetailDTO updatePortfolio(Long portfolioId, PortfolioRequest.UpdateDTO request, LoginUser loginUser) {
		try {
			log.info("포트폴리오 수정 시작: portfolioId = {}", portfolioId);

			validatePortfolioId(portfolioId);
			validateUpdateRequest(request);
			validateLoginUser(loginUser);

			Portfolio portfolio = portfolioRepository.findById(portfolioId)
					.orElseThrow(() -> new Exception404("존재하지 않는 포트폴리오입니다: " + portfolioId));

			// 권한 체크
			if (portfolio.getPhotographerProfile() != null &&
					portfolio.getPhotographerProfile().getUser() != null &&
					!portfolio.getPhotographerProfile().getUser().getUserId().equals(loginUser.getId())) {
				throw new Exception403("포트폴리오 수정 권한이 없습니다");
			}

			// 기존 썸네일 파일 삭제 (새 썸네일이 있는 경우)
			String oldThumbnailUrl = portfolio.getThumbnailUrl();

			// 필드 업데이트
			if (request.getTitle() != null) {
				portfolio.setTitle(request.getTitle());
			}
			if (request.getDescription() != null) {
				portfolio.setDescription(request.getDescription());
			}
			if (request.getThumbnailUrl() != null) {
				// 새 썸네일 Base64를 파일로 저장
				String newThumbnailUrl = fileUploadUtil.saveBase64Image(request.getThumbnailUrl(), "thumbnail");
				portfolio.setThumbnailUrl(newThumbnailUrl);

				// 기존 썸네일 파일 삭제
				if (oldThumbnailUrl != null) {
					fileUploadUtil.deleteFile(oldThumbnailUrl);
				}
			}

			// 카테고리 매핑 업데이트
			if (request.getCategoryIds() != null) {
				updateCategoryMappings(portfolio, request.getCategoryIds());
			}

			// 이미지 업데이트
			if (request.getImageInfoList() != null) {
				updatePortfolioImages(portfolio, request.getImageInfoList());
			}

			// 최종 포트폴리오 조회
			Portfolio finalPortfolio = portfolioRepository.findById(portfolioId)
					.orElseThrow(() -> new Exception500("수정된 포트폴리오 조회 실패"));

			log.info("포트폴리오 수정 완료: portfolioId = {}", portfolioId);
			return PortfolioResponse.DetailDTO.from(finalPortfolio);

		} catch (Exception403 | Exception404 | Exception400 e) {
			throw e;
		} catch (Exception e) {
			log.error("포트폴리오 수정 중 예상치 못한 오류 발생", e);
			throw new Exception500("포트폴리오 수정 중 서버 오류가 발생했습니다");
		}
	}

	/**
	 * 포트폴리오 이미지들 업데이트 - 개별 삭제 방식
	 */
	private void updatePortfolioImages(Portfolio portfolio, List<PortfolioRequest.AddImageDTO> imageInfoList) {
		log.info("포트폴리오 이미지 업데이트 시작: portfolioId = {}", portfolio.getPortfolioId());

		// 1. 기존 이미지들을 개별적으로 삭제
		List<PortfolioImage> existingImages = portfolioImageRepository.findByPortfolioOrderByCreatedAt(portfolio);
		log.info("기존 이미지 개수: {}", existingImages.size());

		for (PortfolioImage image : existingImages) {
			if (image.getImageUrl() != null) {
				boolean fileDeleted = fileUploadUtil.deleteFile(image.getImageUrl());
				log.info("이미지 파일 삭제: {} - 결과: {}", image.getImageUrl(), fileDeleted);
			}
			portfolioImageRepository.delete(image);
		}

		// 2. 새 이미지들 등록
		if (imageInfoList != null && !imageInfoList.isEmpty()) {
			log.info("새 이미지 등록 시작: {}개", imageInfoList.size());
			createPortfolioImages(portfolio, imageInfoList);
		}

		log.info("포트폴리오 이미지 업데이트 완료");
	}

	/**
	 * 포트폴리오 상세 조회
	 */
	@Transactional
	public PortfolioResponse.DetailDTO getPortfolioDetail(Long portfolioId) {
		try {
			log.info("포트폴리오 상세 조회: portfolioId = {}", portfolioId);

			validatePortfolioId(portfolioId);

			Portfolio portfolio = portfolioRepository.findById(portfolioId)
					.orElseThrow(() -> new Exception404("존재하지 않는 포트폴리오입니다: " + portfolioId));

			return PortfolioResponse.DetailDTO.from(portfolio);

		} catch (Exception404 | Exception400 e) {
			throw e;
		} catch (Exception e) {
			log.error("포트폴리오 상세 조회 중 예상치 못한 오류 발생", e);
			throw new Exception500("포트폴리오 조회 중 서버 오류가 발생했습니다");
		}
	}

	/**
	 * 포트폴리오 삭제 - 개별 삭제 방식
	 */
	@Transactional
	public void deletePortfolio(Long portfolioId, LoginUser loginUser) {
		try {
			log.info("포트폴리오 삭제 시작: portfolioId = {}", portfolioId);

			validatePortfolioId(portfolioId);
			validateLoginUser(loginUser);

			Portfolio portfolio = portfolioRepository.findById(portfolioId)
					.orElseThrow(() -> new Exception404("존재하지 않는 포트폴리오입니다: " + portfolioId));

			// 권한 체크
			if (!portfolio.getPhotographerProfile().getUser().getUserId().equals(loginUser.getId())) {
				throw new Exception403("포트폴리오 삭제 권한이 없습니다");
			}

			// 1. 연관된 데이터들을 개별적으로 삭제
			// 포트폴리오 맵 삭제
			List<PortfolioMap> portfolioMaps = portfolioMapRepository.findByPortfolio(portfolio);
			for (PortfolioMap map : portfolioMaps) {
				portfolioMapRepository.delete(map);
			}

			// 포트폴리오 이미지 삭제 (파일과 DB 레코드)
			List<PortfolioImage> portfolioImages = portfolioImageRepository.findByPortfolioOrderByCreatedAt(portfolio);
			for (PortfolioImage image : portfolioImages) {
				if (image.getImageUrl() != null) {
					fileUploadUtil.deleteFile(image.getImageUrl());
				}
				portfolioImageRepository.delete(image);
			}

			// 2. 썸네일 파일 삭제
			if (portfolio.getThumbnailUrl() != null) {
				fileUploadUtil.deleteFile(portfolio.getThumbnailUrl());
			}

			// 3. 포트폴리오 삭제
			portfolioRepository.delete(portfolio);

			log.info("포트폴리오 삭제 완료: portfolioId = {}", portfolioId);

		} catch (Exception403 | Exception404 | Exception400 e) {
			throw e;
		} catch (Exception e) {
			log.error("포트폴리오 삭제 중 예상치 못한 오류 발생", e);
			throw new Exception500("포트폴리오 삭제 중 서버 오류가 발생했습니다: " + e.getMessage());
		}
	}

	// ============ 포트폴리오 조회/검색 ============

	/**
	 * 포트폴리오 목록 조회 (페이징)
	 */
	public Page<PortfolioResponse.ListDTO> getPortfolioList(PortfolioRequest.SearchDTO searchRequest) {
		try {
			log.info("포트폴리오 목록 조회: sortBy = {}", searchRequest.getSortBy());

			validateSearchRequest(searchRequest);

			Pageable pageable = PageRequest.of(searchRequest.getPage(), searchRequest.getSize());
			Page<Portfolio> portfolioPage;

			// 엔티티에 viewCount, likeCount, isPublic 필드가 없으므로, 최신순으로만 조회하도록 수정
			portfolioPage = portfolioRepository.findAllByOrderByCreatedAtDesc(pageable);

			return portfolioPage.map(PortfolioResponse.ListDTO::from);

		} catch (Exception400 e) {
			throw e;
		} catch (Exception e) {
			log.error("포트폴리오 목록 조회 중 예상치 못한 오류 발생", e);
			throw new Exception500("포트폴리오 목록 조회 중 서버 오류가 발생했습니다");
		}
	}

	/**
	 * 포트폴리오 검색
	 */
	public Page<PortfolioResponse.ListDTO> searchPortfolios(PortfolioRequest.SearchDTO searchRequest) {
		try {
			log.info("포트폴리오 검색: keyword = {}", searchRequest.getKeyword());

			validateSearchRequest(searchRequest);

			Pageable pageable = PageRequest.of(searchRequest.getPage(), searchRequest.getSize());

			if (searchRequest.getKeyword() != null && !searchRequest.getKeyword().trim().isEmpty()) {
				return portfolioRepository.findByTitleContaining(searchRequest.getKeyword(), pageable)
						.map(PortfolioResponse.ListDTO::from);
			}
			return getPortfolioList(searchRequest);

		} catch (Exception400 e) {
			throw e;
		} catch (Exception e) {
			log.error("포트폴리오 검색 중 예상치 못한 오류 발생", e);
			throw new Exception500("포트폴리오 검색 중 서버 오류가 발생했습니다");
		}
	}

	/**
	 * 사진작가별 포트폴리오 조회
	 */
	public List<PortfolioResponse.ListDTO> getPhotographerPortfolios(Long photographerId) {
		try {
			log.info("사진작가 포트폴리오 조회: photographerId = {}", photographerId);

			validatePhotographerId(photographerId);

			PhotographerProfile photographer = photographerRepository.findById(photographerId)
					.orElseThrow(() -> new Exception404("존재하지 않는 사진작가입니다: " + photographerId));

			List<Portfolio> portfolios = portfolioRepository.findByPhotographerProfile(photographer);

			return portfolios.stream()
					.map(PortfolioResponse.ListDTO::from)
					.collect(Collectors.toList());

		} catch (Exception404 | Exception400 e) {
			throw e;
		} catch (Exception e) {
			log.error("사진작가 포트폴리오 조회 중 예상치 못한 오류 발생", e);
			throw new Exception500("사진작가 포트폴리오 조회 중 서버 오류가 발생했습니다");
		}
	}

	// ============ 이미지 관리 ============

	/**
	 * 포트폴리오에 이미지 추가
	 */
	@Transactional
	public PortfolioResponse.ImageDTO addImage(PortfolioRequest.AddImageDTO request) {
		try {
			log.info("이미지 추가: portfolioId = {}", request.getPortfolioId());

			validateAddImageRequest(request);

			Portfolio portfolio = portfolioRepository.findById(request.getPortfolioId())
					.orElseThrow(() -> new Exception404("존재하지 않는 포트폴리오입니다: " + request.getPortfolioId()));

			// isMain이 null일 경우 false로 설정
			Boolean isMain = (request.getIsMain() != null) ? request.getIsMain() : false;

			// 이미 메인 이미지가 존재하면 해제
			if (isMain) {
				portfolioImageRepository.findByPortfolioAndIsMainTrue(portfolio)
						.ifPresent(mainImage -> mainImage.setIsMain(false));
			}

			PortfolioImage image = new PortfolioImage();
			image.setPortfolio(portfolio);
			image.setImageUrl(request.getImageData());
			image.setIsMain(isMain);

			PortfolioImage savedImage = portfolioImageRepository.save(image);
			log.info("이미지 추가 완료: imageId = {}", savedImage.getPortfolioImageId());

			return PortfolioResponse.ImageDTO.from(savedImage);

		} catch (Exception404 | Exception400 e) {
			throw e;
		} catch (Exception e) {
			log.error("이미지 추가 중 예상치 못한 오류 발생", e);
			throw new Exception500("이미지 추가 중 서버 오류가 발생했습니다");
		}
	}

	/**
	 * 이미지 삭제
	 */
	@Transactional
	public void deleteImage(Long imageId) {
		try {
			log.info("이미지 삭제: imageId = {}", imageId);

			validateImageId(imageId);

			if (!portfolioImageRepository.existsById(imageId)) {
				throw new Exception404("존재하지 않는 이미지입니다: " + imageId);
			}

			portfolioImageRepository.deleteById(imageId);
			log.info("이미지 삭제 완료: imageId = {}", imageId);

		} catch (Exception404 | Exception400 e) {
			throw e;
		} catch (Exception e) {
			log.error("이미지 삭제 중 예상치 못한 오류 발생", e);
			throw new Exception500("이미지 삭제 중 서버 오류가 발생했습니다");
		}
	}

	// ============ 카테고리 매핑 관리 ============

	/**
	 * 포트폴리오에 카테고리 추가
	 */
	@Transactional
	public void addCategoryToPortfolio(Long portfolioId, Long categoryId) {
		try {
			log.info("카테고리 추가: portfolioId = {}, categoryId = {}", portfolioId, categoryId);

			validatePortfolioId(portfolioId);
			validateCategoryId(categoryId);

			Portfolio portfolio = portfolioRepository.findById(portfolioId)
					.orElseThrow(() -> new Exception404("존재하지 않는 포트폴리오입니다: " + portfolioId));

			PortfolioCategory category = categoryRepository.findById(categoryId)
					.orElseThrow(() -> new Exception404("존재하지 않는 카테고리입니다: " + categoryId));

			if (!portfolioMapRepository.existsByPortfolioAndPortfolioCategory(portfolio, category)) {
				PortfolioMap mapping = new PortfolioMap();
				mapping.setPortfolio(portfolio);
				mapping.setPortfolioCategory(category);
				portfolioMapRepository.save(mapping);
			}

		} catch (Exception404 | Exception400 e) {
			throw e;
		} catch (Exception e) {
			log.error("카테고리 추가 중 예상치 못한 오류 발생", e);
			throw new Exception500("카테고리 추가 중 서버 오류가 발생했습니다");
		}
	}

	// ============ Private 헬퍼 메서드 ============

	private void createCategoryMappings(Portfolio portfolio, List<Long> categoryIds) {
		for (Long categoryId : categoryIds) {
			validateCategoryId(categoryId);

			PortfolioCategory category = categoryRepository.findById(categoryId)
					.orElseThrow(() -> new Exception404("존재하지 않는 카테고리입니다: " + categoryId));

			if (!portfolioMapRepository.existsByPortfolioAndPortfolioCategory(portfolio, category)) {
				PortfolioMap mapping = new PortfolioMap();
				mapping.setPortfolio(portfolio);
				mapping.setPortfolioCategory(category);
				portfolioMapRepository.save(mapping);
			}
		}
	}

	private void updateCategoryMappings(Portfolio portfolio, List<Long> categoryIds) {
		portfolioMapRepository.deleteByPortfolio(portfolio);
		if (categoryIds != null && !categoryIds.isEmpty()) {
			createCategoryMappings(portfolio, categoryIds);
		}
	}

	// ============ 유효성 검증 메서드 ============

	private void validateCreateRequest(PortfolioRequest.CreateDTO request) {
		if (request == null) {
			throw new Exception400("포트폴리오 생성 요청 데이터가 없습니다");
		}
		if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
			throw new Exception400("포트폴리오 제목은 필수입니다");
		}
	}

	private void validateLoginUser(LoginUser loginUser) {
		if (loginUser == null) {
			throw new Exception400("로그인 사용자 정보가 없습니다");
		}
		if (loginUser.getId() == null || loginUser.getId() <= 0) {
			throw new Exception400("유효하지 않은 사용자 ID입니다");
		}
	}

	private void validateUpdateRequest(PortfolioRequest.UpdateDTO request) {
		if (request == null) {
			throw new Exception400("포트폴리오 수정 요청 데이터가 없습니다");
		}
	}

	private void validateAddImageRequest(PortfolioRequest.AddImageDTO request) {
		if (request == null) {
			throw new Exception400("이미지 추가 요청 데이터가 없습니다");
		}
		validatePortfolioId(request.getPortfolioId());
		if (request.getImageData() == null || request.getImageData().trim().isEmpty()) {
			throw new Exception400("이미지 URL은 필수입니다");
		}
	}

	private void validateSearchRequest(PortfolioRequest.SearchDTO request) {
		if (request == null) {
			throw new Exception400("검색 요청 데이터가 없습니다");
		}
		if (request.getPage() < 0) {
			throw new Exception400("페이지 번호는 0 이상이어야 합니다");
		}
		if (request.getSize() <= 0 || request.getSize() > 100) {
			throw new Exception400("페이지 크기는 1 이상 100 이하여야 합니다");
		}
	}

	private void validatePortfolioId(Long portfolioId) {
		if (portfolioId == null || portfolioId <= 0) {
			throw new Exception400("유효하지 않은 포트폴리오 ID입니다: " + portfolioId);
		}
	}

	private void validatePhotographerId(Long photographerId) {
		if (photographerId == null || photographerId <= 0) {
			throw new Exception400("유효하지 않은 사진작가 ID입니다: " + photographerId);
		}
	}

	private void validateCategoryId(Long categoryId) {
		if (categoryId == null || categoryId <= 0) {
			throw new Exception400("유효하지 않은 카테고리 ID입니다: " + categoryId);
		}
	}

	private void validateImageId(Long imageId) {
		if (imageId == null || imageId <= 0) {
			throw new Exception400("유효하지 않은 이미지 ID입니다: " + imageId);
		}
	}
}