package com.green.chakak.chakak.photo.service.repository;

import com.green.chakak.chakak.photo.domain.PhotoServiceInfo;
import com.green.chakak.chakak.photographer.domain.PhotographerProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PhotoServiceJpaRepository extends JpaRepository<PhotoServiceInfo, Long> {

    List<PhotoServiceInfo> findByPhotographerProfile(PhotographerProfile photographerProfile);

	@Query("SELECT ps FROM PhotoServiceInfo ps JOIN FETCH ps.photographerProfile ORDER BY ps.serviceId DESC")
	Page<PhotoServiceInfo> findAllServiceInfo(Pageable pageable);

	@Query("SELECT p FROM PhotoServiceInfo p " +
			"JOIN FETCH p.photographerProfile pp " +
			"JOIN FETCH pp.user u " +
			"JOIN FETCH u.userType " +  // UserType까지 fetch
			"WHERE p.serviceId = :id")
	Optional<PhotoServiceInfo> findByIdWithUser(@Param("id") Long id);

	@Query("SELECT ps FROM PhotoServiceInfo ps WHERE ps.title like %:keyword% ORDER BY ps.serviceId DESC")
	Page<PhotoServiceInfo> findAllServiceInfoByKeyword(Pageable pageable, String keyword);

	// 카테고리별 포토서비스 조회 (PriceInfo 포함)
	@Query("SELECT DISTINCT psi FROM PhotoServiceInfo psi " +
			"LEFT JOIN FETCH psi.priceInfos " +
			"LEFT JOIN FETCH psi.photographerProfile pp " +
			"LEFT JOIN FETCH pp.user u " +
			"JOIN PhotoServiceMapping psm ON psi.serviceId = psm.photoServiceInfo.serviceId " +
			"WHERE psm.photoServiceCategory.categoryId = :categoryId " +
			"ORDER BY psi.serviceId DESC")
	List<PhotoServiceInfo> findByCategoryIdWithPriceInfo(@Param("categoryId") Long categoryId);

    // PhotoServiceJpaRepository에 추가
    List<PhotoServiceInfo> findByPhotographerProfile_PhotographerProfileId(Long photographerId);
}
