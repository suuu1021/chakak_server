package com.green.chakak.chakak.portfolios.controller;

import com.green.chakak.chakak._global.errors.exception.Exception403;
import com.green.chakak.chakak._global.utils.ApiUtil;
import com.green.chakak.chakak._global.utils.Define;
import com.green.chakak.chakak.account.domain.LoginUser;
import com.green.chakak.chakak.admin.domain.LoginAdmin;
import com.green.chakak.chakak.portfolios.service.PortfolioService;
import com.green.chakak.chakak.portfolios.service.request.PortfolioRequest;
import com.green.chakak.chakak.portfolios.service.response.PortfolioResponse;
import jakarta.servlet.http.HttpServletRequest;
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

	// 포트폴리오 생성 - 로그인 필요
	@PostMapping
	public ResponseEntity<?> createPortfolio(@Valid @RequestBody PortfolioRequest.CreateDTO createRequest,
											 HttpServletRequest httpRequest) {
		LoginUser loginUser = (LoginUser) httpRequest.getAttribute(Define.LOGIN_USER);
		PortfolioResponse.DetailDTO response = portfolioService.createPortfolio(createRequest, loginUser);
		URI location = URI.create(String.format("/api/portfolios/%d", response.getPortfolioId()));
		return ResponseEntity.created(location).body(new ApiUtil<>(response));
	}

	// 포트폴리오 상세 조회 - 인증 불필요
	@GetMapping("/{portfolioId}")
	public ResponseEntity<?> getPortfolioDetail(@PathVariable Long portfolioId) {
                    PortfolioResponse.DetailDTO response = portfolioService.getPortfolioDetail(portfolioId);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

	// 포트폴리오 목록 조회 (페이징) - 인증 불필요
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

	// 포트폴리오 수정 - 로그인 필요
	@PutMapping("/{portfolioId}")
	public ResponseEntity<?> updatePortfolio(@PathVariable Long portfolioId,
											 @Valid @RequestBody PortfolioRequest.UpdateDTO updateRequest,
                                            HttpServletRequest httpRequest) {
        LoginUser loginUser = (LoginUser) httpRequest.getAttribute(Define.LOGIN_USER);
		PortfolioResponse.DetailDTO response = portfolioService.updatePortfolio(portfolioId, updateRequest, loginUser);
		return ResponseEntity.ok(new ApiUtil<>(response));
	}

	// 포트폴리오 삭제 - 로그인 필요
	@DeleteMapping("/{portfolioId}")
	public ResponseEntity<?> deletePortfolio(@PathVariable Long portfolioId,
											 HttpServletRequest httpRequest) {
		LoginUser loginUser = (LoginUser) httpRequest.getAttribute(Define.LOGIN_USER);
		portfolioService.deletePortfolio(portfolioId, loginUser);
		return ResponseEntity.noContent().build();
	}

	// 포트폴리오 검색 - 인증 불필요
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

	// 사진작가별 포트폴리오 조회 - 인증 불필요
	@GetMapping("/photographer/{photographerId}")
	public ResponseEntity<?> getPhotographerPortfolios(@PathVariable Long photographerId) {
		List<PortfolioResponse.ListDTO> response = portfolioService.getPhotographerPortfolios(photographerId);
		return ResponseEntity.ok(new ApiUtil<>(response));
	}

	// 포트폴리오에 이미지 추가 - 로그인 필요 (권한 체크는 서비스에서)
	@PostMapping("/{portfolioId}/images")
	public ResponseEntity<?> addImageToPortfolio(@PathVariable Long portfolioId,
												 @Valid @RequestBody PortfolioRequest.AddImageDTO addImageRequest,
												 HttpServletRequest httpRequest) {
		// 포트폴리오 소유자 확인은 서비스 레이어에서 처리할 수 있도록 LoginUser 전달
		LoginUser loginUser = (LoginUser) httpRequest.getAttribute(Define.LOGIN_USER);
		addImageRequest.setPortfolioId(portfolioId);
		PortfolioResponse.ImageDTO response = portfolioService.addImage(addImageRequest);
		return ResponseEntity.ok(new ApiUtil<>(response));
	}

	// 이미지 삭제 - 로그인 필요 (권한 체크는 서비스에서)
	@DeleteMapping("/images/{imageId}")
	public ResponseEntity<?> deleteImage(@PathVariable Long imageId,
										 HttpServletRequest httpRequest) {
		LoginUser loginUser = (LoginUser) httpRequest.getAttribute(Define.LOGIN_USER);
		portfolioService.deleteImage(imageId);
		return ResponseEntity.noContent().build();
	}

	// 포트폴리오에 카테고리 추가 - 로그인 필요 (권한 체크는 서비스에서)
	@PostMapping("/{portfolioId}/categories/{categoryId}")
	public ResponseEntity<?> addCategoryToPortfolio(@PathVariable Long portfolioId,
													@PathVariable Long categoryId,
													HttpServletRequest httpRequest) {
		LoginUser loginUser = (LoginUser) httpRequest.getAttribute(Define.LOGIN_USER);
		portfolioService.addCategoryToPortfolio(portfolioId, categoryId);
		return ResponseEntity.ok(new ApiUtil<>("카테고리가 추가되었습니다."));
	}

}