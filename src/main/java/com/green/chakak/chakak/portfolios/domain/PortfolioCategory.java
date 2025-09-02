package com.green.chakak.chakak.portfolios.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PORTFOLIO_CATEGORY")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioCategory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "category_id")
	private Long categoryId;

	@Column(name = "category_name", nullable = false, length = 50)
	private String categoryName;

	@Column(name = "category_description", length = 200)
	private String categoryDescription;

	@Column(name = "is_active", nullable = false)
	private Boolean isActive = true;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	// 매핑 테이블과의 관계
	@OneToMany(mappedBy = "portfolioCategory", cascade = CascadeType.ALL)
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
}