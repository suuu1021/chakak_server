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
	@Column(name = "portfolio_id")
	private Long portfolioId;

	// Photographer 엔티티와 연결
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "photographer_id", nullable = false)
	private PhotographerProfile photographerProfile;

	@Column(name = "portfolio_title", nullable = false, length = 100)
	private String portfolioTitle;

	@Column(name = "portfolio_description", length = 1000)
	private String portfolioDescription;

	@Column(name = "shooting_location", length = 100)
	private String shootingLocation;

	@Column(name = "shooting_date")
	private LocalDateTime shootingDate;

	@Column(name = "view_count", nullable = false)
	private Integer viewCount = 0;

	@Column(name = "like_count", nullable = false)
	private Integer likeCount = 0;

	@Column(name = "is_public", nullable = false)
	private Boolean isPublic = true;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
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