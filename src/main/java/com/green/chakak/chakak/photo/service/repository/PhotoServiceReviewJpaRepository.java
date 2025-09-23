package com.green.chakak.chakak.photo.service.repository;

import com.green.chakak.chakak.account.domain.User;
import com.green.chakak.chakak.booking.domain.BookingInfo;
import com.green.chakak.chakak.photo.domain.PhotoServiceInfo;
import com.green.chakak.chakak.photo.domain.PhotoServiceReview;
import com.green.chakak.chakak.photographer.domain.PhotographerProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PhotoServiceReviewJpaRepository extends JpaRepository<PhotoServiceReview, Long> {

	// 포토서비스별 리뷰 조회 (최신순) - 페이징 처리
	Page<PhotoServiceReview> findByPhotoServiceInfoOrderByCreatedAtDesc(PhotoServiceInfo photoServiceInfo, Pageable pageable);

	// 기존 메서드도 유지 (페이징 없는 버전)
	List<PhotoServiceReview> findByPhotoServiceInfoOrderByCreatedAtDesc(PhotoServiceInfo photoServiceInfo);

	// 사용자별 작성 리뷰 조회 - 페이징 처리
	Page<PhotoServiceReview> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

	// 기존 메서드도 유지
	List<PhotoServiceReview> findByUserOrderByCreatedAtDesc(User user);

	// 예약별 리뷰 조회 (1:1 관계)
	Optional<PhotoServiceReview> findByBookingInfo(BookingInfo bookingInfo);

	// 포토그래퍼 프로필 목록으로 모든 리뷰 조회 (N+1 문제 해결용)
	List<PhotoServiceReview> findByPhotoServiceInfo_PhotographerProfileIn(List<PhotographerProfile> photographerProfiles);

	// 포토서비스의 특정 평점 이상 리뷰 조회 - 페이징 처리
	Page<PhotoServiceReview> findByPhotoServiceInfoAndRatingGreaterThanEqual(
			PhotoServiceInfo photoServiceInfo, BigDecimal rating, Pageable pageable);

	// 기존 메서드도 유지
	List<PhotoServiceReview> findByPhotoServiceInfoAndRatingGreaterThanEqual(
			PhotoServiceInfo photoServiceInfo, BigDecimal rating);

	// 평점 범위로 리뷰 조회 - 페이징 처리
	Page<PhotoServiceReview> findByRatingBetween(BigDecimal minRating, BigDecimal maxRating, Pageable pageable);
	List<PhotoServiceReview> findByRatingBetween(BigDecimal minRating, BigDecimal maxRating);

	// 리뷰 내용으로 검색 (부분 일치) - 페이징 처리
	Page<PhotoServiceReview> findByReviewContentContaining(String keyword, Pageable pageable);
	List<PhotoServiceReview> findByReviewContentContaining(String keyword);

	// 존재 여부 확인
	boolean existsByUserAndPhotoServiceInfo(User user, PhotoServiceInfo photoServiceInfo);

	// 포토서비스 평균 평점 조회
	@Query("SELECT AVG(r.rating) FROM PhotoServiceReview r WHERE r.photoServiceInfo = :photoServiceInfo")
	Double getAverageRatingByPhotoService(@Param("photoServiceInfo") PhotoServiceInfo photoServiceInfo);

	// 포토서비스 리뷰 개수 조회
	@Query("SELECT COUNT(r) FROM PhotoServiceReview r WHERE r.photoServiceInfo = :photoServiceInfo")
	Long countReviewsByPhotoService(@Param("photoServiceInfo") PhotoServiceInfo photoServiceInfo);

	// 포토서비스별 평점 분포 조회
	@Query("SELECT r.rating, COUNT(r) FROM PhotoServiceReview r WHERE r.photoServiceInfo = :photoServiceInfo GROUP BY r.rating ORDER BY r.rating DESC")
	List<Object[]> getRatingDistributionByPhotoService(@Param("photoServiceInfo") PhotoServiceInfo photoServiceInfo);

	// 최근 N개 리뷰 조회
	List<PhotoServiceReview> findTop10ByPhotoServiceInfoOrderByCreatedAtDesc(PhotoServiceInfo photoServiceInfo);

	// 복합 검색 조건을 위한 추가 메서드들
	@Query("SELECT r FROM PhotoServiceReview r WHERE r.photoServiceInfo = :photoServiceInfo " +
			"AND (:minRating IS NULL OR r.rating >= :minRating) " +
			"AND (:keyword IS NULL OR r.reviewContent LIKE %:keyword%) " +
			"ORDER BY r.createdAt DESC")
	Page<PhotoServiceReview> findReviewsWithFilters(@Param("photoServiceInfo") PhotoServiceInfo photoServiceInfo,
													@Param("minRating") BigDecimal minRating,
													@Param("keyword") String keyword,
													Pageable pageable);
}
