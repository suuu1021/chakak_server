package com.green.chakak.chakak.photo.service;

import com.green.chakak.chakak._global.errors.exception.Exception400;
import com.green.chakak.chakak._global.errors.exception.Exception401;
import com.green.chakak.chakak._global.errors.exception.Exception404;
import com.green.chakak.chakak.booking.domain.BookingStatus;
import com.green.chakak.chakak.booking.service.repository.BookingInfoJpaRepository;
import com.green.chakak.chakak.photo.domain.PhotoServiceReview;
import com.green.chakak.chakak.photo.domain.PhotoServiceInfo;
import com.green.chakak.chakak.booking.domain.BookingInfo;
import com.green.chakak.chakak.photo.service.repository.PhotoServiceReviewJpaRepository;
import com.green.chakak.chakak.photo.service.repository.PhotoServiceJpaRepository;
import com.green.chakak.chakak.photo.service.request.PhotoServiceReviewRequest;
import com.green.chakak.chakak.photo.service.response.PhotoServiceReviewResponse;
import com.green.chakak.chakak.account.domain.User;
import com.green.chakak.chakak.account.service.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PhotoServiceReviewService {

    private final PhotoServiceReviewJpaRepository photoServiceReviewJpaRepository;
    private final PhotoServiceJpaRepository photoServiceJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final BookingInfoJpaRepository bookingInfoJpaRepository; // 추가

    /**
     * 리뷰 작성
     */
    public PhotoServiceReviewResponse.SaveDTO createReview(PhotoServiceReviewRequest.SaveReview saveDTO) {
        PhotoServiceInfo photoServiceInfo = photoServiceJpaRepository.findById(saveDTO.getServiceId())
                .orElseThrow(() -> new Exception404("해당 서비스 ID가 존재하지 않습니다. 요청Id : "+saveDTO.getServiceId()));

        User user = userJpaRepository.findById(saveDTO.getUserId())
                .orElseThrow(() -> new Exception404("해당 유저 ID가 존재하지 않습니다. 요청Id : "+saveDTO.getUserId()));

        BookingInfo bookingInfo = bookingInfoJpaRepository.findById(saveDTO.getBookingId())
                .orElseThrow(() -> new Exception404("해당 예약정보 ID가 존재하지 않습니다. 요청Id : "+saveDTO.getBookingId()));

        if (!bookingInfo.getUserProfile().getUser().getUserId().equals(saveDTO.getUserId())) {
            throw new Exception401("해당 서비스를 이용한 사용자만 리뷰 작성이 가능합니다.");
        }
        if (!bookingInfo.getPhotoServiceInfo().getServiceId().equals(saveDTO.getServiceId())) {
            throw new Exception400("예약한 서비스와 리뷰 작성할 서비스가 일치하지 않습니다.");
        }
        if (photoServiceInfo.getPhotographerProfile().getUser().getUserId().equals(saveDTO.getUserId())) {
            throw new Exception400("자신의 서비스에 리뷰 작성을 할 수 없습니다.");
        }
        if (photoServiceReviewJpaRepository.findByBookingInfo(bookingInfo).isPresent()) {
            throw new Exception400("이미 리뷰를 작성한 서비스입니다.");
        }
        if (bookingInfo.getStatus() != BookingStatus.COMPLETED) {
            throw new Exception400("완료된 예약에 대해서만 리뷰를 작성할 수 있습니다.");
        }

        PhotoServiceReview review = PhotoServiceReview.builder()
                .photoServiceInfo(photoServiceInfo)
                .user(user)
                .bookingInfo(bookingInfo)
                .rating(saveDTO.getRating())
                .reviewContent(saveDTO.getReviewContent())
                .build();

        PhotoServiceReview saved = photoServiceReviewJpaRepository.save(review);
        return new PhotoServiceReviewResponse.SaveDTO(saved);
    }

    /**
     * 리뷰 상세 조회
     */
    @Transactional(readOnly = true)
    public PhotoServiceReviewResponse.DetailDTO getReviewDetail(Long reviewId) {
        PhotoServiceReview review = photoServiceReviewJpaRepository.findById(reviewId)
                .orElseThrow(() -> new Exception404("해당 리뷰 ID가 존재하지 않습니다. 요청Id : "+reviewId));
        return new PhotoServiceReviewResponse.DetailDTO(review);
    }

    /**
     * 포토서비스별 리뷰 목록 조회 (페이징)
     */
    @Transactional(readOnly = true)
    public Page<PhotoServiceReviewResponse.ListDTO> getReviewsByPhotoService(Long serviceId, Pageable pageable) {
        PhotoServiceInfo photoServiceInfo = photoServiceJpaRepository.findById(serviceId)
                .orElseThrow(() -> new Exception404("해당 예약정보 ID가 존재하지 않습니다. 요청Id : "+serviceId));

        Page<PhotoServiceReview> reviews = photoServiceReviewJpaRepository
                .findByPhotoServiceInfoOrderByCreatedAtDesc(photoServiceInfo, pageable);

        return reviews.map(PhotoServiceReviewResponse.ListDTO::new);
    }

    /**
     * 포토그래퍼 전체 리뷰 조회 (페이징)
     */
    @Transactional(readOnly = true)
    public Page<PhotoServiceReviewResponse.ListDTO> getReviewsByPhotographer(Long photographerId, Pageable pageable) {
        Page<PhotoServiceReview> reviews = photoServiceReviewJpaRepository.findByPhotographerId(photographerId, pageable);
        return reviews.map(PhotoServiceReviewResponse.ListDTO::new);
    }

    /**
     * 사용자별 작성 리뷰 조회 (페이징)
     */
    @Transactional(readOnly = true)
    public Page<PhotoServiceReviewResponse.ListDTO> getReviewsByUser(Long userId, Pageable pageable) {
        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new Exception404("해당 유저 ID가 존재하지 않습니다. 요청Id : "+userId));

        Page<PhotoServiceReview> reviews = photoServiceReviewJpaRepository
                .findByUserOrderByCreatedAtDesc(user, pageable);

        return reviews.map(PhotoServiceReviewResponse.ListDTO::new);
    }

    /**
     * 특정 평점 이상 리뷰 조회 (페이징)
     */
    @Transactional(readOnly = true)
    public Page<PhotoServiceReviewResponse.ListDTO> getHighRatingReviews(Long serviceId, BigDecimal minRating, Pageable pageable) {
        PhotoServiceInfo photoServiceInfo = photoServiceJpaRepository.findById(serviceId)
                .orElseThrow(() -> new Exception404("해당 서비스 ID가 존재하지 않습니다. 요청Id : "+serviceId) );

        Page<PhotoServiceReview> reviews = photoServiceReviewJpaRepository
                .findByPhotoServiceInfoAndRatingGreaterThanEqual(photoServiceInfo, minRating, pageable);

        return reviews.map(PhotoServiceReviewResponse.ListDTO::new);
    }

    /**
     * 리뷰 수정
     */
    public PhotoServiceReviewResponse.UpdateDTO updateReview(Long reviewId, Long userId, PhotoServiceReviewRequest.UpdateReview updateDTO) {
        PhotoServiceReview review = photoServiceReviewJpaRepository.findById(reviewId)
                .orElseThrow(() -> new Exception404("해당 리뷰 ID가 존재하지 않습니다. 요청Id : "+reviewId));

        if (!review.getUser().getUserId().equals(userId)) {
            throw new Exception401("해당 리뷰를 작성한 사용자만 수정이 가능합니다.");
        }

        review.updateReview(updateDTO.getRating(), updateDTO.getReviewContent());
        return new PhotoServiceReviewResponse.UpdateDTO(review);
    }

    /**
     * 리뷰 삭제
     */
    public void deleteReview(Long reviewId, Long userId) {
        PhotoServiceReview review = photoServiceReviewJpaRepository.findById(reviewId)
                .orElseThrow(() -> new Exception404("해당 리뷰 ID가 존재하지 않습니다. 요청Id : "+reviewId));

        if (!review.getUser().getUserId().equals(userId)) {
            throw new Exception401("해당 리뷰를 작성한 이용자만 삭제 가능합니다.");
        }

        photoServiceReviewJpaRepository.delete(review);
    }

    /**
     * 포토서비스 평균 평점 및 리뷰 개수 조회
     */
    @Transactional(readOnly = true)
    public PhotoServiceReviewResponse.StatDTO getReviewStats(Long serviceId) {
        PhotoServiceInfo photoServiceInfo = photoServiceJpaRepository.findById(serviceId)
                .orElseThrow(() -> new Exception404("해당 서비스 ID가 존재하지 않습니다. 요청Id : "+serviceId));

        Double averageRating = photoServiceReviewJpaRepository.getAverageRatingByPhotoService(photoServiceInfo);
        Long totalReviews = photoServiceReviewJpaRepository.countReviewsByPhotoService(photoServiceInfo);

        return new PhotoServiceReviewResponse.StatDTO(averageRating, totalReviews);
    }

    /**
     * 포토서비스 평점 분포 조회
     */
    @Transactional(readOnly = true)
    public List<PhotoServiceReviewResponse.RatingDistributionDTO> getRatingDistribution(Long serviceId) {
        PhotoServiceInfo photoServiceInfo = photoServiceJpaRepository.findById(serviceId)
                .orElseThrow(() -> new Exception404("해당 서비스 ID가 존재하지 않습니다. 요청Id : "+serviceId));

        List<Object[]> rawData = photoServiceReviewJpaRepository.getRatingDistributionByPhotoService(photoServiceInfo);

        return rawData.stream()
                .map(data -> new PhotoServiceReviewResponse.RatingDistributionDTO(
                        (BigDecimal) data[0],
                        (Long) data[1]
                ))
                .collect(Collectors.toList());
    }

    /**
     * 포토서비스 최근 리뷰 10개 조회
     */
    @Transactional(readOnly = true)
    public List<PhotoServiceReviewResponse.ListDTO> getRecentReviews(Long serviceId) {
        PhotoServiceInfo photoServiceInfo = photoServiceJpaRepository.findById(serviceId)
                .orElseThrow(() -> new Exception404("해당 서비스 ID가 존재하지 않습니다. 요청Id : "+serviceId));

        List<PhotoServiceReview> reviews = photoServiceReviewJpaRepository
                .findTop10ByPhotoServiceInfoOrderByCreatedAtDesc(photoServiceInfo);

        return reviews.stream()
                .map(PhotoServiceReviewResponse.ListDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * 사용자가 특정 서비스에 리뷰를 작성할 수 있는지 확인
     */
    @Transactional(readOnly = true)
    public boolean canUserWriteReview(Long userId, Long serviceId, Long bookingId) {
        try {
            PhotoServiceInfo photoServiceInfo = photoServiceJpaRepository.findById(serviceId)
                    .orElseThrow(() -> new Exception404("해당 서비스 ID가 존재하지 않습니다. 요청Id : "+serviceId));

            User user = userJpaRepository.findById(userId)
                    .orElseThrow(() -> new Exception404("해당 유저 ID가 존재하지 않습니다. 요청Id : "+userId));

            BookingInfo bookingInfo = bookingInfoJpaRepository.findById(bookingId)
                    .orElseThrow(() -> new Exception404("해당 예약정보 ID가 존재하지 않습니다. 요청Id : "+bookingId));

            if (!bookingInfo.getUserProfile().getUser().getUserId().equals(userId)) {
                return false;
            }
            if (!bookingInfo.getPhotoServiceInfo().getServiceId().equals(serviceId)) {
                return false;
            }
            if (photoServiceInfo.getPhotographerProfile().getUser().getUserId().equals(userId)) {
                return false;
            }
            if (photoServiceReviewJpaRepository.findByBookingInfo(bookingInfo).isPresent()) {
                return false;
            }

            return bookingInfo.getStatus() == BookingStatus.COMPLETED;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 복합 검색 조건으로 리뷰 조회
     */
    @Transactional(readOnly = true)
    public Page<PhotoServiceReviewResponse.ListDTO> searchReviewsWithFilters(
            Long serviceId, BigDecimal minRating, String keyword, Pageable pageable) {

        PhotoServiceInfo photoServiceInfo = photoServiceJpaRepository.findById(serviceId)
                .orElseThrow(() -> new Exception404("해당 서비스 ID가 존재하지 않습니다. 요청Id : "+serviceId));

        Page<PhotoServiceReview> reviews = photoServiceReviewJpaRepository
                .findReviewsWithFilters(photoServiceInfo, minRating, keyword, pageable);

        return reviews.map(PhotoServiceReviewResponse.ListDTO::new);
    }
}
