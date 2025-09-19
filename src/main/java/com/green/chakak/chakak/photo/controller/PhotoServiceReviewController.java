package com.green.chakak.chakak.photo.controller;

import com.green.chakak.chakak.photo.service.PhotoServiceReviewService;
import com.green.chakak.chakak.photo.service.request.PhotoServiceReviewRequest;
import com.green.chakak.chakak.photo.service.response.PhotoServiceReviewResponse;
import com.green.chakak.chakak._global.utils.ApiUtil;
import com.green.chakak.chakak._global.utils.Define;
import com.green.chakak.chakak.account.domain.LoginUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/photo-services")
@RequiredArgsConstructor
public class PhotoServiceReviewController {

    private final PhotoServiceReviewService reviewService;

    /**
     * 리뷰 작성 (인증 필요)
     */
    @PostMapping("/reviews")
    public ResponseEntity<ApiUtil<PhotoServiceReviewResponse.SaveDTO>> createReview(
            @Valid @RequestBody PhotoServiceReviewRequest.SaveReview request,
            @RequestAttribute(Define.LOGIN_USER) LoginUser loginUser) {

        // JWT에서 가져온 userId를 request에 설정
        request.setUserId(loginUser.getId());

        PhotoServiceReviewResponse.SaveDTO response = reviewService.createReview(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiUtil<>(response, "리뷰가 성공적으로 작성되었습니다."));
    }

    /**
     * 리뷰 상세 조회 (인증 불필요)
     */
    @GetMapping("/reviews/{reviewId}/detail")
    public ResponseEntity<ApiUtil<PhotoServiceReviewResponse.DetailDTO>> getReviewDetail(
            @PathVariable Long reviewId) {

        PhotoServiceReviewResponse.DetailDTO response = reviewService.getReviewDetail(reviewId);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    /**
     * 포토서비스별 리뷰 목록 조회 (페이징) - 인증 불필요
     */
    @GetMapping("/{serviceId}/reviews")
    public ResponseEntity<ApiUtil<Page<PhotoServiceReviewResponse.ListDTO>>> getReviewsByPhotoService(
            @PathVariable Long serviceId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PhotoServiceReviewResponse.ListDTO> response =
                reviewService.getReviewsByPhotoService(serviceId, pageable);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    /**
     * 내가 작성한 리뷰 조회 (페이징) - 인증 필요
     */
    @GetMapping("/my-reviews")
    public ResponseEntity<ApiUtil<Page<PhotoServiceReviewResponse.ListDTO>>> getMyReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestAttribute(Define.LOGIN_USER) LoginUser loginUser) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<PhotoServiceReviewResponse.ListDTO> response =
                reviewService.getReviewsByUser(loginUser.getId(), pageable);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    /**
     * 사용자별 작성 리뷰 조회 (페이징) - 인증 불필요 (공개)
     */
    @GetMapping("/users/{userId}/reviews")
    public ResponseEntity<ApiUtil<Page<PhotoServiceReviewResponse.ListDTO>>> getReviewsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<PhotoServiceReviewResponse.ListDTO> response =
                reviewService.getReviewsByUser(userId, pageable);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    /**
     * 특정 평점 이상 리뷰 조회 - 인증 불필요
     */
    @GetMapping("/{serviceId}/reviews/high-rating")
    public ResponseEntity<ApiUtil<Page<PhotoServiceReviewResponse.ListDTO>>> getHighRatingReviews(
            @PathVariable Long serviceId,
            @RequestParam BigDecimal minRating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<PhotoServiceReviewResponse.ListDTO> response =
                reviewService.getHighRatingReviews(serviceId, minRating, pageable);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    /**
     * 리뷰 수정 - 인증 필요
     */
    @PutMapping("/reviews/{reviewId}")
    public ResponseEntity<ApiUtil<PhotoServiceReviewResponse.UpdateDTO>> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody PhotoServiceReviewRequest.UpdateReview request,
            @RequestAttribute(Define.LOGIN_USER) LoginUser loginUser) {

        PhotoServiceReviewResponse.UpdateDTO response =
                reviewService.updateReview(reviewId, loginUser.getId(), request);
        return ResponseEntity.ok(new ApiUtil<>(response, "리뷰가 성공적으로 수정되었습니다."));
    }

    /**
     * 리뷰 삭제 - 인증 필요
     */
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<ApiUtil<String>> deleteReview(
            @PathVariable Long reviewId,
            @RequestAttribute(Define.LOGIN_USER) LoginUser loginUser) {

        reviewService.deleteReview(reviewId, loginUser.getId());
        return ResponseEntity.ok(new ApiUtil<>("리뷰가 성공적으로 삭제되었습니다.", "성공"));
    }

    /**
     * 포토서비스 평균 평점 및 리뷰 개수 조회 - 인증 불필요
     */
    @GetMapping("/{serviceId}/reviews/stats")
    public ResponseEntity<ApiUtil<PhotoServiceReviewResponse.StatDTO>> getReviewStats(
            @PathVariable Long serviceId) {

        PhotoServiceReviewResponse.StatDTO response = reviewService.getReviewStats(serviceId);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    /**
     * 포토서비스 평점 분포 조회 - 인증 불필요
     */
    @GetMapping("/{serviceId}/reviews/rating-distribution")
    public ResponseEntity<ApiUtil<List<PhotoServiceReviewResponse.RatingDistributionDTO>>> getRatingDistribution(
            @PathVariable Long serviceId) {

        List<PhotoServiceReviewResponse.RatingDistributionDTO> response =
                reviewService.getRatingDistribution(serviceId);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    /**
     * 포토서비스 최근 리뷰 10개 조회 - 인증 불필요
     */
    @GetMapping("/{serviceId}/reviews/recent")
    public ResponseEntity<ApiUtil<List<PhotoServiceReviewResponse.ListDTO>>> getRecentReviews(
            @PathVariable Long serviceId) {

        List<PhotoServiceReviewResponse.ListDTO> response = reviewService.getRecentReviews(serviceId);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    /**
     * 리뷰 작성 가능 여부 확인 - 인증 필요
     */
    @GetMapping("/{serviceId}/reviews/can-write")
    public ResponseEntity<ApiUtil<Boolean>> canUserWriteReview(
            @PathVariable Long serviceId,
            @RequestParam Long bookingId,
            @RequestAttribute(Define.LOGIN_USER) LoginUser loginUser) {

        boolean canWrite = reviewService.canUserWriteReview(loginUser.getId(), serviceId, bookingId);
        return ResponseEntity.ok(new ApiUtil<>(canWrite));
    }

    /**
     * 복합 검색 조건으로 리뷰 조회 - 인증 불필요
     */
    @GetMapping("/{serviceId}/reviews/search")
    public ResponseEntity<ApiUtil<Page<PhotoServiceReviewResponse.ListDTO>>> searchReviews(
            @PathVariable Long serviceId,
            @RequestParam(required = false) BigDecimal minRating,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<PhotoServiceReviewResponse.ListDTO> response =
                reviewService.searchReviewsWithFilters(serviceId, minRating, keyword, pageable);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }
}