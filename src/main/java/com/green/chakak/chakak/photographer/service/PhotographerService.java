package com.green.chakak.chakak.photographer.service;

import com.green.chakak.chakak.photographer.domain.PhotographerProfile;
import com.green.chakak.chakak.photographer.domain.PhotographerCategory;
import com.green.chakak.chakak.photographer.domain.PhotographerMap;
import com.green.chakak.chakak.photographer.service.repository.PhotographerRepository;
import com.green.chakak.chakak.photographer.service.repository.PhotographerCategoryRepository;
import com.green.chakak.chakak.photographer.service.repository.PhotographerMapRepository;
import com.green.chakak.chakak.account.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PhotographerService {

	private final PhotographerRepository photographerRepository;
	private final PhotographerCategoryRepository photographerCategoryRepository;
	private final PhotographerMapRepository photographerMapRepository;

	/**
	 * 포토그래퍼 가입
	 */
	public PhotographerProfile joinAsPhotographer(User user, String businessName,
												  String introduction, String location,
												  Integer experienceYears) {
		// 이미 가입된 포토그래퍼인지 확인
		if (photographerRepository.existsByUser_UserId(user.getUserId())) {
			throw new RuntimeException("이미 가입된 포토그래퍼입니다.");
		}

		PhotographerProfile photographerProfile = PhotographerProfile.builder()
				.user(user)
				.businessName(businessName)
				.introduction(introduction)
				.location(location)
				.experienceYears(experienceYears)
				.status("ACTIVE")
				.build();

		return photographerRepository.save(photographerProfile);
	}

	/**
	 * 포토그래퍼 ID로 조회
	 */
	@Transactional(readOnly = true)
	public PhotographerProfile getPhotographerById(Long photographerId) {
		return photographerRepository.findById(photographerId)
				.orElseThrow(() -> new RuntimeException("포토그래퍼를 찾을 수 없습니다."));
	}

	/**
	 * 사용자 ID로 포토그래퍼 조회
	 */
	@Transactional(readOnly = true)
	public Optional<PhotographerProfile> getPhotographerByUserId(Long userId) {
		return photographerRepository.findByUser_UserId(userId);
	}

	/**
	 * 활성 포토그래퍼 목록 조회
	 */
	@Transactional(readOnly = true)
	public List<PhotographerProfile> getActivePhotographers() {
		return photographerRepository.findByStatus("ACTIVE");
	}

	/**
	 * 지역별 포토그래퍼 조회
	 */
	@Transactional(readOnly = true)
	public List<PhotographerProfile> getPhotographersByLocation(String location) {
		return photographerRepository.findByLocationAndStatus(location, "ACTIVE");
	}

	/**
	 * 상호명으로 포토그래퍼 검색
	 */
	@Transactional(readOnly = true)
	public List<PhotographerProfile> searchByBusinessName(String businessName) {
		return photographerRepository.findByBusinessNameContaining(businessName);
	}

	/**
	 * 포토그래퍼 정보 수정
	 */
	public PhotographerProfile updatePhotographer(Long photographerId, String businessName,
												  String introduction, String location,
												  Integer experienceYears) {
		PhotographerProfile photographer = getPhotographerById(photographerId);

		// 정보 업데이트
		photographer.setBusinessName(businessName);
		photographer.setIntroduction(introduction);
		photographer.setLocation(location);
		photographer.setExperienceYears(experienceYears);

		return photographerRepository.save(photographer);
	}

	/**
	 * 포토그래퍼 활성화
	 */
	public void activatePhotographer(Long photographerId) {
		PhotographerProfile photographer = getPhotographerById(photographerId);
		photographer.setStatus("ACTIVE");
		photographerRepository.save(photographer);
	}

	/**
	 * 포토그래퍼 비활성화
	 */
	public void deactivatePhotographer(Long photographerId) {
		PhotographerProfile photographer = getPhotographerById(photographerId);
		photographer.setStatus("INACTIVE");
		photographerRepository.save(photographer);
	}

	/**
	 * 포토그래퍼에 카테고리 추가
	 */
	public PhotographerMap addCategoryToPhotographer(Long photographerId, Long categoryId) {
		PhotographerProfile photographer = getPhotographerById(photographerId);
		PhotographerCategory category = photographerCategoryRepository.findById(categoryId)
				.orElseThrow(() -> new RuntimeException("카테고리를 찾을 수 없습니다."));

		// 중복 매핑 확인
		if (photographerMapRepository.existsByPhotographerProfileAndPhotographerCategory(photographer, category)) {
			throw new RuntimeException("이미 등록된 카테고리입니다.");
		}

		PhotographerMap photographerMap = PhotographerMap.builder()
				.photographerProfile(photographer)
				.photographerCategory(category)
				.build();

		return photographerMapRepository.save(photographerMap);
	}

	/**
	 * 포토그래퍼 카테고리 목록 조회
	 */
	@Transactional(readOnly = true)
	public List<PhotographerMap> getPhotographerCategories(Long photographerId) {
		PhotographerProfile photographer = getPhotographerById(photographerId);
		return photographerMapRepository.findByPhotographerProfile(photographer);
	}

	/**
	 * 포토그래퍼 삭제 (논리적 삭제 - 상태 변경)
	 */
	public void deletePhotographer(Long photographerId) {
		PhotographerProfile photographer = getPhotographerById(photographerId);
		photographer.setStatus("INACTIVE");
		photographerRepository.save(photographer);
	}

	/**
	 * 포토그래퍼 완전 삭제 (물리적 삭제)
	 */
	public void removePhotographer(Long photographerId) {
		// 관련 매핑 데이터 먼저 삭제
		PhotographerProfile photographer = getPhotographerById(photographerId);
		List<PhotographerMap> mappings = photographerMapRepository.findByPhotographerProfile(photographer);
		photographerMapRepository.deleteAll(mappings);

		// 포토그래퍼 삭제
		photographerRepository.deleteById(photographerId);
	}

	/**
	 * 포토그래퍼에서 카테고리 제거
	 */
	public void removeCategoryFromPhotographer(Long photographerId, Long categoryId) {
		PhotographerProfile photographer = getPhotographerById(photographerId);
		PhotographerCategory category = photographerCategoryRepository.findById(categoryId)
				.orElseThrow(() -> new RuntimeException("카테고리를 찾을 수 없습니다."));

		PhotographerMap mapping = photographerMapRepository
				.findByPhotographerProfileAndPhotographerCategory(photographer, category)
				.orElseThrow(() -> new RuntimeException("매핑을 찾을 수 없습니다."));

		photographerMapRepository.delete(mapping);
	}
}