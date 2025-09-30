package com.green.chakak.chakak.portfolios.service.response;

import com.green.chakak.chakak.portfolios.domain.Portfolio;
import com.green.chakak.chakak.portfolios.domain.PortfolioCategory;
import com.green.chakak.chakak.portfolios.domain.PortfolioImage;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class PortfolioResponse {

	// 포트폴리오 상세 조회
	@Data
	@NoArgsConstructor
	public static class DetailDTO {

		private Long portfolioId;
		private Long photographerId;
		private String photographerName;
		private String photographerEmail;
		private String title;
		private String description;
		private String thumbnailUrl;
		private LocalDateTime createdAt;
		private LocalDateTime updatedAt;
		private Long photographerUserId;

		// 포트폴리오 이미지 목록
		private List<ImageDTO> images;

		// 카테고리 목록
		private List<CategoryDTO> categories;

		public DetailDTO(Portfolio portfolio) {
			this.portfolioId = portfolio.getPortfolioId();

			// 사진작가 정보 안전하게 가져오기
			if (portfolio.getPhotographerProfile() != null) {
				this.photographerId = portfolio.getPhotographerProfile().getPhotographerProfileId();
				this.photographerName = portfolio.getPhotographerProfile().getBusinessName();
				if (portfolio.getPhotographerProfile().getUser() != null) {
					this.photographerUserId = portfolio.getPhotographerProfile().getUser().getUserId();
				}

			}

			this.title = portfolio.getTitle();
			this.description = portfolio.getDescription();
			this.thumbnailUrl = portfolio.getThumbnailUrl();
			this.createdAt = portfolio.getCreatedAt();
			this.updatedAt = portfolio.getUpdatedAt();

			// NPE 방지 - 이미지 변환
			this.images = portfolio.getPortfolioImages() != null
					? portfolio.getPortfolioImages().stream()
					.map(ImageDTO::new)
					.sorted((a, b) -> {
						// 메인 이미지 우선 정렬
						if (a.getIsMain() && !b.getIsMain()) return -1;
						if (!a.getIsMain() && b.getIsMain()) return 1;
						return a.getCreatedAt().compareTo(b.getCreatedAt());
					})
					.collect(Collectors.toList())
					: List.of();

			// NPE 방지 - 카테고리 변환
			this.categories = portfolio.getPortfolioMaps() != null
					? portfolio.getPortfolioMaps().stream()
					.map(map -> new CategoryDTO(map.getPortfolioCategory()))
					.collect(Collectors.toList())
					: List.of();
		}

		public static DetailDTO from(Portfolio portfolio) {
			return new DetailDTO(portfolio);
		}
	}

	// 포트폴리오 목록 조회 (간단한 정보)
	@Data
	@NoArgsConstructor
	public static class ListDTO {

		private Long portfolioId;
		private Long photographerUserId;
		private Long photographerId; // 추가
		private String photographerName; // 추가
		private String title;
		private String description;
		private String thumbnailUrl;
		private LocalDateTime createdAt;

		public ListDTO(Portfolio portfolio) {
			this.portfolioId = portfolio.getPortfolioId();
			this.title = portfolio.getTitle();
			this.description = portfolio.getDescription();
			this.thumbnailUrl = portfolio.getThumbnailUrl();
			this.createdAt = portfolio.getCreatedAt();

			// 사진작가 정보 NPE 방지
			if (portfolio.getPhotographerProfile() != null) {
				this.photographerId = portfolio.getPhotographerProfile().getPhotographerProfileId();
				this.photographerName = portfolio.getPhotographerProfile().getBusinessName();
				if (portfolio.getPhotographerProfile().getUser() != null) {
					this.photographerUserId = portfolio.getPhotographerProfile().getUser().getUserId();
				}
			}
		}

		public static ListDTO from(Portfolio portfolio) {
			return new ListDTO(portfolio);
		}
	}

	// 이미지 정보
	@Data
	@NoArgsConstructor
	public static class ImageDTO {

		private Long portfolioImageId;
		private String imageUrl;
		private Boolean isMain = false; // 기본값 설정
		private Integer sortOrder = 0; // 정렬 순서 추가
		private LocalDateTime createdAt;

		public ImageDTO(PortfolioImage image) {
			this.portfolioImageId = image.getPortfolioImageId();
			this.imageUrl = image.getImageUrl();
			this.isMain = image.getIsMain() != null ? image.getIsMain() : false;
			this.createdAt = image.getCreatedAt();
		}

		public static ImageDTO from(PortfolioImage image) {
			return new ImageDTO(image);
		}
	}

	// 카테고리 정보
	@Data
	@NoArgsConstructor
	public static class CategoryDTO {

		private Long categoryId;
		private String categoryName;
		private Long parentId; // 부모 카테고리 ID 추가
		private String parentName; // 부모 카테고리명 추가
		private Integer sortOrder; // 정렬 순서 추가

		public CategoryDTO(PortfolioCategory category) {
			this.categoryId = category.getPortfolioCategoryId();
			this.categoryName = category.getCategoryName();
			this.sortOrder = category.getSortOrder();

			// 부모 카테고리 정보
			if (category.getParent() != null) {
				this.parentId = category.getParent().getPortfolioCategoryId();
				this.parentName = category.getParent().getCategoryName();
			}
		}

		public static CategoryDTO from(PortfolioCategory category) {
			return new CategoryDTO(category);
		}
	}
}