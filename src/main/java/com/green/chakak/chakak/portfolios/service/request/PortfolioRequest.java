package com.green.chakak.chakak.portfolios.service.request;

import com.green.chakak.chakak.portfolios.domain.Portfolio;
import com.green.chakak.chakak.photographer.domain.PhotographerProfile;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

public class PortfolioRequest {

	// 포트폴리오 생성
	@Data
	@NoArgsConstructor
	public static class CreateDTO {

		@NotNull(message = "사진작가 ID는 필수입니다")
		private Long photographerId;

		@NotBlank(message = "포트폴리오 제목은 필수입니다")
		@Size(max = 255, message = "제목은 255자 이내여야 합니다")
		private String title;

		@Size(max = 1000, message = "설명은 텍스트 필드입니다")
		private String description;

		@NotBlank(message = "썸네일 URL은 필수입니다")
		@Size(max = 512, message = "썸네일 URL은 512자 이내여야 합니다")
		private String thumbnailUrl;

		// 카테고리 ID 목록
		private List<Long> categoryIds;

		public Portfolio toEntity(PhotographerProfile photographerProfile) {
			Portfolio portfolio = new Portfolio();
			portfolio.setPhotographerProfile(photographerProfile);
			portfolio.setTitle(this.title);
			portfolio.setDescription(this.description);
			portfolio.setThumbnailUrl(this.thumbnailUrl);
			return portfolio;
		}
	}

	// 포트폴리오 수정
	@Data
	@NoArgsConstructor
	public static class UpdateDTO {

		@Size(max = 255, message = "제목은 255자 이내여야 합니다")
		private String title;

		@Size(max = 1000, message = "설명은 텍스트 필드입니다")
		private String description;

		@Size(max = 512, message = "썸네일 URL은 512자 이내여야 합니다")
		private String thumbnailUrl;

		// 카테고리 ID 목록 (수정 시 기존 매핑 삭제 후 새로 등록)
		private List<Long> categoryIds;
	}

	// 이미지 추가
	@Data
	@NoArgsConstructor
	public static class AddImageDTO {

		@NotNull(message = "포트폴리오 ID는 필수입니다")
		private Long portfolioId;

		@NotBlank(message = "이미지 URL은 필수입니다")
		@Size(max = 512, message = "이미지 URL은 512자 이내여야 합니다")
		private String imageUrl;

		private Boolean isMain;
	}

	// 포트폴리오 검색
	@Data
	@NoArgsConstructor
	public static class SearchDTO {

		private String keyword; // 제목, 설명, 키워드
		private List<Long> categoryIds; // 카테고리 필터
		private String sortBy = "latest"; // latest, popular, viewed

		@Min(value = 0, message = "페이지는 0 이상이어야 합니다")
		private int page = 0;

		@Min(value = 1, message = "크기는 1 이상이어야 합니다")
		@Max(value = 100, message = "크기는 100 이하여야 합니다")
		private int size = 10;
	}
}