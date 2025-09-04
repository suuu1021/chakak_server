package com.green.chakak.chakak.photo.domain;

import com.green.chakak.chakak.account.domain.User;

import com.green.chakak.chakak.booking.domain.BookingInfo;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "photo_service_review")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhotoServiceReview {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "review_id")
	private Long reviewId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "service_id", nullable = false)
	private PhotoServiceInfo photoServiceInfo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "booking_id", nullable = false)
	private BookingInfo bookingInfo;

	@Column(name = "rating", nullable = false, precision = 2, scale = 1)
	private BigDecimal rating;

	@Column(name = "review_content", columnDefinition = "TEXT")
	private String reviewContent;

	@Column(name = "is_anonymous", nullable = false)
	private Boolean isAnonymous;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false)
	private Timestamp createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private Timestamp updatedAt;

	@Builder
	public PhotoServiceReview(PhotoServiceInfo photoServiceInfo, User user,
							  BookingInfo bookingInfo, BigDecimal rating,
							  String reviewContent, Boolean isAnonymous) {
		this.photoServiceInfo = photoServiceInfo;
		this.user = user;
		this.bookingInfo = bookingInfo;
		this.rating = rating;
		this.reviewContent = reviewContent;
		this.isAnonymous = isAnonymous;
	}
}