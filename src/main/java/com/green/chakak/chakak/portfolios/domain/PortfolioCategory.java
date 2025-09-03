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
	@Column(name = "PORTFOLIO_CATEGORY_ID")
	private Long portfolioCategoryId;

	@Column(name = "CATEGORY_NAME", nullable = false, length = 50)
	private String categoryName;

	// 계층구조를 위한 자기 참조
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PARENT_ID")
	private PortfolioCategory parent;

	// 하위 카테고리들
	@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
	private List<PortfolioCategory> children = new ArrayList<>();

	@Column(name = "SORT_ORDER", nullable = false)
	private Integer sortOrder = 0;

	@Column(name = "IS_ACTIVE", nullable = false)
	private Boolean isActive = true;

	@Column(name = "CREATED_AT", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "UPDATED_AT")
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

	// 편의 메서드 - 하위 카테고리 추가
	public void addChild(PortfolioCategory child) {
		children.add(child);
		child.setParent(this);
	}

	// 편의 메서드 - 하위 카테고리 제거
	public void removeChild(PortfolioCategory child) {
		children.remove(child);
		child.setParent(null);
	}

	// 편의 메서드 - 루트 카테고리인지 확인
	public boolean isRoot() {
		return parent == null;
	}

	// 편의 메서드 - 리프 카테고리인지 확인
	public boolean isLeaf() {
		return children.isEmpty();
	}
}