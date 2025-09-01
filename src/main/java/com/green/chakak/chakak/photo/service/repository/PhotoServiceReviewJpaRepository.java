package com.green.chakak.chakak.photo.service.repository;

import com.green.chakak.chakak.photo.domain.PhotoServiceReview;
import com.green.chakak.chakak.photo.domain.PhotoServiceInfo;
import com.green.chakak.chakak.account.user.User;
import com.green.chakak.chakak.booking.domain.BookingInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PhotoServiceReviewJpaRepository extends JpaRepository<PhotoServiceReview, Long> {

	// 포토서비스별 리뷰 조회 (최신순)
	List<PhotoServiceReview> findByPhotoServiceInfoOrderByCreatedAtDesc(PhotoServiceInfo photoServiceInfo);

	// 사용자별 작성 리뷰 조회
	List<PhotoServiceReview> findByUserOrderByCreatedAtDesc(User user);

	// 예약별 리뷰 조회 (1:1 관계)
	Optional<PhotoServiceReview> findByBookingInfo(BookingInfo bookingInfo);

	// 포토서비스의 특정 평점 이상 리뷰 조회
	List<PhotoServiceReview> findByPhotoServiceInfoAndRatingGreaterThanEqual(
			PhotoServiceInfo photoServiceInfo, BigDecimal rating);

	// 익명/실명 리뷰 구분 조회
	List<PhotoServiceReview> findByPhotoServiceInfoAndIsAnonymous(
			PhotoServiceInfo photoServiceInfo, Boolean isAnonymous);

	// 평점 범위로 리뷰 조회
	List<PhotoServiceReview> findByRatingBetween(BigDecimal minRating, BigDecimal maxRating);

	// 리뷰 내용으로 검색 (부분 일치)
	List<PhotoServiceReview> findByReviewContentContaining(String keyword);

	// 존재 여부 확인
	//boolean existsByBookingInfo(BookingInfo bookingInfo);
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
}