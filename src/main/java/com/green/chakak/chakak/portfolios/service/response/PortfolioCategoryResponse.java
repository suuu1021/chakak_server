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
		private String parentName; // 부모 카테고리명 추가
		private Integer sortOrder;
		private Boolean isActive;
		private LocalDateTime createdAt;
		private LocalDateTime updatedAt;

		public static DetailDTO from(PortfolioCategory category) {
			DetailDTO dto = new DetailDTO();
			dto.setCategoryId(category.getPortfolioCategoryId());
			dto.setCategoryName(category.getCategoryName());

			// NPE 방지 - 부모 카테고리 정보
			if (category.getParent() != null) {
				dto.setParentId(category.getParent().getPortfolioCategoryId());
				dto.setParentName(category.getParent().getCategoryName());
			}

			dto.setSortOrder(category.getSortOrder());
			dto.setIsActive(category.getIsActive());
			dto.setCreatedAt(category.getCreatedAt());
			dto.setUpdatedAt(category.getUpdatedAt());
			return dto;
		}
	}

	// 카테고리 목록 조회 DTO (간단한 정보)
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ListDTO {
		private Long categoryId;
		private String categoryName;
		private Long parentId; // 부모 카테고리 ID 추가
		private String parentName; // 부모 카테고리명 추가
		private Integer sortOrder; // 정렬 순서 추가
		private Boolean isActive; // 활성화 상태 추가

		public static ListDTO from(PortfolioCategory category) {
			ListDTO dto = new ListDTO();
			dto.setCategoryId(category.getPortfolioCategoryId());
			dto.setCategoryName(category.getCategoryName());

			// NPE 방지 - 부모 카테고리 정보
			if (category.getParent() != null) {
				dto.setParentId(category.getParent().getPortfolioCategoryId());
				dto.setParentName(category.getParent().getCategoryName());
			}

			dto.setSortOrder(category.getSortOrder());
			dto.setIsActive(category.getIsActive());
			return dto;
		}
	}
}