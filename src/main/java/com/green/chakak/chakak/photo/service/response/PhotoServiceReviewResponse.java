//package com.green.chakak.chakak.photo.service.response;
//
//import com.green.chakak.chakak.photo.domain.PhotoServiceReview;
//import lombok.Data;
//
//import java.math.BigDecimal;
//import java.sql.Timestamp;
//
//public class PhotoServiceReviewResponse {
//
//	@Data
//	public static class SaveDTO {
//		private Long reviewId;
//		private Long serviceId;
//		private Long userId;
//		private Long bookingId;
//		private BigDecimal rating;
//		private String reviewContent;
//		private Boolean isAnonymous;
//		private Timestamp createdAt;
//
//		public SaveDTO(PhotoServiceReview review) {
//			this.reviewId = review.getReviewId();
//			this.serviceId = review.getPhotoServiceInfo().getServiceId();
//			this.userId = review.getUser().getUserId();
//			this.bookingId = review.getBookingInfo().getBookingId();
//			this.rating = review.getRating();
//			this.reviewContent = review.getReviewContent();
//			this.isAnonymous = review.getIsAnonymous();
//			this.createdAt = review.getCreatedAt();
//		}
//	}
//
//	@Data
//	public static class UpdateDTO {
//		private Long reviewId;
//		private BigDecimal rating;
//		private String reviewContent;
//		private Boolean isAnonymous;
//		private Timestamp updatedAt;
//
//		public UpdateDTO(PhotoServiceReview review) {
//			this.reviewId = review.getReviewId();
//			this.rating = review.getRating();
//			this.reviewContent = review.getReviewContent();
//			this.isAnonymous = review.getIsAnonymous();
//			this.updatedAt = review.getUpdatedAt();
//		}
//	}
//
//	@Data
//	public static class DetailDTO {
//		private Long reviewId;
//		private Long serviceId;
//		private String serviceTitle;
//		private Long userId;
//		private Long bookingId;
//		private BigDecimal rating;
//		private String reviewContent;
//		private Boolean isAnonymous;
//		private Timestamp createdAt;
//		private Timestamp updatedAt;
//
//		public DetailDTO(PhotoServiceReview review) {
//			this.reviewId = review.getReviewId();
//			this.serviceId = review.getPhotoServiceInfo().getServiceId();
//			this.serviceTitle = review.getPhotoServiceInfo().getTitle();
//			this.userId = review.getUser().getUserId();
//			this.bookingId = review.getBookingInfo().getBookingId();
//			this.rating = review.getRating();
//			this.reviewContent = review.getReviewContent();
//			this.isAnonymous = review.getIsAnonymous();
//			this.createdAt = review.getCreatedAt();
//			this.updatedAt = review.getUpdatedAt();
//		}
//	}
//
//	@Data
//	public static class ListDTO {
//		private Long reviewId;
//		private Long userId;
//		private BigDecimal rating;
//		private String reviewContent;
//		private Boolean isAnonymous;
//		private Timestamp createdAt;
//
//		public ListDTO(PhotoServiceReview review) {
//			this.reviewId = review.getReviewId();
//			this.userId = review.getUser().getUserId();
//			this.rating = review.getRating();
//			this.reviewContent = review.getReviewContent();
//			this.isAnonymous = review.getIsAnonymous();
//			this.createdAt = review.getCreatedAt();
//		}
//	}
//
//	@Data
//	public static class StatDTO {
//		private Double averageRating;
//		private Long totalReviews;
//
//		public StatDTO(Double averageRating, Long totalReviews) {
//			this.averageRating = averageRating != null ? averageRating : 0.0;
//			this.totalReviews = totalReviews != null ? totalReviews : 0L;
//		}
//	}
//}