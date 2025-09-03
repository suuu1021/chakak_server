package com.green.chakak.chakak.portfolios.service.response;

import com.green.chakak.chakak.portfolios.domain.PortfolioCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

public class PortfolioCategoryResponse {

	// 카테고리 상세 정보 DTO
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class DetailDTO {
		private Long categoryId;
		private String categoryName;
		private Long parentId;
		private Integer sortOrder;
		private Boolean isActive;
		private LocalDateTime createdAt;
		private LocalDateTime updatedAt;

		public static DetailDTO from(PortfolioCategory category) {
			DetailDTO dto = new DetailDTO();
			dto.setCategoryId(category.getPortfolioCategoryId());
			dto.setCategoryName(category.getCategoryName());
			if (category.getParent() != null) {
				dto.setParentId(category.getParent().getPortfolioCategoryId());
			}
			dto.setSortOrder(category.getSortOrder());
			dto.setIsActive(category.getIsActive());
			dto.setCreatedAt(category.getCreatedAt());
			dto.setUpdatedAt(category.getUpdatedAt());
			return dto;
		}
	}

	// 카테고리 목록 조회 DTO
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ListDTO {
		private Long categoryId;
		private String categoryName;

		public static ListDTO from(PortfolioCategory category) {
			ListDTO dto = new ListDTO();
			dto.setCategoryId(category.getPortfolioCategoryId());
			dto.setCategoryName(category.getCategoryName());
			return dto;
		}
	}
}