package com.green.chakak.chakak.portfolios.controller;

import com.green.chakak.chakak.global.utils.ApiUtil;
import com.green.chakak.chakak.portfolios.service.PortfolioService;
import com.green.chakak.chakak.portfolios.service.request.PortfolioRequest;
import com.green.chakak.chakak.portfolios.service.response.PortfolioResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * 포트폴리오 컨트롤러
 *
 * [포트폴리오 CRUD]
 * POST   /api/portfolios                     : 포트폴리오 생성
 * GET    /api/portfolios/{portfolioId}       : 포트폴리오 상세 조회
 * PUT    /api/portfolios/{portfolioId}       : 포트폴리오 수정
 * DELETE /api/portfolios/{portfolioId}       : 포트폴리오 삭제
 *
 * [포트폴리오 조회/검색]
 * GET    /api/portfolios                     : 포트폴리오 목록 조회 (페이징, 정렬)
 * GET    /api/portfolios/search              : 포트폴리오 검색 (키워드)
 * GET    /api/portfolios/photographer/{id}   : 사진작가별 포트폴리오 조회
 *
 * [이미지 관리]
 * POST   /api/portfolios/{portfolioId}/images : 이미지 추가
 * DELETE /api/portfolios/images/{imageId}     : 이미지 삭제
 *
 * [카테고리/기타]
 * POST   /api/portfolios/{portfolioId}/categories/{categoryId} : 카테고리 추가
 * POST   /api/portfolios/{portfolioId}/like                   : 좋아요 증가
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/portfolios")
public class PortfolioController {

	private final PortfolioService portfolioService;

	// 포트폴리오 생성
	@PostMapping
	public ResponseEntity<?> createPortfolio(@Valid @RequestBody PortfolioRequest.CreateDTO createRequest) {
		PortfolioResponse.DetailDTO response = portfolioService.createPortfolio(createRequest);
		URI location = URI.create(String.format("/api/portfolios/%d", response.getPortfolioId()));
		return ResponseEntity.created(location).body(new ApiUtil<>(response));
	}

	// 포트폴리오 상세 조회
	@GetMapping("/{portfolioId}")
	public ResponseEntity<?> getPortfolioDetail(@PathVariable Long portfolioId) {
		PortfolioResponse.DetailDTO response = portfolioService.getPortfolioDetail(portfolioId);
		return ResponseEntity.ok(new ApiUtil<>(response));
	}

	// 포트폴리오 목록 조회 (페이징)
	@GetMapping
	public ResponseEntity<?> getPortfolioList(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "latest") String sortBy) {

		PortfolioRequest.SearchDTO searchRequest = new PortfolioRequest.SearchDTO();
		searchRequest.setPage(page);
		searchRequest.setSize(size);
		searchRequest.setSortBy(sortBy);

		Page<PortfolioResponse.ListDTO> response = portfolioService.getPortfolioList(searchRequest);
		return ResponseEntity.ok(new ApiUtil<>(response));
	}

	// 포트폴리오 수정
	@PutMapping("/{portfolioId}")
	public ResponseEntity<?> updatePortfolio(@PathVariable Long portfolioId,
											 @Valid @RequestBody PortfolioRequest.UpdateDTO updateRequest) {
		PortfolioResponse.DetailDTO response = portfolioService.updatePortfolio(portfolioId, updateRequest);
		return ResponseEntity.ok(new ApiUtil<>(response));
	}

	// 포트폴리오 삭제
	@DeleteMapping("/{portfolioId}")
	public ResponseEntity<?> deletePortfolio(@PathVariable Long portfolioId) {
		portfolioService.deletePortfolio(portfolioId);
		return ResponseEntity.noContent().build();
	}

	// 포트폴리오 검색
	@GetMapping("/search")
	public ResponseEntity<?> searchPortfolios(
			@RequestParam String keyword,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {

		PortfolioRequest.SearchDTO searchRequest = new PortfolioRequest.SearchDTO();
		searchRequest.setKeyword(keyword);
		searchRequest.setPage(page);
		searchRequest.setSize(size);

		Page<PortfolioResponse.ListDTO> response = portfolioService.searchPortfolios(searchRequest);
		return ResponseEntity.ok(new ApiUtil<>(response));
	}

	// 사진작가별 포트폴리오 조회
	@GetMapping("/photographer/{photographerId}")
	public ResponseEntity<?> getPhotographerPortfolios(@PathVariable Long photographerId) {
		List<PortfolioResponse.ListDTO> response = portfolioService.getPhotographerPortfolios(photographerId);
		return ResponseEntity.ok(new ApiUtil<>(response));
	}

	// 포트폴리오에 이미지 추가
	@PostMapping("/{portfolioId}/images")
	public ResponseEntity<?> addImageToPortfolio(@PathVariable Long portfolioId,
												 @Valid @RequestBody PortfolioRequest.AddImageDTO addImageRequest) {
		addImageRequest.setPortfolioId(portfolioId);
		PortfolioResponse.ImageDTO response = portfolioService.addImage(addImageRequest);
		return ResponseEntity.ok(new ApiUtil<>(response));
	}

	// 이미지 삭제
	@DeleteMapping("/images/{imageId}")
	public ResponseEntity<?> deleteImage(@PathVariable Long imageId) {
		portfolioService.deleteImage(imageId);
		return ResponseEntity.noContent().build();
	}

	// 포트폴리오에 카테고리 추가
	@PostMapping("/{portfolioId}/categories/{categoryId}")
	public ResponseEntity<?> addCategoryToPortfolio(@PathVariable Long portfolioId,
													@PathVariable Long categoryId) {
		portfolioService.addCategoryToPortfolio(portfolioId, categoryId);
		return ResponseEntity.ok(new ApiUtil<>("카테고리가 추가되었습니다."));
	}

}