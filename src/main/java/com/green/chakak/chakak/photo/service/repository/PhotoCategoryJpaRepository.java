package com.green.chakak.chakak.photo.service.repository;

import com.green.chakak.chakak.photo.domain.PhotoServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PhotoCategoryJpaRepository extends JpaRepository<PhotoServiceCategory, Long> {

    // 카테고리명으로 조회
    Optional<PhotoServiceCategory> findByCategoryName(String categoryName);

    // 카테고리명 중복 체크
    boolean existsByCategoryName(String categoryName);

    // 카테고리명으로 검색 (LIKE 검색)
    List<PhotoServiceCategory> findByCategoryNameContaining(String keyword);
}
