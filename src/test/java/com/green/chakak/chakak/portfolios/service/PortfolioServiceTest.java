package com.green.chakak.chakak.portfolios.service;

import com.green.chakak.chakak.photographer.domain.PhotographerProfile;
import com.green.chakak.chakak.photographer.service.repository.PhotographerRepository;
import com.green.chakak.chakak.portfolios.domain.Portfolio;
import com.green.chakak.chakak.portfolios.service.repository.PortfolioCategoryJpaRepository;
import com.green.chakak.chakak.portfolios.service.repository.PortfolioImageJpaRepository;
import com.green.chakak.chakak.portfolios.service.repository.PortfolioJpaRepository;
import com.green.chakak.chakak.portfolios.service.repository.PortfolioMapJpaRepository;
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
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PortfolioServiceTest {

	@InjectMocks
	private PortfolioService portfolioService;

	@Mock
	private PortfolioJpaRepository portfolioRepository;

	@Mock
	private PhotographerRepository photographerRepository;

	@Mock
	private PortfolioCategoryJpaRepository categoryRepository;

	@Mock
	private PortfolioMapJpaRepository portfolioMapRepository;

	@Mock
	private PortfolioImageJpaRepository portfolioImageRepository;

	private PhotographerProfile mockPhotographer;
	private Portfolio mockPortfolio;

	@BeforeEach
	void setUp() {
		// Mock 포토그래퍼 생성
		mockPhotographer = new PhotographerProfile();
		mockPhotographer.setPhotographerId(1L);
		mockPhotographer.setBusinessName("테스트 스튜디오");

		// Mock 포트폴리오 생성
		mockPortfolio = new Portfolio();
		mockPortfolio.setPortfolioId(1L);
		mockPortfolio.setPhotographerProfile(mockPhotographer);
		mockPortfolio.setPortfolioTitle("테스트 포트폴리오");
		mockPortfolio.setPortfolioDescription("테스트 설명");
		mockPortfolio.setViewCount(0);
		mockPortfolio.setLikeCount(0);
		mockPortfolio.setIsPublic(true);
		mockPortfolio.setCreatedAt(LocalDateTime.now());
		mockPortfolio.setPortfolioImages(new ArrayList<>());
		mockPortfolio.setPortfolioMaps(new ArrayList<>());
	}

	@Test
	@DisplayName("포트폴리오 생성 성공 테스트")
	void createPortfolio_Success() {
		// Given
		PortfolioRequest.CreateDTO request = new PortfolioRequest.CreateDTO();
		request.setPhotographerId(1L);
		request.setPortfolioTitle("새 포트폴리오");
		request.setPortfolioDescription("새 포트폴리오 설명");
		request.setIsPublic(true);

		given(photographerRepository.findById(1L)).willReturn(Optional.of(mockPhotographer));
		given(portfolioRepository.save(any(Portfolio.class))).willReturn(mockPortfolio);

		// When
		PortfolioResponse.DetailDTO result = portfolioService.createPortfolio(request);

		// Then
		assertNotNull(result);
		assertEquals(1L, result.getPortfolioId());
		assertEquals("테스트 포트폴리오", result.getPortfolioTitle());
		assertEquals(1L, result.getPhotographerId());
		assertEquals("테스트 스튜디오", result.getPhotographerName());

		verify(photographerRepository, times(1)).findById(1L);
		verify(portfolioRepository, times(1)).save(any(Portfolio.class));
	}

	@Test
	@DisplayName("존재하지 않는 사진작가로 포트폴리오 생성 실패 테스트")
	void createPortfolio_PhotographerNotFound() {
		// Given
		PortfolioRequest.CreateDTO request = new PortfolioRequest.CreateDTO();
		request.setPhotographerId(999L);
		request.setPortfolioTitle("새 포트폴리오");

		given(photographerRepository.findById(999L)).willReturn(Optional.empty());

		// When & Then
		IllegalArgumentException exception = assertThrows(
				IllegalArgumentException.class,
				() -> portfolioService.createPortfolio(request)
		);

		assertEquals("존재하지 않는 사진작가입니다: 999", exception.getMessage());
		verify(photographerRepository, times(1)).findById(999L);
		verify(portfolioRepository, times(0)).save(any(Portfolio.class));
	}

	@Test
	@DisplayName("포트폴리오 상세 조회 성공 테스트")
	void getPortfolioDetail_Success() {
		// Given
		Long portfolioId = 1L;
		given(portfolioRepository.findById(portfolioId)).willReturn(Optional.of(mockPortfolio));

		// When
		PortfolioResponse.DetailDTO result = portfolioService.getPortfolioDetail(portfolioId);

		// Then
		assertNotNull(result);
		assertEquals(portfolioId, result.getPortfolioId());
		assertEquals("테스트 포트폴리오", result.getPortfolioTitle());

		verify(portfolioRepository, times(1)).findById(portfolioId);
		verify(portfolioRepository, times(1)).incrementViewCount(portfolioId);
	}

	@Test
	@DisplayName("포트폴리오 삭제 성공 테스트")
	void deletePortfolio_Success() {
		// Given
		Long portfolioId = 1L;
		given(portfolioRepository.findById(portfolioId)).willReturn(Optional.of(mockPortfolio));

		// When
		portfolioService.deletePortfolio(portfolioId);

		// Then
		verify(portfolioRepository, times(1)).findById(portfolioId);
		verify(portfolioRepository, times(1)).delete(mockPortfolio);
	}
}