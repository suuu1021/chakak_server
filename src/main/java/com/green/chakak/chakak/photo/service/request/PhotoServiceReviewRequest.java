package com.green.chakak.chakak.photo.service.request;

import com.green.chakak.chakak.booking.domain.BookingInfo;
import com.green.chakak.chakak.photo.domain.PhotoServiceReview;
import com.green.chakak.chakak.photo.domain.PhotoServiceInfo;
import com.green.chakak.chakak.account.domain.User;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

public class PhotoServiceReviewRequest {

	// 리뷰 작성
	@Data
	@NoArgsConstructor
	public static class SaveReview {

		@NotNull(message = "포토서비스 ID는 필수입니다.")
		private Long serviceId;

		@NotNull(message = "사용자 ID는 필수입니다.")
		private Long userId;

		@NotNull(message = "예약 ID는 필수입니다.")
		private Long bookingId;

		@NotNull(message = "평점은 필수입니다.")
		@DecimalMin(value = "1.0", message = "평점은 1.0 이상이어야 합니다.")
		@DecimalMax(value = "5.0", message = "평점은 5.0 이하여야 합니다.")
		@Digits(integer = 1, fraction = 1, message = "평점은 소수점 첫째 자리까지만 입력 가능합니다.")
		private BigDecimal rating;

		@Size(max = 1000, message = "리뷰 내용은 1000자를 초과할 수 없습니다.")
		private String reviewContent;

		@NotNull(message = "익명 여부는 필수입니다.")
		private Boolean isAnonymous;

		public PhotoServiceReview toEntity(PhotoServiceInfo photoServiceInfo, User user, BookingInfo bookingInfo) {
			return PhotoServiceReview.builder()
					.photoServiceInfo(photoServiceInfo)
					.user(user)
					.bookingInfo(bookingInfo)
					.rating(this.rating)
					.reviewContent(this.reviewContent)
					.isAnonymous(this.isAnonymous)
					.build();
		}
	}

	// 리뷰 수정
	@Data
	@NoArgsConstructor
	public static class UpdateReview {

		@NotNull(message = "평점은 필수입니다.")
		@DecimalMin(value = "1.0", message = "평점은 1.0 이상이어야 합니다.")
		@DecimalMax(value = "5.0", message = "평점은 5.0 이하여야 합니다.")
		@Digits(integer = 1, fraction = 1, message = "평점은 소수점 첫째 자리까지만 입력 가능합니다.")
		private BigDecimal rating;

		@Size(max = 1000, message = "리뷰 내용은 1000자를 초과할 수 없습니다.")
		private String reviewContent;

		@NotNull(message = "익명 여부는 필수입니다.")
		private Boolean isAnonymous;
	}

	// 리뷰 검색
	@Data
	@NoArgsConstructor
	public static class SearchReview {

		private Long serviceId;

		@DecimalMin(value = "1.0", message = "최소 평점은 1.0 이상이어야 합니다.")
		@DecimalMax(value = "5.0", message = "최소 평점은 5.0 이하여야 합니다.")
		private BigDecimal minRating;

		private Boolean isAnonymous;

		@Size(max = 100, message = "검색 키워드는 100자를 초과할 수 없습니다.")
		private String keyword;
	}
}