package com.green.chakak.chakak.portfolios.service.request;

import com.green.chakak.chakak.portfolios.domain.Portfolio;
import com.green.chakak.chakak.photographer.domain.PhotographerProfile;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class PortfolioRequest {

	// 포트폴리오 생성
	@Data
	@NoArgsConstructor
	public static class CreateDTO {

		@NotNull(message = "사진작가 ID는 필수입니다")
		private Long photographerId;

		@NotBlank(message = "포트폴리오 제목은 필수입니다")
		@Size(max = 100, message = "제목은 100자 이내여야 합니다")
		private String portfolioTitle;

		@Size(max = 1000, message = "설명은 1000자 이내여야 합니다")
		private String portfolioDescription;

		@Size(max = 100, message = "촬영 위치는 100자 이내여야 합니다")
		private String shootingLocation;

		private LocalDateTime shootingDate;

		private Boolean isPublic = true;

		// 카테고리 ID 목록
		private List<Long> categoryIds;

		public Portfolio toEntity(PhotographerProfile photographerProfile) {
			Portfolio portfolio = new Portfolio();
			portfolio.setPhotographerProfile(photographerProfile);
			portfolio.setPortfolioTitle(this.portfolioTitle);
			portfolio.setPortfolioDescription(this.portfolioDescription);
			portfolio.setShootingLocation(this.shootingLocation);
			portfolio.setShootingDate(this.shootingDate);
			portfolio.setIsPublic(this.isPublic);
			return portfolio;
		}
	}

	// 포트폴리오 수정
	@Data
	@NoArgsConstructor
	public static class UpdateDTO {

		@Size(max = 100, message = "제목은 100자 이내여야 합니다")
		private String portfolioTitle;

		@Size(max = 1000, message = "설명은 1000자 이내여야 합니다")
		private String portfolioDescription;

		@Size(max = 100, message = "촬영 위치는 100자 이내여야 합니다")
		private String shootingLocation;

		private LocalDateTime shootingDate;

		private Boolean isPublic;

		// 카테고리 ID 목록 (수정 시 기존 매핑 삭제 후 새로 등록)
		private List<Long> categoryIds;
	}

	// 포트폴리오 이미지 추가
	@Data
	@NoArgsConstructor
	public static class AddImageDTO {

		@NotNull(message = "포트폴리오 ID는 필수입니다")
		private Long portfolioId;

		@NotBlank(message = "이미지 URL은 필수입니다")
		@Size(max = 500, message = "이미지 URL은 500자 이내여야 합니다")
		private String imageUrl;

		@Size(max = 100, message = "이미지명은 100자 이내여야 합니다")
		private String imageName;

		@Size(max = 500, message = "이미지 설명은 500자 이내여야 합니다")
		private String imageDescription;

		private Long fileSize;
		private Integer imageWidth;
		private Integer imageHeight;
	}

	// 포트폴리오 검색
	@Data
	@NoArgsConstructor
	public static class SearchDTO {

		private String keyword;          // 제목 검색
		private String location;         // 위치 검색
		private List<Long> categoryIds;  // 카테고리 필터
		private LocalDateTime startDate; // 촬영일 시작
		private LocalDateTime endDate;   // 촬영일 종료
		private String sortBy = "latest"; // latest, popular, viewed

		@Min(value = 0, message = "페이지는 0 이상이어야 합니다")
		private int page = 0;

		@Min(value = 1, message = "크기는 1 이상이어야 합니다")
		@Max(value = 100, message = "크기는 100 이하여야 합니다")
		private int size = 10;
	}

	// 카테고리 생성/수정
	@Data
	@NoArgsConstructor
	public static class CategoryDTO {

		@NotBlank(message = "카테고리명은 필수입니다")
		@Size(max = 50, message = "카테고리명은 50자 이내여야 합니다")
		private String categoryName;

		@Size(max = 200, message = "카테고리 설명은 200자 이내여야 합니다")
		private String categoryDescription;

		private Boolean isActive = true;
	}
}