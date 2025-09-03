package com.green.chakak.chakak.portfolios.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "PORTFOLIO_CATEGORY_MAP")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioMap {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PORTFOLIO_MAP_ID")
	private Long portfolioMapId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PORTFOLIO_ID", nullable = false)
	private Portfolio portfolio;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PORTFOLIO_CATEGORY_ID", nullable = false)
	private PortfolioCategory portfolioCategory;

	@Column(name = "CREATED_AT", nullable = false)
	private LocalDateTime createdAt;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
	}

	// 복합 유니크 제약조건을 위한 equals/hashCode
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof PortfolioMap)) return false;
		PortfolioMap that = (PortfolioMap) o;
		return portfolio != null && portfolioCategory != null &&
				portfolio.getPortfolioId().equals(that.portfolio.getPortfolioId()) &&
				portfolioCategory.getPortfolioCategoryId().equals(that.portfolioCategory.getPortfolioCategoryId());
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}