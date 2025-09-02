package com.green.chakak.chakak.portfolios.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.green.chakak.chakak.portfolios.service.PortfolioService;
import com.green.chakak.chakak.portfolios.service.request.PortfolioRequest;
import com.green.chakak.chakak.portfolios.service.response.PortfolioResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PortfolioController.class)
class PortfolioControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private PortfolioService portfolioService;

	private PortfolioResponse.DetailDTO mockDetailResponse;

	@BeforeEach
	void setUp() {
		// Mock 응답 데이터 생성
		mockDetailResponse = new PortfolioResponse.DetailDTO();
		mockDetailResponse.setPortfolioId(1L);
		mockDetailResponse.setPhotographerId(1L);
		mockDetailResponse.setPhotographerName("테스트 스튜디오");
		mockDetailResponse.setPortfolioTitle("테스트 포트폴리오");
		mockDetailResponse.setPortfolioDescription("테스트 설명");
		mockDetailResponse.setViewCount(0);
		mockDetailResponse.setLikeCount(0);
		mockDetailResponse.setIsPublic(true);
		mockDetailResponse.setCreatedAt(LocalDateTime.now());
		mockDetailResponse.setImages(new ArrayList<>());
		mockDetailResponse.setCategories(new ArrayList<>());
	}

	@Test
	@DisplayName("포트폴리오 생성 API 테스트")
	void createPortfolio_Success() throws Exception {
		// Given
		PortfolioRequest.CreateDTO request = new PortfolioRequest.CreateDTO();
		request.setPhotographerId(1L);
		request.setPortfolioTitle("새 포트폴리오");
		request.setPortfolioDescription("새 포트폴리오 설명");
		request.setIsPublic(true);

		given(portfolioService.createPortfolio(any(PortfolioRequest.CreateDTO.class)))
				.willReturn(mockDetailResponse);

		// When & Then
		mockMvc.perform(post("/api/portfolios")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(header().string("Location", "/api/portfolios/1"))
				.andExpect(jsonPath("$.body.portfolioId").value(1L))
				.andExpect(jsonPath("$.body.portfolioTitle").value("테스트 포트폴리오"))
				.andExpect(jsonPath("$.body.photographerName").value("테스트 스튜디오"));

		verify(portfolioService, times(1)).createPortfolio(any(PortfolioRequest.CreateDTO.class));
	}

	@Test
	@DisplayName("포트폴리오 상세 조회 API 테스트")
	void getPortfolioDetail_Success() throws Exception {
		// Given
		Long portfolioId = 1L;
		given(portfolioService.getPortfolioDetail(portfolioId)).willReturn(mockDetailResponse);

		// When & Then
		mockMvc.perform(get("/api/portfolios/{portfolioId}", portfolioId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.body.portfolioId").value(1L))
				.andExpect(jsonPath("$.body.portfolioTitle").value("테스트 포트폴리오"))
				.andExpect(jsonPath("$.body.photographerId").value(1L));

		verify(portfolioService, times(1)).getPortfolioDetail(portfolioId);
	}

	@Test
	@DisplayName("포트폴리오 삭제 API 테스트")
	void deletePortfolio_Success() throws Exception {
		// Given
		Long portfolioId = 1L;

		// When & Then
		mockMvc.perform(delete("/api/portfolios/{portfolioId}", portfolioId))
				.andExpect(status().isNoContent());

		verify(portfolioService, times(1)).deletePortfolio(portfolioId);
	}

	@Test
	@DisplayName("잘못된 JSON으로 포트폴리오 생성 실패 테스트")
	void createPortfolio_InvalidJson() throws Exception {
		// Given - 제목이 없는 잘못된 요청
		PortfolioRequest.CreateDTO request = new PortfolioRequest.CreateDTO();
		request.setPhotographerId(1L);
		// portfolioTitle 누락 (필수 필드)

		// When & Then
		mockMvc.perform(post("/api/portfolios")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest());

		verify(portfolioService, times(0)).createPortfolio(any(PortfolioRequest.CreateDTO.class));
	}

	@Test
	@DisplayName("좋아요 증가 API 테스트")
	void likePortfolio_Success() throws Exception {
		// Given
		Long portfolioId = 1L;

		// When & Then
		mockMvc.perform(post("/api/portfolios/{portfolioId}/like", portfolioId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.body").value("좋아요가 추가되었습니다."));

		verify(portfolioService, times(1)).increaseLike(portfolioId);
	}
}