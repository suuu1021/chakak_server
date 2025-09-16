package com.green.chakak.chakak.photo.controller;

import com.green.chakak.chakak._global.errors.exception.Exception403;
import com.green.chakak.chakak._global.utils.ApiUtil;
import com.green.chakak.chakak._global.utils.Define;
import com.green.chakak.chakak.account.domain.LoginUser;
import com.green.chakak.chakak.admin.domain.LoginAdmin;
import com.green.chakak.chakak.photo.service.PhotoService;
import com.green.chakak.chakak.photo.service.request.PhotoCategoryRequest;
import com.green.chakak.chakak.photo.service.response.PhotoCategoryResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/photo/categories")
public class PhotoCategoryRestController {
    private final PhotoService photoCategoryService;

    // 카테고리 목록 조회
    @GetMapping("/list")
    public ResponseEntity<?> list() {
        List<PhotoCategoryResponse.PhotoCategoryListDTO> categoryList = photoCategoryService.categoryList();
        return ResponseEntity.ok(new ApiUtil<>(categoryList));
    }

    // 카테고리 생성
    @PostMapping("")
    public ResponseEntity<?> create(
            @Valid @RequestBody PhotoCategoryRequest.SaveDTO saveDTO,
            @RequestAttribute(Define.LOGIN_USER) LoginUser loginUser,
            @RequestAttribute(Define.LOGIN_ADMIN) LoginAdmin loginAdmin) {
        if (loginAdmin == null) {
            throw new Exception403("관리자 권한이 필요합니다.");
        }
        photoCategoryService.saveCategory(saveDTO, loginUser);
        return ResponseEntity.ok(new ApiUtil<>("카테고리 생성이 완료 되었습니다"));
    }

    // 카테고리 수정
    @PatchMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @Valid @RequestBody PhotoCategoryRequest.UpdateDTO updateDTO,
            @RequestAttribute(Define.LOGIN_USER) LoginUser loginUser,
            @RequestAttribute(Define.LOGIN_ADMIN) LoginAdmin loginAdmin) {

        if (loginAdmin == null) {
            throw new Exception403("관리자 권한이 필요합니다.");
        }
        photoCategoryService.updateCategory(id, updateDTO, loginUser);
        return ResponseEntity.ok(new ApiUtil<>("카테고리 수정이 완료 되었습니다"));
    }

    // 카테고리 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Long id,
             @RequestAttribute(Define.LOGIN_USER) LoginUser loginUser,
            @RequestAttribute(Define.LOGIN_ADMIN) LoginAdmin loginAdmin) {

        if (loginAdmin == null) {
            throw new Exception403("관리자 권한이 필요합니다.");
        }
        photoCategoryService.deleteCategory(id, loginUser);
        return ResponseEntity.ok(new ApiUtil<>("카테고리 삭제가 완료 되었습니다"));
    }
}
