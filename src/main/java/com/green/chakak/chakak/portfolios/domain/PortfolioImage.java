package com.green.chakak.chakak.portfolios.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "PORTFOLIO_IMAGE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioImage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PORTFOLIO_IMAGE_ID")
	private Long portfolioImageId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PORTFOLIO_ID", nullable = false)
	private Portfolio portfolio;

	@Column(name = "IMAGE_URL", nullable = false, length = 512)
	private String imageUrl;

	@Column(name = "IS_MAIN", nullable = true)
	private Boolean isMain = false;

	@Column(name = "CREATED_AT", nullable = false)
	private LocalDateTime createdAt;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		if (isMain == null) {
			isMain = false;
		}
	}

	// 편의 메서드 - 메인 이미지로 설정
	public void setAsMain() {
		this.isMain = true;
	}

	// 편의 메서드 - 메인 이미지 해제
	public void unsetAsMain() {
		this.isMain = false;
	}
}