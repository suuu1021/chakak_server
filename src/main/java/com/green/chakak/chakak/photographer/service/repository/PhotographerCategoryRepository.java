package com.green.chakak.chakak.photographer.service.repository;

import com.green.chakak.chakak.photographer.domain.PhotographerCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PhotographerCategoryRepository extends JpaRepository<PhotographerCategory, Long> {

	// 카테고리명으로 조회
	Optional<PhotographerCategory> findByCategoryName(String categoryName);

	// 카테고리명 존재 여부 확인
	boolean existsByCategoryName(String categoryName);
}