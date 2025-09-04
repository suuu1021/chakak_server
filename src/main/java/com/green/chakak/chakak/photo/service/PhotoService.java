package com.green.chakak.chakak.photo.service;

import com.green.chakak.chakak.account.user.LoginUser;
import com.green.chakak.chakak.account.user.User;
import com.green.chakak.chakak.global.errors.exception.*;
import com.green.chakak.chakak.photo.domain.PhotoServiceCategory;
import com.green.chakak.chakak.photo.domain.PhotoServiceInfo;
import com.green.chakak.chakak.photo.service.repository.PhotoCategoryJpaRepository;
import com.green.chakak.chakak.photo.service.repository.PhotoServiceJpaRepository;
import com.green.chakak.chakak.photo.service.request.PhotoCategoryRequest;
import com.green.chakak.chakak.photo.service.request.PhotoServiceInfoRequest;
import com.green.chakak.chakak.photo.service.response.PhotoCategoryResponse;
import com.green.chakak.chakak.photo.service.response.PhotoServiceResponse;
import com.green.chakak.chakak.photographer.domain.PhotographerProfile;
import com.green.chakak.chakak.photographer.service.repository.PhotographerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class PhotoService {

    private final PhotoServiceJpaRepository photoServiceJpaRepository;
    private final PhotographerRepository photographerRepository;
    private final PhotoCategoryJpaRepository photoCategoryJpaRepository;

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
}
