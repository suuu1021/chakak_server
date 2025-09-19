package com.green.chakak.chakak.portfolios.controller;

import com.green.chakak.chakak._global.errors.exception.Exception403;
import com.green.chakak.chakak._global.utils.ApiUtil;
import com.green.chakak.chakak._global.utils.Define;
import com.green.chakak.chakak.admin.domain.LoginAdmin;
import com.green.chakak.chakak.portfolios.service.PortfolioCategoryService;
import com.green.chakak.chakak.portfolios.service.request.PortfolioCategoryRequest;
import com.green.chakak.chakak.portfolios.service.response.PortfolioCategoryResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * 포트폴리오 카테고리 컨트롤러
 *
 * [카테고리 CRUD]
 * POST   /api/portfolio-categories           : 카테고리 생성
 * GET    /api/portfolio-categories/{id}      : 카테고리 상세 조회
 * PUT    /api/portfolio-categories/{id}      : 카테고리 수정
 * DELETE /api/portfolio-categories/{id}      : 카테고리 삭제 (비활성화)
 *
 * [카테고리 조회]
 * GET    /api/portfolio-categories           : 활성 카테고리 목록
 * GET    /api/portfolio-categories/all       : 전체 카테고리 목록 (관리자용)
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/portfolio-categories")
public class PortfolioCategoryController {

	private final PortfolioCategoryService portfolioCategoryService;

	// 카테고리 생성
	@PostMapping
	public ResponseEntity<?> createCategory(@Valid @RequestBody PortfolioCategoryRequest.CreateDTO categoryRequest,
                                            @RequestAttribute(value = Define.LOGIN_ADMIN, required = false) LoginAdmin loginAdmin) {
        if (loginAdmin == null) {
            loginAdmin = LoginAdmin.builder()
                    .adminId(0L)
                    .adminName("testAdmin")
                    .userTypeName("admin")
                    .build();
        }
        PortfolioCategoryResponse.DetailDTO response = portfolioCategoryService.createCategory(categoryRequest, loginAdmin);
		URI location = URI.create(String.format("/api/portfolio-categories/%d", response.getCategoryId()));
		return ResponseEntity.created(location).body(new ApiUtil<>(response));
	}

	// 카테고리 상세 조회
	@GetMapping("/{categoryId}")
	public ResponseEntity<?> getCategory(@PathVariable Long categoryId) {
		PortfolioCategoryResponse.DetailDTO response = portfolioCategoryService.getCategory(categoryId);
		return ResponseEntity.ok(new ApiUtil<>(response));
	}

	// 활성 카테고리 목록 조회
	@GetMapping
	public ResponseEntity<?> getActiveCategories() {
		List<PortfolioCategoryResponse.DetailDTO> response = portfolioCategoryService.getActiveCategories();
		return ResponseEntity.ok(new ApiUtil<>(response));
	}

	// 전체 카테고리 목록 조회 (관리자용)
	@GetMapping("/all")
	public ResponseEntity<?> getAllCategories(@RequestAttribute(value = Define.LOGIN_ADMIN, required = false) LoginAdmin loginAdmin) {
        if (loginAdmin == null) {
            throw new Exception403("관리자 권한이 필요합니다.");
        }
		List<PortfolioCategoryResponse.DetailDTO> response = portfolioCategoryService.getAllCategories(loginAdmin);
		return ResponseEntity.ok(new ApiUtil<>(response));
	}

	// 카테고리 수정
	@PutMapping("/{categoryId}")
	public ResponseEntity<?> updateCategory(@PathVariable Long categoryId,
											@Valid @RequestBody PortfolioCategoryRequest.UpdateDTO categoryRequest,
                                            @RequestAttribute(value = Define.LOGIN_ADMIN, required = false) LoginAdmin loginAdmin) {
        if (loginAdmin == null) {
            loginAdmin = LoginAdmin.builder()
                    .adminId(0L)
                    .adminName("testAdmin")
                    .userTypeName("admin")
                    .build();
        }
		PortfolioCategoryResponse.DetailDTO response = portfolioCategoryService.updateCategory(categoryId, categoryRequest, loginAdmin);
		return ResponseEntity.ok(new ApiUtil<>(response));
	}

	// 카테고리 삭제 (비활성화)
	@DeleteMapping("/{categoryId}")
	public ResponseEntity<?> deleteCategory(@PathVariable Long categoryId,
                                            @RequestAttribute(value = Define.LOGIN_ADMIN, required = false) LoginAdmin loginAdmin) {
        if (loginAdmin == null) {
            loginAdmin = LoginAdmin.builder()
                    .adminId(0L)
                    .adminName("testAdmin")
                    .userTypeName("admin")
                    .build();
        }
		portfolioCategoryService.deleteCategory(categoryId, loginAdmin);
		return ResponseEntity.ok(new ApiUtil<>("카테고리 비활성화가 완료 되었습니다."));
	}
}