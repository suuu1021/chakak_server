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
		private String portfolioTitle;
		private String portfolioDescription;
		private String shootingLocation;
		private LocalDateTime shootingDate;
		private Integer viewCount;
		private Integer likeCount;
		private Boolean isPublic;
		private LocalDateTime createdAt;
		private LocalDateTime updatedAt;

		// 포트폴리오 이미지 목록
		private List<ImageDTO> images;

		// 카테고리 목록
		private List<CategoryDTO> categories;

		public DetailDTO(Portfolio portfolio) {
			this.portfolioId = portfolio.getPortfolioId();
			this.photographerId = portfolio.getPhotographerProfile().getPhotographerId();
			this.photographerName = portfolio.getPhotographerProfile().getBusinessName();
			this.portfolioTitle = portfolio.getPortfolioTitle();
			this.portfolioDescription = portfolio.getPortfolioDescription();
			this.shootingLocation = portfolio.getShootingLocation();
			this.shootingDate = portfolio.getShootingDate();
			this.viewCount = portfolio.getViewCount();
			this.likeCount = portfolio.getLikeCount();
			this.isPublic = portfolio.getIsPublic();
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
		private Long photographerId;
		private String photographerName;
		private String portfolioTitle;
		private String portfolioDescription;
		private Integer viewCount;
		private Integer likeCount;
		private LocalDateTime createdAt;
		private String thumbnailUrl;

		public ListDTO(Portfolio portfolio) {
			this.portfolioId = portfolio.getPortfolioId();
			this.photographerId = portfolio.getPhotographerProfile().getPhotographerId();
			this.photographerName = portfolio.getPhotographerProfile().getBusinessName();
			this.portfolioTitle = portfolio.getPortfolioTitle();
			this.portfolioDescription = portfolio.getPortfolioDescription();
			this.viewCount = portfolio.getViewCount();
			this.likeCount = portfolio.getLikeCount();
			this.createdAt = portfolio.getCreatedAt();

			// 썸네일 URL 설정 (첫 번째 이미지)
			this.thumbnailUrl = portfolio.getPortfolioImages().isEmpty() ?
					null : portfolio.getPortfolioImages().get(0).getImageUrl();
		}

		public static ListDTO from(Portfolio portfolio) {
			return new ListDTO(portfolio);
		}
	}

	// 이미지 정보
	@Data
	@NoArgsConstructor
	public static class ImageDTO {

		private Long imageId;
		private String imageUrl;
		private String imageName;
		private String imageDescription;
		private Integer imageOrder;
		private Long fileSize;
		private Integer imageWidth;
		private Integer imageHeight;
		private LocalDateTime createdAt;

		public ImageDTO(PortfolioImage image) {
			this.imageId = image.getImageId();
			this.imageUrl = image.getImageUrl();
			this.imageName = image.getImageName();
			this.imageDescription = image.getImageDescription();
			this.imageOrder = image.getImageOrder();
			this.fileSize = image.getFileSize();
			this.imageWidth = image.getImageWidth();
			this.imageHeight = image.getImageHeight();
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
		private String categoryDescription;
		private Boolean isActive;

		public CategoryDTO(PortfolioCategory category) {
			this.categoryId = category.getCategoryId();
			this.categoryName = category.getCategoryName();
			this.categoryDescription = category.getCategoryDescription();
			this.isActive = category.getIsActive();
		}

		public static CategoryDTO from(PortfolioCategory category) {
			return new CategoryDTO(category);
		}
	}
}