package com.green.chakak.chakak.photographer.service;

import com.green.chakak.chakak._global.errors.exception.Exception400;
import com.green.chakak.chakak._global.errors.exception.Exception403;
import com.green.chakak.chakak._global.errors.exception.Exception404;
import com.green.chakak.chakak.account.domain.LoginUser;
import com.green.chakak.chakak.account.domain.User;
import com.green.chakak.chakak.account.domain.UserType;
import com.green.chakak.chakak.account.service.repository.UserJpaRepository;
import com.green.chakak.chakak.account.service.repository.UserTypeRepository;
import com.green.chakak.chakak.photographer.domain.PhotographerCategory;
import com.green.chakak.chakak.photographer.domain.PhotographerMap;
import com.green.chakak.chakak.photographer.domain.PhotographerProfile;
import com.green.chakak.chakak.photographer.service.repository.PhotographerCategoryRepository;
import com.green.chakak.chakak.photographer.service.request.PhotographerCategoryRequest;
import com.green.chakak.chakak.photographer.service.repository.PhotographerMapRepository;
import com.green.chakak.chakak.photographer.service.repository.PhotographerRepository;
import com.green.chakak.chakak.photographer.service.request.PhotographerRequest;
import com.green.chakak.chakak.photographer.service.response.PhotographerResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PhotographerService {

    private final UserJpaRepository userJpaRepository;
    private final PhotographerRepository photographerRepository;
    private final PhotographerCategoryRepository photographerCategoryRepository;
    private final PhotographerMapRepository photographerMapRepository;

    /**
     * 포토그래퍼 프로필 저장
     */
    @Transactional
    public PhotographerResponse.SaveDTO createProfile(PhotographerRequest.SaveProfile saveDTO) {

        // 1. dto의 UserId로 회원 존재여부 조회
        User searchUser = userJpaRepository.findById(saveDTO.getUserId()).orElseThrow(() -> new Exception404("해당 유저가 존재하지 않습니다."));

        // 1. 요청 사용자가 'photographer' 타입인지 확인 (권한 검사)
        if (!"photographer".equalsIgnoreCase(searchUser.getUserType().getTypeCode())) {
            throw new Exception403("포토그래퍼 회원만 프로필을 등록할 수 있습니다.");
        }

        // 2. 이미 프로필이 등록되어 있는지 확인 (중복 생성 방지)
        if (photographerRepository.existsByUser_UserId(searchUser.getUserId())) {
            throw new Exception400("이미 등록된 프로필이 존재합니다.");
        }

        // 3. 포토그래퍼 프로필 생성 및 저장
        PhotographerProfile photographerProfile = saveDTO.toEntity(searchUser);
        PhotographerProfile saved = photographerRepository.save(photographerProfile);
        List<PhotographerMap> maps = new ArrayList<>();

        // 4. 카테고리 매핑 생성
        if (saveDTO.getCategoryIds() != null && !saveDTO.getCategoryIds().isEmpty()) {
            createCategoryMappings(saved, saveDTO.getCategoryIds());
            maps = photographerMapRepository.findByPhotographerProfileWithCategory(saved);
        }
        return new PhotographerResponse.SaveDTO(saved, maps);
    }


    // 포토그래퍼 프로필과 카테고리 목록을 받아 매핑 데이터를 생성하고 저장
    private void createCategoryMappings(PhotographerProfile profile, List<Long> categoryIds) {
        // 1. 요청된 ID에 해당하는 카테고리들을 한 번의 쿼리로 모두 조회
        List<Long> distinctIds = categoryIds.stream().distinct().collect(Collectors.toList());
        List<PhotographerCategory> categories = photographerCategoryRepository.findAllById(distinctIds);

        if (categories.size() != distinctIds.size()) {
            // 요청된 ID 개수와 실제 조회된 카테고리 개수가 다를 경우, 존재하지 않는 ID를 찾아서 에러 메시지에 포함
            List<Long> foundIds = categories.stream()
                    .map(PhotographerCategory::getCategoryId)
                    .collect(Collectors.toList());
            List<Long> notFoundIds = distinctIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .collect(Collectors.toList());
            throw new Exception404("다음 ID에 해당하는 카테고리를 찾을 수 없습니다: " + notFoundIds);
        }

        // 2. 조회된 카테고리들로 매핑 객체 리스트를 생성
        List<PhotographerMap> mappings = categories.stream()
                .map(category -> PhotographerMap.builder().photographerProfile(profile).photographerCategory(category).build())
                .collect(Collectors.toList());

        // 3. 생성된 매핑들을 한 번의 쿼리로 모두 저장
        photographerMapRepository.saveAll(mappings);
    }

    /**
     * 포토그래퍼 프로필 수정
     */
    @Transactional
    public PhotographerResponse.UpdateDTO updateProfile(Long photographerId, PhotographerRequest.UpdateProfile updateDTO, LoginUser loginUser) {
        PhotographerProfile photographer = checkIsPhotographerAndOwner(photographerId, loginUser);

        // 1. 프로필 기본 정보 업데이트
        photographer.update(updateDTO);

        // 2. 카테고리 정보 업데이트
        // categoryIds 필드가 요청에 포함된 경우에만 카테고리 업데이트 수행
        if (updateDTO.getCategoryIds() != null) {
            // 기존 매핑을 모두 삭제
            List<PhotographerMap> existingMappings = photographerMapRepository.findByPhotographerProfile(photographer);
            photographerMapRepository.deleteAll(existingMappings);

            // 새로운 카테고리 목록이 비어있지 않다면, 다시 생성
            if (!updateDTO.getCategoryIds().isEmpty()) {
                createCategoryMappings(photographer, updateDTO.getCategoryIds());
            }
        }

        // 3. 업데이트된 카테고리 정보를 포함하여 DTO 반환
        List<PhotographerMap> updatedMaps = photographerMapRepository.findByPhotographerProfileWithCategory(photographer);
        return new PhotographerResponse.UpdateDTO(photographer, updatedMaps);
    }

    /**
     * 포토그래퍼 상세 조회
     */
    @Transactional(readOnly = true)
    public PhotographerResponse.DetailDTO getProfileDetail(Long photographerId) {
        // 1. 포토그래퍼 프로필 조회
        PhotographerProfile photographer = getPhotographerById(photographerId);

        // 2. N+1 문제 없이 연관된 카테고리 목록을 함께 조회
        List<PhotographerMap> maps = photographerMapRepository.findByPhotographerProfileWithCategory(photographer);
        return new PhotographerResponse.DetailDTO(photographer, maps);
    }

    /**
     * 활성 포토그래퍼 목록 조회
     */
    @Transactional(readOnly = true)
    public List<PhotographerResponse.ListDTO> getActiveProfile() {
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
    @Transactional
    public PhotographerResponse.UpdateDTO activatePhotographer(Long photographerId, LoginUser loginUser) {
        PhotographerProfile photographer = checkIsPhotographerAndOwner(photographerId, loginUser);
        photographer.changeStatus("ACTIVE");

        List<PhotographerMap> maps = photographerMapRepository.findByPhotographerProfileWithCategory(photographer);
        return new PhotographerResponse.UpdateDTO(photographer, maps);
    }

    /**
     * 포토그래퍼 비활성화
     */
    @Transactional
    public PhotographerResponse.UpdateDTO deactivatePhotographer(Long photographerId, LoginUser loginUser) {
        PhotographerProfile photographer = checkIsPhotographerAndOwner(photographerId, loginUser);
        photographer.changeStatus("INACTIVE");

        List<PhotographerMap> maps = photographerMapRepository.findByPhotographerProfileWithCategory(photographer);
        return new PhotographerResponse.UpdateDTO(photographer, maps);
    }

    /**
     * 포토그래퍼에 카테고리 추가
     */
    @Transactional
    public PhotographerResponse.mapDTO addCategoryToPhotographer(Long photographerId, PhotographerCategoryRequest.AddCategoryToPhotographer request, LoginUser loginUser) {
        PhotographerProfile photographer = checkIsPhotographerAndOwner(photographerId, loginUser);
        PhotographerCategory category = photographerCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new Exception404("카테고리를 찾을 수 없습니다."));

        // 중복 매핑 확인
        if (photographerMapRepository.existsByPhotographerProfileAndPhotographerCategory(photographer, category)) {
            throw new Exception400("이미 등록된 카테고리입니다.");
        }

        PhotographerMap photographerMap = PhotographerMap.builder()
                .photographerProfile(photographer)
                .photographerCategory(category)
                .build();

        PhotographerMap savedMap = photographerMapRepository.save(photographerMap);
        return new PhotographerResponse.mapDTO(savedMap);
    }

    /**
     * 포토그래퍼 카테고리 목록 조회
     */
    @Transactional(readOnly = true)
    public List<PhotographerResponse.mapDTO> getPhotographerCategories(Long photographerId) {
        PhotographerProfile photographer = getPhotographerById(photographerId);
        List<PhotographerMap> maps = photographerMapRepository.findByPhotographerProfileWithCategory(photographer);
        return maps.stream().map(PhotographerResponse.mapDTO::new).collect(Collectors.toList());
    }

    /**
     * 포토그래퍼에서 카테고리 제거
     */
    @Transactional
    public void removeCategoryFromPhotographer(Long photographerId, Long categoryId, LoginUser loginUser) {
        PhotographerProfile photographer = checkIsPhotographerAndOwner(photographerId, loginUser);
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
    @Transactional
    public void removePhotographer(Long photographerId, LoginUser loginUser) {
        PhotographerProfile photographer = checkIsPhotographerAndOwner(photographerId, loginUser);

        // 관련 매핑 데이터 먼저 삭제
        List<PhotographerMap> mappings = photographerMapRepository.findByPhotographerProfile(photographer);
        photographerMapRepository.deleteAll(mappings);

        // 포토그래퍼 삭제
        photographerRepository.deleteById(photographerId);
    }

    /**
     * 사용자가 '포토그래퍼' 유형인지, 그리고 해당 프로필의 소유주인지 확인
     */
    private PhotographerProfile checkIsPhotographerAndOwner(Long photographerId, LoginUser loginUser) {
        // 1. 요청을 보낸 사용자가 'photographer' 유형인지 확인
        if (loginUser.getUserTypeName() == null || !"photographer".equalsIgnoreCase(loginUser.getUserTypeName())) {
            throw new Exception403("포토그래퍼 회원만 사용할 수 있는 기능입니다.");
        }

        // 2. 프로필 존재 여부 확인 및 소유권 확인
        PhotographerProfile photographer = getPhotographerById(photographerId);
        if (!photographer.getUser().getUserId().equals(loginUser.getId())) {
            throw new Exception403("해당 프로필에 대한 권한이 없습니다.");
        }
        return photographer;
    }

    /**
     * 현재 로그인한 사용자의 포토그래퍼 프로필 조회
     */
    @Transactional(readOnly = true)
    public PhotographerResponse.DetailDTO getMyProfile(LoginUser loginUser) {
        // 1. 요청을 보낸 사용자가 'photographer' 유형인지 확인
        if (loginUser.getUserTypeName() == null || !"photographer".equalsIgnoreCase(loginUser.getUserTypeName())) {
            throw new Exception403("포토그래퍼 회원만 사용할 수 있는 기능입니다.");
        }

        // 2. 현재 로그인한 사용자의 포토그래퍼 프로필 조회
        PhotographerProfile photographer = photographerRepository.findByUser_UserId(loginUser.getId())
                .orElseThrow(() -> new Exception404("등록된 포토그래퍼 프로필이 없습니다."));

        // 3. 카테고리 정보와 함께 반환
        List<PhotographerMap> maps = photographerMapRepository.findByPhotographerProfileWithCategory(photographer);
        return new PhotographerResponse.DetailDTO(photographer, maps);
    }
}