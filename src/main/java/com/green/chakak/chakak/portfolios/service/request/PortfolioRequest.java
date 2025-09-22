package com.green.chakak.chakak.portfolios.service.request;

import com.green.chakak.chakak.photo.service.response.PriceInfoResponse;
import com.green.chakak.chakak.portfolios.domain.Portfolio;
import com.green.chakak.chakak.photographer.domain.PhotographerProfile;
import com.green.chakak.chakak.portfolios.service.response.PortfolioResponse;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

public class PortfolioRequest {

	// 포트폴리오 생성
	@Data
	@NoArgsConstructor
	public static class CreateDTO {

		@NotBlank(message = "포트폴리오 제목은 필수입니다")
		@Size(max = 255, message = "제목은 255자 이내여야 합니다")
		private String title;

		@Size(max = 2000, message = "설명은 2000자 이내여야 합니다")
		private String description;

		@NotBlank(message = "썸네일 데이터는 필수입니다")
		private String thumbnailUrl; // Base64 인코딩된 썸네일 이미지 데이터

		// 카테고리 ID 목록 - Long 타입이 맞음
		@Size(max = 10, message = "카테고리는 최대 10개까지 선택 가능합니다")
		private List<@NotNull @Positive Long> categoryIds;

		// Base64 이미지 데이터 목록
		@Size(max = 20, message = "이미지는 최대 20개까지 등록 가능합니다")
		private List<AddImageDTO> imageInfoList;

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

		@Size(max = 2000, message = "설명은 2000자 이내여야 합니다")
		private String description;

		private String thumbnailUrl; // Base64 인코딩된 썸네일 이미지 데이터

		// 카테고리 ID 목록 (수정 시 기존 매핑 삭제 후 새로 등록)
		@Size(max = 10, message = "카테고리는 최대 10개까지 선택 가능합니다")
		private List<@NotNull @Positive Long> categoryIds;

		// Base64 이미지 데이터 목록 (수정 시 기존 이미지 삭제 후 새로 등록)
		@Size(max = 20, message = "이미지는 최대 20개까지 등록 가능합니다")
		private List<AddImageDTO> imageInfoList;
	}

	// 이미지 추가
	@Data
	@NoArgsConstructor
	public static class AddImageDTO {

		@NotNull(message = "포트폴리오 ID는 필수입니다")
		@Positive(message = "포트폴리오 ID는 양수여야 합니다")
		private Long portfolioId;

		// Base64 이미지 데이터 (data:image/jpeg;base64,/9j/4AAQ... 형태)
		@NotBlank(message = "이미지 데이터는 필수입니다")
		private String imageData;

		// 원본 파일명 (선택사항)
		private String originalFileName;

		private Boolean isMain = false; // 기본값 설정
	}

	// 포트폴리오 검색
	@Data
	@NoArgsConstructor
	public static class SearchDTO {

		@Size(max = 100, message = "검색 키워드는 100자 이내여야 합니다")
		private String keyword; // 제목, 설명, 키워드

		@Size(max = 10, message = "카테고리 필터는 최대 10개까지 가능합니다")
		private List<@NotNull @Positive Long> categoryIds; // 카테고리 필터

		@Pattern(regexp = "^(latest|popular|viewed)$",
				message = "정렬 기준은 latest, popular, viewed 중 하나여야 합니다")
		private String sortBy = "latest"; // latest, popular, viewed

		@Min(value = 0, message = "페이지는 0 이상이어야 합니다")
		private int page = 0;

		@Min(value = 1, message = "크기는 1 이상이어야 합니다")
		@Max(value = 100, message = "크기는 100 이하여야 합니다")
		private int size = 10;
	}
}