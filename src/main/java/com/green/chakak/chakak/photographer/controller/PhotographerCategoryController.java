package com.green.chakak.chakak.photographer.controller;

import com.green.chakak.chakak.global.utils.ApiUtil;
import com.green.chakak.chakak.photographer.domain.PhotographerCategory;
import com.green.chakak.chakak.photographer.domain.PhotographerMap;
import com.green.chakak.chakak.photographer.service.PhotographerCategoryService;
import com.green.chakak.chakak.photographer.service.PhotographerService;
import com.green.chakak.chakak.photographer.service.request.PhotographerCategoryRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/photographer-categories")
public class PhotographerCategoryController {
    private final PhotographerCategoryService photographerCategoryService;
    private final PhotographerService photographerService;

    /**
     * 카테고리 생성
     */
    @PostMapping
    public ResponseEntity<?> createCategory(@Valid @RequestBody PhotographerCategoryRequest.SaveCategory saveCategory) {
        PhotographerCategory response = photographerCategoryService.createCategory(saveCategory);
        URI location = URI.create(String.format("/api/photographer-categories/%d", response.getCategoryId()));
        return ResponseEntity.created(location).body(new ApiUtil<>(response));
    }

    /**
     * 모든 카테고리 조회
     */
    @GetMapping
    public ResponseEntity<?> getAllCategories() {
        List<PhotographerCategory> response = photographerCategoryService.getAllCategories();
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    /**
     * 카테고리 상세 조회
     */
    @GetMapping("/{categoryId}")
    public ResponseEntity<?> getCategoryDetail(@PathVariable Long categoryId) {
        PhotographerCategory response = photographerCategoryService.getCategoryById(categoryId);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    /**
     * 카테고리 수정
     */
    @PutMapping("/{categoryId}")
    public ResponseEntity<?> updateCategory(@PathVariable Long categoryId,
                                            @Valid @RequestBody PhotographerCategoryRequest.UpdateCategory updateCategory) {
        PhotographerCategory response = photographerCategoryService.updateCategory(categoryId, updateCategory);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    /**
     * 카테고리 삭제
     */
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long categoryId) {
        photographerCategoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 포토그래퍼에 카테고리 추가
     */
    @PostMapping("/photographers/{photographerId}/categories/{categoryId}")
    public ResponseEntity<?> addCategoryToPhotographer(@PathVariable Long photographerId,
                                                       @PathVariable Long categoryId) {
        PhotographerMap response = photographerService.addCategoryToPhotographer(photographerId, categoryId);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    /**
     * 포토그래퍼의 카테고리 목록 조회
     */
    @GetMapping("/photographers/{photographerId}")
    public ResponseEntity<?> getPhotographerCategories(@PathVariable Long photographerId) {
        List<PhotographerMap> response = photographerService.getPhotographerCategories(photographerId);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    /**
     * 포토그래퍼에서 카테고리 제거
     */
    @DeleteMapping("/photographers/{photographerId}/categories/{categoryId}")
    public ResponseEntity<?> removeCategoryFromPhotographer(@PathVariable Long photographerId,
                                                            @PathVariable Long categoryId) {
        photographerService.removeCategoryFromPhotographer(photographerId, categoryId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 특정 카테고리에 속한 포토그래퍼들 조회
     */
    @GetMapping("/{categoryId}/photographers")
    public ResponseEntity<?> getPhotographersByCategory(@PathVariable Long categoryId) {
        List<PhotographerMap> response = photographerCategoryService.getPhotographersByCategory(categoryId);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

}

