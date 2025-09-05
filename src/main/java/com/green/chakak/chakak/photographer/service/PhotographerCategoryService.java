package com.green.chakak.chakak.photographer.service;

import com.green.chakak.chakak._global.errors.exception.Exception400;
import com.green.chakak.chakak._global.errors.exception.Exception404;
import com.green.chakak.chakak.photographer.domain.PhotographerCategory;
import com.green.chakak.chakak.photographer.domain.PhotographerMap;
import com.green.chakak.chakak.photographer.service.repository.PhotographerCategoryRepository;
import com.green.chakak.chakak.photographer.service.repository.PhotographerMapRepository;
import com.green.chakak.chakak.photographer.service.request.PhotographerCategoryRequest;
import com.green.chakak.chakak.photographer.service.response.PhotographerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PhotographerCategoryService {

    private final PhotographerCategoryRepository photographerCategoryRepository;
    private final PhotographerMapRepository photographerMapRepository;

    /**
     * 카테고리 생성
     */
    public PhotographerResponse.CategoryDTO createCategory(PhotographerCategoryRequest.SaveCategory saveCategory) {
        // 중복 카테고리명 확인
        if (photographerCategoryRepository.existsByCategoryName(saveCategory.getCategoryName())) {
            throw new Exception400("이미 존재하는 카테고리명입니다.");
        }

        PhotographerCategory category = saveCategory.toEntity();
        PhotographerCategory savedCategory = photographerCategoryRepository.save(category);
        return new PhotographerResponse.CategoryDTO(savedCategory);
    }

    /**
     * 모든 카테고리 조회
     */
    @Transactional(readOnly = true)
    public List<PhotographerResponse.CategoryDTO> getAllCategories() {
        return photographerCategoryRepository.findAll().stream()
                .map(PhotographerResponse.CategoryDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * 카테고리 ID로 조회
     */
    @Transactional(readOnly = true)
    public PhotographerResponse.CategoryDTO getCategoryById(Long categoryId) {
        return photographerCategoryRepository.findById(categoryId)
                .map(PhotographerResponse.CategoryDTO::new)
                .orElseThrow(() -> new Exception404("카테고리를 찾을 수 없습니다."));
    }

    /**
     * 카테고리명으로 조회
     */
    @Transactional(readOnly = true)
    public PhotographerResponse.CategoryDTO getCategoryByName(String categoryName) {
        return photographerCategoryRepository.findByCategoryName(categoryName)
                .map(PhotographerResponse.CategoryDTO::new)
                .orElseThrow(() -> new Exception404("카테고리를 찾을 수 없습니다."));
    }

    /**
     * 카테고리 수정
     */
    public PhotographerResponse.CategoryDTO updateCategory(Long categoryId, PhotographerCategoryRequest.UpdateCategory updateCategory) {
        PhotographerCategory category = photographerCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new Exception404("카테고리를 찾을 수 없습니다."));

        // 카테고리명 중복 확인 (본인 제외)
        if (!category.getCategoryName().equals(updateCategory.getCategoryName()) &&
                photographerCategoryRepository.existsByCategoryName(updateCategory.getCategoryName())) {
            throw new Exception400("이미 존재하는 카테고리명입니다.");
        }

        // 카테고리 업데이트
        category.update(updateCategory.getCategoryName());
        return new PhotographerResponse.CategoryDTO(category);
    }

    /**
     * 카테고리 삭제
     */
    public void deleteCategory(Long categoryId) {
        PhotographerCategory category = photographerCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new Exception404("카테고리를 찾을 수 없습니다."));

        // 해당 카테고리를 사용하는 포토그래퍼가 있는지 확인
        List<PhotographerMap> mappings = photographerMapRepository.findByPhotographerCategory(category);
        if (!mappings.isEmpty()) {
            throw new Exception400("사용 중인 카테고리는 삭제할 수 없습니다. 먼저 포토그래퍼들의 카테고리 매핑을 해제해주세요.");
        }

        photographerCategoryRepository.deleteById(categoryId);
    }

    /**
     * 특정 카테고리에 속한 포토그래퍼들 조회
     */
    @Transactional(readOnly = true)
    public List<PhotographerResponse.ListDTO> getPhotographersByCategory(Long categoryId) {
        PhotographerCategory category = photographerCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new Exception404("카테고리를 찾을 수 없습니다."));

        List<PhotographerMap> maps = photographerMapRepository.findByPhotographerCategoryWithPhotographer(category);

        return maps.stream()
                .map(photographerMap -> new PhotographerResponse.ListDTO(photographerMap.getPhotographerProfile()))
                .collect(Collectors.toList());
    }

    /**
     * 카테고리명 중복 확인
     */
    @Transactional(readOnly = true)
    public boolean existsByCategoryName(String categoryName) {
        return photographerCategoryRepository.existsByCategoryName(categoryName);
    }
}
