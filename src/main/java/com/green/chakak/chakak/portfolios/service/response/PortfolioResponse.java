package com.green.chakak.chakak.portfolios.service.response;

import com.green.chakak.chakak.portfolios.domain.Portfolio;
import com.green.chakak.chakak.portfolios.domain.PortfolioImage;
import com.green.chakak.chakak.portfolios.domain.PortfolioCategory;
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
		private String title;
		private String description;
		private String thumbnailUrl;
		private LocalDateTime createdAt;
		private LocalDateTime updatedAt;

		// 포트폴리오 이미지 목록
		private List<ImageDTO> images;

		// 카테고리 목록
		private List<CategoryDTO> categories;

		public DetailDTO(Portfolio portfolio) {
			this.portfolioId = portfolio.getPortfolioId();
			this.photographerId = portfolio.getPhotographerProfile().getPhotographerProfileId();
			this.photographerName = portfolio.getPhotographerProfile().getBusinessName();
			this.title = portfolio.getTitle();
			this.description = portfolio.getDescription();
			this.thumbnailUrl = portfolio.getThumbnailUrl();
			this.createdAt = portfolio.getCreatedAt();
			this.updatedAt = portfolio.getUpdatedAt();

			// 이미지 변환
			this.images = portfolio.getPortfolioImages().stream()
					.map(ImageDTO::new)
					.collect(Collectors.toList());

			// 카테고리 변환
			this.categories = portfolio.getPortfolioMaps().stream()
					.map(map -> new CategoryDTO(map.getPortfolioCategory()))
					.collect(Collectors.toList());
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
		private Boolean isMain;
		private LocalDateTime createdAt;

		public ImageDTO(PortfolioImage image) {
			this.portfolioImageId = image.getPortfolioImageId();
			this.imageUrl = image.getImageUrl();
			this.isMain = image.getIsMain();
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

		public CategoryDTO(PortfolioCategory category) {
			this.categoryId = category.getPortfolioCategoryId();
			this.categoryName = category.getCategoryName();
		}

		public static CategoryDTO from(PortfolioCategory category) {
			return new CategoryDTO(category);
		}
	}
}