package com.green.chakak.chakak.photographer.service;

import com.green.chakak.chakak.account.domain.User;
import com.green.chakak.chakak.global.errors.exception.Exception400;
import com.green.chakak.chakak.global.errors.exception.Exception403;
import com.green.chakak.chakak.global.errors.exception.Exception404;
import com.green.chakak.chakak.photographer.domain.PhotographerCategory;
import com.green.chakak.chakak.photographer.domain.PhotographerMap;
import com.green.chakak.chakak.photographer.domain.PhotographerProfile;
import com.green.chakak.chakak.photographer.service.repository.PhotographerCategoryRepository;
import com.green.chakak.chakak.photographer.service.repository.PhotographerMapRepository;
import com.green.chakak.chakak.photographer.service.repository.PhotographerRepository;
import com.green.chakak.chakak.photographer.service.request.PhotographerRequest;
import com.green.chakak.chakak.photographer.service.response.PhotographerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public PhotographerResponse.SaveDTO joinAsPhotographer(User user, PhotographerRequest.SaveProfile saveDTO) {
        if (user.getUserType() != null && "photographer".equalsIgnoreCase(user.getUserType().getTypeCode())) {
            throw new Exception400("이미 포토그래퍼 회원입니다.");
        }

        if (photographerRepository.existsByUser_UserId(user.getUserId())) {
            throw new Exception400("이미 등록된 프로필이 존재합니다.");
        }

        PhotographerProfile photographerProfile = saveDTO.toEntity(user);
        PhotographerProfile saved = photographerRepository.save(photographerProfile);

        return new PhotographerResponse.SaveDTO(saved);
    }

    /**
     * 포토그래퍼 정보 수정
     */
    public PhotographerResponse.UpdateDTO updatePhotographer(Long photographerId, PhotographerRequest.UpdateProfile updateDTO, User user) {
        PhotographerProfile photographer = checkIsPhotographerAndOwner(photographerId, user);

        // 정보 업데이트
        photographer.update(updateDTO.getBusinessName(), updateDTO.getIntroduction(),
                updateDTO.getLocation(), updateDTO.getExperienceYears(), updateDTO.getStatus());

        return new PhotographerResponse.UpdateDTO(photographer);
    }

    /**
     * 포토그래퍼 상세 조회
     */
    @Transactional(readOnly = true)
    public PhotographerResponse.DetailDTO getPhotographerDetail(Long photographerId) {
        PhotographerProfile photographer = photographerRepository.findById(photographerId)
                .orElseThrow(() -> new Exception404("포토그래퍼를 찾을 수 없습니다."));

        return new PhotographerResponse.DetailDTO(photographer);
    }

    /**
     * 활성 포토그래퍼 목록 조회
     */
    @Transactional(readOnly = true)
    public List<PhotographerResponse.ListDTO> getActivePhotographers() {
        List<PhotographerProfile> photographers = photographerRepository.findByStatus("ACTIVE");

        return photographers.stream()
                .map(PhotographerResponse.ListDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * 지역별 포토그래퍼 조회
     */
    @Transactional(readOnly = true)
    public List<PhotographerResponse.ListDTO> getPhotographersByLocation(String location) {
        List<PhotographerProfile> photographers = photographerRepository.findByLocationAndStatus(location, "ACTIVE");

        return photographers.stream()
                .map(PhotographerResponse.ListDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * 상호명으로 포토그래퍼 검색
     */
    @Transactional(readOnly = true)
    public List<PhotographerResponse.ListDTO> searchByBusinessName(String businessName) {
        List<PhotographerProfile> photographers = photographerRepository.findByBusinessNameContaining(businessName);

        return photographers.stream()
                .map(PhotographerResponse.ListDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * 사용자 ID로 포토그래퍼 조회
     */
    @Transactional(readOnly = true)
    public Optional<PhotographerResponse.DetailDTO> getPhotographerByUserId(Long userId) {
        Optional<PhotographerProfile> photographer = photographerRepository.findByUser_UserId(userId);

        return photographer.map(PhotographerResponse.DetailDTO::new);
    }

    /**
     * 포토그래퍼 ID로 조회 (내부 사용)
     */
    @Transactional(readOnly = true)
    public PhotographerProfile getPhotographerById(Long photographerId) {
        return photographerRepository.findById(photographerId)
                .orElseThrow(() -> new Exception404("포토그래퍼를 찾을 수 없습니다."));
    }

    /**
     * 포토그래퍼 활성화
     */
    public PhotographerResponse.UpdateDTO activatePhotographer(Long photographerId, User user) {
        PhotographerProfile photographer = checkIsPhotographerAndOwner(photographerId, user);
        photographer.changeStatus("ACTIVE");

        return new PhotographerResponse.UpdateDTO(photographer);
    }

    /**
     * 포토그래퍼 비활성화
     */
    public PhotographerResponse.UpdateDTO deactivatePhotographer(Long photographerId, User user) {
        PhotographerProfile photographer = checkIsPhotographerAndOwner(photographerId, user);
        photographer.changeStatus("INACTIVE");

        return new PhotographerResponse.UpdateDTO(photographer);
    }

    /**
     * 포토그래퍼에 카테고리 추가
     */
    public PhotographerMap addCategoryToPhotographer(Long photographerId, Long categoryId, User user) {
        PhotographerProfile photographer = checkIsPhotographerAndOwner(photographerId, user);
        PhotographerCategory category = photographerCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new Exception404("카테고리를 찾을 수 없습니다."));

        // 중복 매핑 확인
        if (photographerMapRepository.existsByPhotographerProfileAndPhotographerCategory(photographer, category)) {
            throw new Exception400("이미 등록된 카테고리입니다.");
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
     * 포토그래퍼에서 카테고리 제거
     */
    public void removeCategoryFromPhotographer(Long photographerId, Long categoryId, User user) {
        PhotographerProfile photographer = checkIsPhotographerAndOwner(photographerId, user);
        PhotographerCategory category = photographerCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new Exception404("카테고리를 찾을 수 없습니다."));

        PhotographerMap mapping = photographerMapRepository
                .findByPhotographerProfileAndPhotographerCategory(photographer, category)
                .orElseThrow(() -> new Exception404("매핑을 찾을 수 없습니다."));

        photographerMapRepository.delete(mapping);
    }

    /**
     * 포토그래퍼 완전 삭제 (물리적 삭제)
     */
    public void removePhotographer(Long photographerId, User user) {
        PhotographerProfile photographer = checkIsPhotographerAndOwner(photographerId, user);

        // 관련 매핑 데이터 먼저 삭제
        List<PhotographerMap> mappings = photographerMapRepository.findByPhotographerProfile(photographer);
        photographerMapRepository.deleteAll(mappings);

        // 포토그래퍼 삭제
        photographerRepository.deleteById(photographerId);
    }

    /**
     * 사용자가 '포토그래퍼' 유형인지, 그리고 해당 프로필의 소유주인지 확인
     */
    private PhotographerProfile checkIsPhotographerAndOwner(Long photographerId, User user) {
        // 1. 요청을 보낸 사용자가 'photographer' 유형인지 확인
        if (user.getUserType() == null || !"photographer".equalsIgnoreCase(user.getUserType().getTypeCode())) {
            throw new Exception403("포토그래퍼 회원만 사용할 수 있는 기능입니다.");
        }

        // 2. 프로필 존재 여부 확인 및 소유권 확인
        PhotographerProfile photographer = getPhotographerById(photographerId);
        if (!photographer.getUser().getUserId().equals(user.getUserId())) {
            throw new Exception403("해당 프로필에 대한 권한이 없습니다.");
        }
        return photographer;
    }
}