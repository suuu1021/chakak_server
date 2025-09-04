package com.green.chakak.chakak.photographer.service.repository;

import com.green.chakak.chakak.photographer.domain.PhotographerMap;
import com.green.chakak.chakak.photographer.domain.PhotographerProfile;
import com.green.chakak.chakak.photographer.domain.PhotographerCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PhotographerMapRepository extends JpaRepository<PhotographerMap, Long> {

    // 포토그래퍼별 카테고리 조회
    List<PhotographerMap> findByPhotographerProfile(PhotographerProfile photographerProfile);

    // 카테고리별 포토그래퍼 조회
    List<PhotographerMap> findByPhotographerCategory(PhotographerCategory photographerCategory);

    // 특정 포토그래퍼-카테고리 매핑 조회
    Optional<PhotographerMap> findByPhotographerProfileAndPhotographerCategory(
            PhotographerProfile photographerProfile, PhotographerCategory photographerCategory);

    // 중복 매핑 방지용
    boolean existsByPhotographerProfileAndPhotographerCategory(
            PhotographerProfile photographerProfile, PhotographerCategory photographerCategory);

    @Query("SELECT pm FROM PhotographerMap pm JOIN FETCH pm.photographerProfile WHERE pm.photographerCategory = :category")
    List<PhotographerMap> findByPhotographerCategoryWithPhotographer(@Param("category") PhotographerCategory category);

    @Query("SELECT pm FROM PhotographerMap pm JOIN FETCH pm.photographerCategory WHERE pm.photographerProfile = :photographerProfile")
    List<PhotographerMap> findByPhotographerProfileWithCategory(@Param("photographerProfile") PhotographerProfile photographerProfile);
}