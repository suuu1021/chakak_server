package com.green.chakak.chakak.portfolios.service.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

public class PortfolioCategoryRequest {

	// 카테고리 생성 요청
	@Data
	@NoArgsConstructor
	public static class CreateDTO {

		@NotBlank(message = "카테고리명은 필수입니다")
		@Size(max = 50, message = "카테고리명은 50자 이내여야 합니다")
		private String categoryName;

		// 부모 카테고리 ID (선택 사항)
		@Positive(message = "부모 카테고리 ID는 양수여야 합니다")
		private Long parentId;

		// 정렬 순서
		@Min(value = 0, message = "정렬 순서는 0 이상이어야 합니다")
		private Integer sortOrder = 0;
	}

	// 카테고리 수정 요청
	@Data
	@NoArgsConstructor
	public static class UpdateDTO {

		@Size(max = 50, message = "카테고리명은 50자 이내여야 합니다")
		private String categoryName;

		// 부모 카테고리 ID
		@Positive(message = "부모 카테고리 ID는 양수여야 합니다")
		private Long parentId;

		// 정렬 순서
		@Min(value = 0, message = "정렬 순서는 0 이상이어야 합니다")
		private Integer sortOrder;

		// 활성화 여부
		private Boolean isActive;
	}
}