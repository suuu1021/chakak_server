package com.green.chakak.chakak.photographer.service.repository;

import com.green.chakak.chakak.photographer.domain.PhotographerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PhotographerRepository extends JpaRepository<PhotographerProfile, Long> {

	// 사용자 ID로 포토그래퍼 조회 (1:1 관계)
	Optional<PhotographerProfile> findByUser_UserId(Long userId);

	// 활성 상태 포토그래퍼만 조회
	List<PhotographerProfile> findByStatus(String status);

	// 지역별 활성 포토그래퍼 조회
	List<PhotographerProfile> findByLocationAndStatus(String location, String status);

	// 경력 연수 범위로 조회
	List<PhotographerProfile> findByExperienceYearsBetween(Integer minYears, Integer maxYears);

	// 상호명으로 검색 (부분 일치)
	List<PhotographerProfile> findByBusinessNameContaining(String businessName);

	// 존재 여부 확인
	boolean existsByUser_UserId(Long userId);
	boolean existsByBusinessName(String businessName);

	// 활성 포토그래퍼 수 조회
	@Query("SELECT COUNT(p) FROM PhotographerProfile p WHERE p.status = :status")
	Long countByStatus(@Param("status") String status);

	// 지역별 포토그래퍼 수
	@Query("SELECT p.location, COUNT(p) FROM PhotographerProfile p WHERE p.status = 'ACTIVE' GROUP BY p.location")
	List<Object[]> countActivePhotographersByLocation();
}
