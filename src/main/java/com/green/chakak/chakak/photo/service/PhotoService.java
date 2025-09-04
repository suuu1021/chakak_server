package com.green.chakak.chakak.photo.service;

import com.green.chakak.chakak.account.domain.LoginUser;
import com.green.chakak.chakak.global.errors.exception.*;
import com.green.chakak.chakak.photo.domain.PhotoServiceCategory;
import com.green.chakak.chakak.photo.domain.PhotoServiceInfo;
import com.green.chakak.chakak.photo.domain.PhotoServiceMapping;
import com.green.chakak.chakak.photo.domain.PriceInfo;
import com.green.chakak.chakak.photo.service.repository.PhotoCategoryJpaRepository;
import com.green.chakak.chakak.photo.service.repository.PhotoMappingRepository;
import com.green.chakak.chakak.photo.service.repository.PhotoServiceJpaRepository;
import com.green.chakak.chakak.photo.service.repository.PriceInfoJpaRepository;
import com.green.chakak.chakak.photo.service.request.PhotoCategoryRequest;
import com.green.chakak.chakak.photo.service.request.PhotoMappingRequest;
import com.green.chakak.chakak.photo.service.request.PhotoServiceInfoRequest;
import com.green.chakak.chakak.photo.service.request.PriceInfoRequest;
import com.green.chakak.chakak.photo.service.response.PhotoCategoryResponse;
import com.green.chakak.chakak.photo.service.response.PhotoMappingResponse;
import com.green.chakak.chakak.photo.service.response.PhotoServiceResponse;
import com.green.chakak.chakak.photo.service.response.PriceInfoResponse;
import com.green.chakak.chakak.photographer.domain.PhotographerProfile;
import com.green.chakak.chakak.photographer.service.repository.PhotographerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class PhotoService {

    private final PhotoServiceJpaRepository photoServiceJpaRepository;
    private final PhotographerRepository photographerRepository;
    private final PhotoCategoryJpaRepository photoCategoryJpaRepository;
    private final PhotoMappingRepository photoMappingRepository;
    private final PriceInfoJpaRepository priceInfoJpaRepository;

    // ========== PhotoService 관련 메서드들 ==========
    public List<PhotoServiceResponse.PhotoServiceListDTO> serviceList(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<PhotoServiceInfo> photoServiceInfosPage;

        if (keyword != null && !keyword.trim().isEmpty()){
            photoServiceInfosPage = photoServiceJpaRepository.findAllServiceInfoByKeyword(pageable, keyword);
        } else {
            photoServiceInfosPage = photoServiceJpaRepository.findAllServiceInfo(pageable);
        }

        List<PhotoServiceResponse.PhotoServiceListDTO> serviceInfoList = new ArrayList<>();

        for (PhotoServiceInfo serviceInfo : photoServiceInfosPage.getContent()) {
            PhotoServiceResponse.PhotoServiceListDTO mainDTO = new PhotoServiceResponse.PhotoServiceListDTO(serviceInfo);
            serviceInfoList.add(mainDTO);
        }
        return serviceInfoList;
    }

    public PhotoServiceResponse.PhotoServiceDetailDTO serviceDetail(Long id, LoginUser loginUser) {

        PhotoServiceInfo photoServiceInfo = photoServiceJpaRepository.findById(id).orElseThrow(() -> new Exception404("해당 게시물이 존재하지 않습니다."));


        // 수정 권한 체크
        boolean canEdit = false;
        if (loginUser != null) {
            try {
                PhotographerProfile photographerProfile = photoServiceInfo.getPhotographerProfile();
                canEdit = photographerProfile.getUser().getUserId().equals(loginUser.getId());
            } catch (Exception e) {
                canEdit = false;
            }
        }

        // DTO 생성 및 권한 정보 설정
        PhotoServiceResponse.PhotoServiceDetailDTO dto = new PhotoServiceResponse.PhotoServiceDetailDTO(photoServiceInfo);
        dto.setCanEdit(canEdit);
        return dto;
    }

    @Transactional
    public void saveService(PhotoServiceInfoRequest.SaveDTO saveDTO, LoginUser loginUser) {

        PhotographerProfile userProfileInfo = photographerRepository.findByUser_UserId(loginUser.getId()).orElseThrow(() -> new Exception404("해당 유저가 존재하지 않습니다."));

        PhotoServiceInfo savedInfo = photoServiceJpaRepository.save(saveDTO.toEntity(userProfileInfo));

        if(savedInfo.getServiceId() == null) {
            throw new Exception500("서비스 생성이 처리되지 않았습니다.");
        }

        // 2. 가격 정보들 등록 (새로 추가)
        if (saveDTO.getPriceInfoList() != null && !saveDTO.getPriceInfoList().isEmpty()) {
            createPriceInfos(savedInfo, saveDTO.getPriceInfoList());
        }
    }

    @Transactional
    public void updateService(Long id, PhotoServiceInfoRequest.UpdateDTO reqDTO, LoginUser loginUser) {

        PhotographerProfile userProfileInfo = photographerRepository.findByUser_UserId(loginUser.getId()).orElseThrow(() -> new Exception404("해당 유저가 존재하지 않습니다."));

        if (!userProfileInfo.getPhotographerProfileId().equals(loginUser.getId())) {
            throw new Exception400("해당 서비스를 등록한 회원만 수정 가능 합니다.");
        }

        // 2. 수정할 서비스 조회
        PhotoServiceInfo photoService = photoServiceJpaRepository.findById(id)
                .orElseThrow(() -> new Exception404("해당 서비스가 존재하지 않습니다."));

        // 3. 소유자 권한 검증
        if (!photoService.getPhotographerProfile().getPhotographerProfileId().equals(userProfileInfo.getPhotographerProfileId())) {
            throw new Exception400("해당 서비스를 등록한 회원만 수정 가능합니다.");
        }

        // 4. 엔티티 수정
        photoService.updateFromDto(reqDTO);

    }

    @Transactional
    public void deleteService(Long id, LoginUser loginUser) {

        // 1. 로그인 사용자의 프로필 조회
        PhotographerProfile userProfileInfo = photographerRepository
                .findByUser_UserId(loginUser.getId())
                .orElseThrow(() -> new Exception404("해당 유저가 존재하지 않습니다."));

        // 2. 삭제할 서비스 조회
        PhotoServiceInfo photoService = photoServiceJpaRepository.findById(id)
                .orElseThrow(() -> new Exception404("해당 서비스가 존재하지 않습니다."));

        // 3. 소유자 권한 검증
        if (!photoService.getPhotographerProfile().getPhotographerProfileId().equals(userProfileInfo.getPhotographerProfileId())) {
            throw new Exception400("해당 서비스를 등록한 회원만 삭제 가능합니다.");
        }

        // 4. 삭제 실행
        photoServiceJpaRepository.deleteById(id);
    }


    // ========== PhotoServiceCategory 관련 메서드들 ==========
    // 카테고리 목록 조회
    public List<PhotoCategoryResponse.PhotoCategoryListDTO> categoryList() {
        List<PhotoServiceCategory> categories = photoCategoryJpaRepository.findAll();

        List<PhotoCategoryResponse.PhotoCategoryListDTO> categoryList = new ArrayList<>();

        for (PhotoServiceCategory category : categories) {
            PhotoCategoryResponse.PhotoCategoryListDTO categoryDTO = new PhotoCategoryResponse.PhotoCategoryListDTO(category);
            categoryList.add(categoryDTO);
        }

        return categoryList;
    }

    // 카테고리 상세 조회
    public PhotoCategoryResponse.PhotoCategoryDetailDTO categoryDetail(Long id) {
        PhotoServiceCategory category = photoCategoryJpaRepository.findById(id)
                .orElseThrow(() -> new Exception404("해당 카테고리가 존재하지 않습니다."));

        return new PhotoCategoryResponse.PhotoCategoryDetailDTO(category);
    }

    // 카테고리 생성
    @Transactional
    public void saveCategory(PhotoCategoryRequest.SaveDTO saveDTO, LoginUser loginUser) {
        // 관리자 권한 체크 (선택사항)
        // checkAdminPermission(loginUser);

        PhotoServiceCategory category = saveDTO.toEntity();
        PhotoServiceCategory savedCategory = photoCategoryJpaRepository.save(category);

        if (savedCategory.getCategoryId() == null) {
            throw new Exception500("카테고리 생성이 처리되지 않았습니다.");
        }
    }

    // 카테고리 수정
    @Transactional
    public void updateCategory(Long id, PhotoCategoryRequest.UpdateDTO updateDTO, LoginUser loginUser) {
        // 관리자 권한 체크 (선택사항)
        // checkAdminPermission(loginUser);

        PhotoServiceCategory category = photoCategoryJpaRepository.findById(id)
                .orElseThrow(() -> new Exception404("해당 카테고리가 존재하지 않습니다."));

        category.updateFromDto(updateDTO);
        photoCategoryJpaRepository.save(category);
    }

    // 카테고리 삭제
    @Transactional
    public void deleteCategory(Long id, LoginUser loginUser) {
        // 관리자 권한 체크 (선택사항)
        // checkAdminPermission(loginUser);

        PhotoServiceCategory category = photoCategoryJpaRepository.findById(id)
                .orElseThrow(() -> new Exception404("해당 카테고리가 존재하지 않습니다."));

        photoCategoryJpaRepository.deleteById(id);
    }

    // 관리자 권한 체크 메서드 (필요시 사용)
    private void checkAdminPermission(LoginUser loginUser) {
        if (!loginUser.getUserTypeName().equals("ADMIN")) { // User 엔티티에 isAdmin() 메서드가 있다고 가정
            throw new Exception403("관리자만 카테고리를 관리할 수 있습니다.");
        }
    }


    // ========== PhotoServiceMapping 관련 메서드들 ==========
    public List<PhotoMappingResponse.PhotoMappingListDTO> getMappingList(Long serviceId, Long categoryId) {

        List<PhotoServiceMapping> mappings;

        if (serviceId != null && categoryId != null) {
            // 특정 서비스와 카테고리 매핑 조회
            mappings = photoMappingRepository.findByPhotoServiceInfo_ServiceIdAndPhotoServiceCategory_CategoryId(serviceId, categoryId);
        } else if (serviceId != null) {
            // 특정 서비스의 모든 매핑 조회
            mappings = photoMappingRepository.findByPhotoServiceInfo_ServiceId(serviceId);
        } else if (categoryId != null) {
            // 특정 카테고리의 모든 매핑 조회
            mappings = photoMappingRepository.findByPhotoServiceCategory_CategoryId(categoryId);
        } else {
            // 전체 매핑 조회
            mappings = photoMappingRepository.findAll();
        }

        List<PhotoMappingResponse.PhotoMappingListDTO> mappingList = new ArrayList<>();

        for (PhotoServiceMapping mapping : mappings) {
            PhotoMappingResponse.PhotoMappingListDTO mappingDTO = new PhotoMappingResponse.PhotoMappingListDTO(mapping);
            mappingList.add(mappingDTO);
        }

        return mappingList;
    }

    // 매핑 상세 조회
    public PhotoMappingResponse.PhotoMappingDetailDTO getMappingDetail(Long id) {
        PhotoServiceMapping mapping = photoMappingRepository.findById(id)
                .orElseThrow(() -> new Exception404("해당 매핑이 존재하지 않습니다."));

        return new PhotoMappingResponse.PhotoMappingDetailDTO(mapping);
    }

    // 매핑 생성
    @Transactional
    public void createMapping(PhotoMappingRequest.SaveDTO saveDTO, LoginUser loginUser) {

        // 서비스 존재 여부 확인
        PhotoServiceInfo photoServiceInfo = photoServiceJpaRepository.findById(saveDTO.getServiceId())
                .orElseThrow(() -> new Exception404("해당 서비스가 존재하지 않습니다."));

        // 카테고리 존재 여부 확인
        PhotoServiceCategory photoServiceCategory = photoCategoryJpaRepository.findById(saveDTO.getCategoryId())
                .orElseThrow(() -> new Exception404("해당 카테고리가 존재하지 않습니다."));

        // 권한 체크 (서비스 소유자만 매핑 가능)
        if (loginUser != null) {
            PhotographerProfile photographerProfile = photoServiceInfo.getPhotographerProfile();
            if (!photographerProfile.getUser().getUserId().equals(loginUser.getId())) {
                throw new Exception403("해당 서비스의 소유자만 카테고리를 연결할 수 있습니다.");
            }
        }

        // 중복 매핑 체크
        if (photoMappingRepository.existsByPhotoServiceInfoAndPhotoServiceCategory(photoServiceInfo, photoServiceCategory)) {
            throw new Exception400("이미 연결된 서비스-카테고리입니다.");
        }

        PhotoServiceMapping mapping = saveDTO.toEntity(photoServiceInfo, photoServiceCategory);
        photoMappingRepository.save(mapping);
    }

    // 매핑 삭제
    @Transactional
    public void removeMapping(Long id, LoginUser loginUser) {
        PhotoServiceMapping mapping = photoMappingRepository.findById(id)
                .orElseThrow(() -> new Exception404("해당 매핑이 존재하지 않습니다."));

        // 권한 체크 (서비스 소유자만 삭제 가능)
        if (loginUser != null) {
            PhotographerProfile photographerProfile = mapping.getPhotoServiceInfo().getPhotographerProfile();
            if (!photographerProfile.getUser().getUserId().equals(loginUser.getId())) {
                throw new Exception403("해당 서비스의 소유자만 매핑을 삭제할 수 있습니다.");
            }
        }

        photoMappingRepository.deleteById(id);
    }

    // 특정 서비스의 카테고리들 조회
    public List<PhotoCategoryResponse.PhotoCategoryListDTO> getServiceCategories(Long serviceId) {
        List<PhotoServiceMapping> mappings = photoMappingRepository.findByPhotoServiceInfo_ServiceId(serviceId);

        List<PhotoCategoryResponse.PhotoCategoryListDTO> categories = new ArrayList<>();

        for (PhotoServiceMapping mapping : mappings) {
            PhotoCategoryResponse.PhotoCategoryListDTO categoryDTO =
                    new PhotoCategoryResponse.PhotoCategoryListDTO(mapping.getPhotoServiceCategory());
            categories.add(categoryDTO);
        }

        return categories;
    }

    // 특정 카테고리의 서비스들 조회
    public List<PhotoServiceResponse.PhotoServiceListDTO> getCategoryServices(Long categoryId) {
        List<PhotoServiceMapping> mappings = photoMappingRepository.findByPhotoServiceCategory_CategoryId(categoryId);

        List<PhotoServiceResponse.PhotoServiceListDTO> services = new ArrayList<>();

        for (PhotoServiceMapping mapping : mappings) {
            PhotoServiceResponse.PhotoServiceListDTO serviceDTO =
                    new PhotoServiceResponse.PhotoServiceListDTO(mapping.getPhotoServiceInfo());
            services.add(serviceDTO);
        }

        return services;
    }

    // priceInfo 관련 메서드
    // 가격 정보 등록 (단독으로 추가할 때)
    @Transactional
    public PriceInfoResponse.PriceInfoListDTO createPriceInfo(Long photoServiceInfoId, PriceInfoRequest.CreateDTO request) {
        PhotoServiceInfo photoServiceInfo = photoServiceJpaRepository.findById(photoServiceInfoId)
                .orElseThrow(() -> new Exception404("PhotoService를 찾을 수 없습니다: " + photoServiceInfoId));

        PriceInfo priceInfo = request.toEntity(photoServiceInfo);
        PriceInfo savedPriceInfo = priceInfoJpaRepository.save(priceInfo);
        return new PriceInfoResponse.PriceInfoListDTO(savedPriceInfo);
    }

    // 특정 PhotoService의 모든 가격 정보 조회
    public PriceInfoResponse.PriceInfosDTO getPriceInfosByPhotoServiceId(Long photoServiceInfoId) {
        // PhotoService 존재 확인
        if (!photoServiceJpaRepository.existsById(photoServiceInfoId)) {
            throw new Exception404("PhotoService를 찾을 수 없습니다: " + photoServiceInfoId);
        }

        List<PriceInfo> priceInfoList = priceInfoJpaRepository.findByPhotoServiceInfo_serviceId(photoServiceInfoId);
        return new PriceInfoResponse.PriceInfosDTO(photoServiceInfoId, priceInfoList);
    }

    // 가격 정보 단일 조회
    public PriceInfoResponse.PriceInfoDetailDTO getPriceInfo(Long priceInfoId) {
        PriceInfo priceInfo = priceInfoJpaRepository.findById(priceInfoId)
                .orElseThrow(() -> new Exception404("가격 정보를 찾을 수 없습니다: " + priceInfoId));

        return new PriceInfoResponse.PriceInfoDetailDTO(priceInfo);
    }

    // 가격 정보 수정
    @Transactional
    public PriceInfoResponse.PriceInfoListDTO updatePriceInfo(PriceInfoRequest.UpdateDTO request) {
        PriceInfo priceInfo = priceInfoJpaRepository.findById(request.getPriceInfoId())
                .orElseThrow(() -> new Exception404("가격 정보를 찾을 수 없습니다: " + request.getPriceInfoId()));

        // 필드 업데이트
        priceInfo = PriceInfo.builder()
                .priceInfoId(priceInfo.getPriceInfoId())
                .photoServiceInfo(priceInfo.getPhotoServiceInfo())
                .participantCount(request.getParticipantCount())
                .shootingDuration(request.getShootingDuration())
                .outfitChanges(request.getOutfitChanges())
                .specialEquipment(request.getSpecialEquipment())
                .isMakeupService(request.getIsMakeupService())
                .createdAt(priceInfo.getCreatedAt())
                .updatedAt(priceInfo.getUpdatedAt())
                .build();

        PriceInfo savedPriceInfo = priceInfoJpaRepository.save(priceInfo);
        return new PriceInfoResponse.PriceInfoListDTO(savedPriceInfo);
    }

    // 가격 정보 삭제
    @Transactional
    public void deletePriceInfo(Long priceInfoId) {
        if (!priceInfoJpaRepository.existsById(priceInfoId)) {
            throw new Exception404("가격 정보를 찾을 수 없습니다: " + priceInfoId);
        }
        priceInfoJpaRepository.deleteById(priceInfoId);
    }

    // PhotoService 삭제 시 연관된 가격 정보들 삭제 (PhotoService에서 호출)
    @Transactional
    public void deletePriceInfosByPhotoServiceId(Long photoServiceInfoId) {
        priceInfoJpaRepository.deleteByPhotoServiceInfo_serviceId(photoServiceInfoId);
    }

    // PhotoService 등록 시 가격 정보들 일괄 등록 (PhotoService에서 호출)
    @Transactional
    public List<PriceInfo> createPriceInfos(PhotoServiceInfo photoServiceInfo, List<PriceInfoRequest.CreateDTO> priceInfoRequests) {
        if (priceInfoRequests == null || priceInfoRequests.isEmpty()) {
            return Collections.emptyList();
        }

        List<PriceInfo> priceInfoList = priceInfoRequests.stream()
                .map(request -> request.toEntity(photoServiceInfo))
                .collect(Collectors.toList());

        return priceInfoJpaRepository.saveAll(priceInfoList);
    }
}
