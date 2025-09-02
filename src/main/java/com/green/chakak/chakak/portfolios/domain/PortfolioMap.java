package com.green.chakak.chakak.portfolios.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
@Entity
@Table(name = "PORTFOLIO_MAP")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioMap {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "map_id")
	private Long mapId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "portfolio_id", nullable = false)
	private Portfolio portfolio;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id", nullable = false)
	private PortfolioCategory portfolioCategory;

	@Column(name = "created_at", nullable = false)
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
				portfolioCategory.getCategoryId().equals(that.portfolioCategory.getCategoryId());
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}
