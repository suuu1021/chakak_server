package com.green.chakak.chakak.photographer.service;

import com.green.chakak.chakak.photographer.domain.PhotographerCategory;
import com.green.chakak.chakak.photographer.domain.PhotographerMap;
import com.green.chakak.chakak.photographer.service.repository.PhotographerCategoryRepository;
import com.green.chakak.chakak.photographer.service.repository.PhotographerMapRepository;
import com.green.chakak.chakak.photographer.service.request.PhotographerCategoryRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PhotographerCategoryService {

    private final PhotographerCategoryRepository photographerCategoryRepository;
    private final PhotographerMapRepository photographerMapRepository;

    /**
     * 카테고리 생성
     */
    public PhotographerCategory createCategory(PhotographerCategoryRequest.SaveCategory saveCategory) {
        // 중복 카테고리명 확인
        if (photographerCategoryRepository.existsByCategoryName(saveCategory.getCategoryName())) {
            throw new RuntimeException("이미 존재하는 카테고리명입니다.");
        }

        PhotographerCategory category = saveCategory.toEntity();
        return photographerCategoryRepository.save(category);
    }

    /**
     * 모든 카테고리 조회
     */
    @Transactional(readOnly = true)
    public List<PhotographerCategory> getAllCategories() {
        return photographerCategoryRepository.findAll();
    }

    /**
     * 카테고리 ID로 조회
     */
    @Transactional(readOnly = true)
    public PhotographerCategory getCategoryById(Long categoryId) {
        return photographerCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("카테고리를 찾을 수 없습니다."));
    }

    /**
     * 카테고리명으로 조회
     */
    @Transactional(readOnly = true)
    public PhotographerCategory getCategoryByName(String categoryName) {
        return photographerCategoryRepository.findByCategoryName(categoryName)
                .orElseThrow(() -> new RuntimeException("카테고리를 찾을 수 없습니다."));
    }

    /**
     * 카테고리 수정
     */
    public PhotographerCategory updateCategory(Long categoryId, PhotographerCategoryRequest.UpdateCategory updateCategory) {
        PhotographerCategory category = getCategoryById(categoryId);

        // 카테고리명 중복 확인 (본인 제외)
        if (!category.getCategoryName().equals(updateCategory.getCategoryName()) &&
                photographerCategoryRepository.existsByCategoryName(updateCategory.getCategoryName())) {
            throw new RuntimeException("이미 존재하는 카테고리명입니다.");
        }

        // 카테고리 업데이트 (update 메서드가 있다고 가정)
        category.update(updateCategory.getCategoryName());
        return category;
    }

    /**
     * 카테고리 삭제
     */
    public void deleteCategory(Long categoryId) {
        PhotographerCategory category = getCategoryById(categoryId);

        // 해당 카테고리를 사용하는 포토그래퍼가 있는지 확인
        List<PhotographerMap> mappings = photographerMapRepository.findByPhotographerCategory(category);
        if (!mappings.isEmpty()) {
            throw new RuntimeException("사용 중인 카테고리는 삭제할 수 없습니다. 먼저 포토그래퍼들의 카테고리 매핑을 해제해주세요.");
        }

        photographerCategoryRepository.deleteById(categoryId);
    }

    /**
     * 특정 카테고리에 속한 포토그래퍼들 조회
     */
    @Transactional(readOnly = true)
    public List<PhotographerMap> getPhotographersByCategory(Long categoryId) {
        PhotographerCategory category = getCategoryById(categoryId);
        return photographerMapRepository.findByPhotographerCategory(category);
    }

    /**
     * 카테고리명 중복 확인
     */
    @Transactional(readOnly = true)
    public boolean existsByCategoryName(String categoryName) {
        return photographerCategoryRepository.existsByCategoryName(categoryName);
    }
}
