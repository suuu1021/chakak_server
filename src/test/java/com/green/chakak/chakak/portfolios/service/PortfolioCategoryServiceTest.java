package com.green.chakak.chakak.portfolios.service;

import com.green.chakak.chakak.portfolios.domain.PortfolioCategory;
import com.green.chakak.chakak.portfolios.service.repository.PortfolioCategoryJpaRepository;
import com.green.chakak.chakak.portfolios.service.request.PortfolioRequest;
import com.green.chakak.chakak.portfolios.service.response.PortfolioResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class PortfolioCategoryServiceTest {

	@InjectMocks
	private PortfolioCategoryService portfolioCategoryService;

	@Mock
	private PortfolioCategoryJpaRepository portfolioCategoryRepository;

	private PortfolioCategory mockCategory;

	@BeforeEach
	void setUp() {
		// 테스트용 Mock 카테고리 생성
		mockCategory = new PortfolioCategory();
		mockCategory.setCategoryId(1L);
		mockCategory.setCategoryName("웨딩촬영");
		mockCategory.setCategoryDescription("웨딩 전문 촬영");
		mockCategory.setIsActive(true);
		mockCategory.setCreatedAt(LocalDateTime.now());
		mockCategory.setUpdatedAt(LocalDateTime.now());
	}

	@Test
	@DisplayName("카테고리 생성 성공 테스트")
	void createCategory_Success() {
		// Given (준비)
		PortfolioRequest.CategoryDTO request = new PortfolioRequest.CategoryDTO();
		request.setCategoryName("웨딩촬영");
		request.setCategoryDescription("웨딩 전문 촬영");
		request.setIsActive(true);

		given(portfolioCategoryRepository.existsByCategoryName("웨딩촬영")).willReturn(false);
		given(portfolioCategoryRepository.save(any(PortfolioCategory.class))).willReturn(mockCategory);

		// When (실행)
		PortfolioResponse.CategoryDTO result = portfolioCategoryService.createCategory(request);

		// Then (검증)
		assertNotNull(result);
		assertEquals(1L, result.getCategoryId());
		assertEquals("웨딩촬영", result.getCategoryName());
		assertEquals("웨딩 전문 촬영", result.getCategoryDescription());
		assertTrue(result.getIsActive());

		verify(portfolioCategoryRepository, times(1)).existsByCategoryName("웨딩촬영");
		verify(portfolioCategoryRepository, times(1)).save(any(PortfolioCategory.class));
	}

	@Test
	@DisplayName("중복된 카테고리명으로 생성 실패 테스트")
	void createCategory_DuplicateName() {
		// Given
		PortfolioRequest.CategoryDTO request = new PortfolioRequest.CategoryDTO();
		request.setCategoryName("웨딩촬영");

		given(portfolioCategoryRepository.existsByCategoryName("웨딩촬영")).willReturn(true);

		// When & Then
		IllegalArgumentException exception = assertThrows(
				IllegalArgumentException.class,
				() -> portfolioCategoryService.createCategory(request)
		);

		assertEquals("이미 존재하는 카테고리명입니다: 웨딩촬영", exception.getMessage());
		verify(portfolioCategoryRepository, times(1)).existsByCategoryName("웨딩촬영");
		verify(portfolioCategoryRepository, times(0)).save(any(PortfolioCategory.class));
	}

	@Test
	@DisplayName("카테고리 조회 성공 테스트")
	void getCategory_Success() {
		// Given
		Long categoryId = 1L;
		given(portfolioCategoryRepository.findById(categoryId)).willReturn(Optional.of(mockCategory));

		// When
		PortfolioResponse.CategoryDTO result = portfolioCategoryService.getCategory(categoryId);

		// Then
		assertNotNull(result);
		assertEquals(categoryId, result.getCategoryId());
		assertEquals("웨딩촬영", result.getCategoryName());

		verify(portfolioCategoryRepository, times(1)).findById(categoryId);
	}

	@Test
	@DisplayName("존재하지 않는 카테고리 조회 실패 테스트")
	void getCategory_NotFound() {
		// Given
		Long categoryId = 999L;
		given(portfolioCategoryRepository.findById(categoryId)).willReturn(Optional.empty());

		// When & Then
		IllegalArgumentException exception = assertThrows(
				IllegalArgumentException.class,
				() -> portfolioCategoryService.getCategory(categoryId)
		);

		assertEquals("존재하지 않는 카테고리입니다: 999", exception.getMessage());
		verify(portfolioCategoryRepository, times(1)).findById(categoryId);
	}
}