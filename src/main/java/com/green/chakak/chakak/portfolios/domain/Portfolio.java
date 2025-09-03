package com.green.chakak.chakak.portfolios.domain;

import com.green.chakak.chakak.photographer.domain.PhotographerProfile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PORTFOLIO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Portfolio {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PORTFOLIO_ID")
	private Long portfolioId;

	// Photographer 엔티티와 연결
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PHOTOGRAPHER_PROFILE_ID", nullable = false)
	private PhotographerProfile photographerProfile;

	@Column(name = "TITLE", nullable = false, length = 255)
	private String title;

	@Column(name = "DESCRIPTION", columnDefinition = "TEXT")
	private String description;

	@Column(name = "THUMBNAIL_URL", nullable = false, length = 512)
	private String thumbnailUrl;

	@Column(name = "CREATED_AT", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "UPDATED_AT")
	private LocalDateTime updatedAt;

	// 포트폴리오 이미지들과의 관계
	@OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PortfolioImage> portfolioImages = new ArrayList<>();

	// 카테고리 매핑과의 관계
	@OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL)
	private List<PortfolioMap> portfolioMaps = new ArrayList<>();

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

	// 편의 메서드
	public void addPortfolioImage(PortfolioImage portfolioImage) {
		portfolioImages.add(portfolioImage);
		portfolioImage.setPortfolio(this);
	}

	public void removePortfolioImage(PortfolioImage portfolioImage) {
		portfolioImages.remove(portfolioImage);
		portfolioImage.setPortfolio(null);
	}
}