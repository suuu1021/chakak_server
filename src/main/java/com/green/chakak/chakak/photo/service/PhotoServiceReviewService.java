//package com.green.chakak.chakak.photo.service;
//
//import com.green.chakak.chakak.photo.domain.PhotoServiceReview;
//import com.green.chakak.chakak.photo.domain.PhotoServiceInfo;
//import com.green.chakak.chakak.photo.service.repository.PhotoServiceReviewJpaRepository;
//import com.green.chakak.chakak.photo.service.repository.PhotoServiceJpaRepository;
//import com.green.chakak.chakak.photo.service.request.PhotoServiceReviewRequest;
//import com.green.chakak.chakak.photo.service.response.PhotoServiceReviewResponse;
//import com.green.chakak.chakak.account.user.User;
//import com.green.chakak.chakak.account.user.UserJpaRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//@Transactional
//public class PhotoServiceReviewService {
//
//	private final PhotoServiceReviewJpaRepository photoServiceReviewJpaRepository;
//	private final PhotoServiceJpaRepository photoServiceJpaRepository;
//	private final UserJpaRepository userJpaRepository;
//
//	/**
//	 * 리뷰 작성
//	 */
//	public PhotoServiceReviewResponse.SaveDTO createReview(PhotoServiceReviewRequest.SaveReview saveDTO) {
//		// 포토서비스 존재 확인
//		PhotoServiceInfo photoServiceInfo = photoServiceJpaRepository.findById(saveDTO.getServiceId())
//				.orElseThrow(() -> new RuntimeException("포토서비스를 찾을 수 없습니다. ID: " + saveDTO.getServiceId()));
//
//		// 사용자 존재 확인
//		User user = userJpaRepository.findById(saveDTO.getUserId())
//				.orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. ID: " + saveDTO.getUserId()));
//
//		// BookingInfo 조회 (임시로 ID만 사용, 나중에 BookingService 주입으로 변경)
//		// BookingInfo bookingInfo = bookingService.getBookingById(saveDTO.getBookingId());
//
//		// 이미 리뷰 작성했는지 확인 (임시로 주석 처리)
//		// if (photoServiceReviewRepository.existsByBookingInfo(bookingInfo)) {
//		//     throw new RuntimeException("이미 리뷰를 작성하셨습니다.");
//		// }
//
//		PhotoServiceReview review = PhotoServiceReview.builder()
//				.photoServiceInfo(photoServiceInfo)
//				.user(user)
//				// .bookingInfo(bookingInfo)  // BookingInfo 완성 후 추가
//				.rating(saveDTO.getRating())
//				.reviewContent(saveDTO.getReviewContent())
//				.isAnonymous(saveDTO.getIsAnonymous())
//				.build();
//
//		PhotoServiceReview saved = photoServiceReviewJpaRepository.save(review);
//		return new PhotoServiceReviewResponse.SaveDTO(saved);
//	}
//
//	/**
//	 * 리뷰 상세 조회
//	 */
//	@Transactional(readOnly = true)
//	public PhotoServiceReviewResponse.DetailDTO getReviewDetail(Long reviewId) {
//		PhotoServiceReview review = photoServiceReviewJpaRepository.findById(reviewId)
//				.orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다."));
//
//		return new PhotoServiceReviewResponse.DetailDTO(review);
//	}
//
//	/**
//	 * 포토서비스별 리뷰 목록 조회
//	 */
//	@Transactional(readOnly = true)
//	public List<PhotoServiceReviewResponse.ListDTO> getReviewsByPhotoService(Long serviceId) {
//		PhotoServiceInfo photoServiceInfo = photoServiceJpaRepository.findById(serviceId)
//				.orElseThrow(() -> new RuntimeException("포토서비스를 찾을 수 없습니다."));
//
//		List<PhotoServiceReview> reviews = photoServiceReviewJpaRepository.findByPhotoServiceInfoOrderByCreatedAtDesc(photoServiceInfo);
//
//		return reviews.stream()
//				.map(PhotoServiceReviewResponse.ListDTO::new)
//				.collect(Collectors.toList());
//	}
//
//	/**
//	 * 사용자별 작성 리뷰 조회
//	 */
//	@Transactional(readOnly = true)
//	public List<PhotoServiceReviewResponse.ListDTO> getReviewsByUser(Long userId) {
//		User user = userJpaRepository.findById(userId)
//				.orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
//
//		List<PhotoServiceReview> reviews = photoServiceReviewJpaRepository.findByUserOrderByCreatedAtDesc(user);
//
//		return reviews.stream()
//				.map(PhotoServiceReviewResponse.ListDTO::new)
//				.collect(Collectors.toList());
//	}
//
//	/**
//	 * 특정 평점 이상 리뷰 조회
//	 */
//	@Transactional(readOnly = true)
//	public List<PhotoServiceReviewResponse.ListDTO> getHighRatingReviews(Long serviceId, BigDecimal minRating) {
//		PhotoServiceInfo photoServiceInfo = photoServiceJpaRepository.findById(serviceId)
//				.orElseThrow(() -> new RuntimeException("포토서비스를 찾을 수 없습니다."));
//
//		List<PhotoServiceReview> reviews = photoServiceReviewJpaRepository.findByPhotoServiceInfoAndRatingGreaterThanEqual(photoServiceInfo, minRating);
//
//		return reviews.stream()
//				.map(PhotoServiceReviewResponse.ListDTO::new)
//				.collect(Collectors.toList());
//	}
//
//	/**
//	 * 리뷰 수정
//	 */
//	public PhotoServiceReviewResponse.UpdateDTO updateReview(Long reviewId, PhotoServiceReviewRequest.UpdateReview updateDTO) {
//		PhotoServiceReview review = photoServiceReviewJpaRepository.findById(reviewId)
//				.orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다."));
//
//		review.setRating(updateDTO.getRating());
//		review.setReviewContent(updateDTO.getReviewContent());
//		review.setIsAnonymous(updateDTO.getIsAnonymous());
//
//		return new PhotoServiceReviewResponse.UpdateDTO(review);
//	}
//
//	/**
//	 * 리뷰 삭제
//	 */
//	public void deleteReview(Long reviewId) {
//		PhotoServiceReview review = photoServiceReviewJpaRepository.findById(reviewId)
//				.orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다."));
//
//		photoServiceReviewJpaRepository.delete(review);
//	}
//
//	/**
//	 * 포토서비스 평균 평점 및 리뷰 개수 조회
//	 */
//	@Transactional(readOnly = true)
//	public PhotoServiceReviewResponse.StatDTO getReviewStats(Long serviceId) {
//		PhotoServiceInfo photoServiceInfo = photoServiceJpaRepository.findById(serviceId)
//				.orElseThrow(() -> new RuntimeException("포토서비스를 찾을 수 없습니다."));
//
//		Double averageRating = photoServiceReviewJpaRepository.getAverageRatingByPhotoService(photoServiceInfo);
//		Long totalReviews = photoServiceReviewJpaRepository.countReviewsByPhotoService(photoServiceInfo);
//
//		return new PhotoServiceReviewResponse.StatDTO(averageRating, totalReviews);
//	}
//
//	/**
//	 * 포토서비스 평점 분포 조회
//	 */
//	@Transactional(readOnly = true)
//	public List<Object[]> getRatingDistribution(Long serviceId) {
//		PhotoServiceInfo photoServiceInfo = photoServiceJpaRepository.findById(serviceId)
//				.orElseThrow(() -> new RuntimeException("포토서비스를 찾을 수 없습니다."));
//
//		return photoServiceReviewJpaRepository.getRatingDistributionByPhotoService(photoServiceInfo);
//	}
//
//	/**
//	 * 포토서비스 최근 리뷰 10개 조회
//	 */
//	@Transactional(readOnly = true)
//	public List<PhotoServiceReviewResponse.ListDTO> getRecentReviews(Long serviceId) {
//		PhotoServiceInfo photoServiceInfo = photoServiceJpaRepository.findById(serviceId)
//				.orElseThrow(() -> new RuntimeException("포토서비스를 찾을 수 없습니다."));
//
//		List<PhotoServiceReview> reviews = photoServiceReviewJpaRepository.findTop10ByPhotoServiceInfoOrderByCreatedAtDesc(photoServiceInfo);
//
//		return reviews.stream()
//				.map(PhotoServiceReviewResponse.ListDTO::new)
//				.collect(Collectors.toList());
//	}
//}