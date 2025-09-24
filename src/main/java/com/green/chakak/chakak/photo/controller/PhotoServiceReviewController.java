package com.green.chakak.chakak.photo.controller;

import com.green.chakak.chakak.photo.service.PhotoServiceReviewService;
import com.green.chakak.chakak.photo.service.request.PhotoServiceReviewRequest;
import com.green.chakak.chakak.photo.service.response.PhotoServiceReviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class PhotoServiceReviewController {

    private final PhotoServiceReviewService reviewService;

    // ✅ 포토서비스별 리뷰 조회
    @GetMapping("/services/{serviceId}")
    public Page<PhotoServiceReviewResponse.ListDTO> getReviewsByService(
            @PathVariable Long serviceId, Pageable pageable) {
        return reviewService.getReviewsByPhotoService(serviceId, pageable);
    }

    // ✅ 포토그래퍼 전체 리뷰 조회
    @GetMapping("/photographers/{photographerId}")
    public Page<PhotoServiceReviewResponse.ListDTO> getReviewsByPhotographer(
            @PathVariable Long photographerId, Pageable pageable) {
        return reviewService.getReviewsByPhotographer(photographerId, pageable);
    }

    // ✅ 사용자별 리뷰 조회
    @GetMapping("/users/{userId}")
    public Page<PhotoServiceReviewResponse.ListDTO> getReviewsByUser(
            @PathVariable Long userId, Pageable pageable) {
        return reviewService.getReviewsByUser(userId, pageable);
    }

    // ✅ 리뷰 작성
    @PostMapping
    public PhotoServiceReviewResponse.SaveDTO createReview(@RequestBody PhotoServiceReviewRequest.SaveReview dto) {
        return reviewService.createReview(dto);
    }

    // ✅ 리뷰 수정
    @PutMapping("/{reviewId}")
    public PhotoServiceReviewResponse.UpdateDTO updateReview(
            @PathVariable Long reviewId,
            @RequestParam Long userId,
            @RequestBody PhotoServiceReviewRequest.UpdateReview dto) {
        return reviewService.updateReview(reviewId, userId, dto);
    }

    // ✅ 리뷰 삭제
    @DeleteMapping("/{reviewId}")
    public void deleteReview(@PathVariable Long reviewId, @RequestParam Long userId) {
        reviewService.deleteReview(reviewId, userId);
    }

    // ✅ 리뷰 상세 조회
    @GetMapping("/{reviewId}")
    public PhotoServiceReviewResponse.DetailDTO getReviewDetail(@PathVariable Long reviewId) {
        return reviewService.getReviewDetail(reviewId);
    }

    // ✅ 평균 평점/리뷰 수
    @GetMapping("/stats/{serviceId}")
    public PhotoServiceReviewResponse.StatDTO getReviewStats(@PathVariable Long serviceId) {
        return reviewService.getReviewStats(serviceId);
    }

    // ✅ 평점 분포
    @GetMapping("/distribution/{serviceId}")
    public List<PhotoServiceReviewResponse.RatingDistributionDTO> getRatingDistribution(@PathVariable Long serviceId) {
        return reviewService.getRatingDistribution(serviceId);
    }

    // ✅ 최근 리뷰
    @GetMapping("/recent/{serviceId}")
    public List<PhotoServiceReviewResponse.ListDTO> getRecentReviews(@PathVariable Long serviceId) {
        return reviewService.getRecentReviews(serviceId);
    }

    // ✅ 필터 검색
    @GetMapping("/search/{serviceId}")
    public Page<PhotoServiceReviewResponse.ListDTO> searchReviews(
            @PathVariable Long serviceId,
            @RequestParam(required = false) BigDecimal minRating,
            @RequestParam(required = false) String keyword,
            Pageable pageable) {
        return reviewService.searchReviewsWithFilters(serviceId, minRating, keyword, pageable);
    }
}
