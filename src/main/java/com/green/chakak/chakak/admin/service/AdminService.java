package com.green.chakak.chakak.admin.service;

import com.green.chakak.chakak._global.errors.exception.*;
import com.green.chakak.chakak._global.utils.Define;
import com.green.chakak.chakak._global.utils.JwtUtil;
import com.green.chakak.chakak.account.domain.LoginUser;
import com.green.chakak.chakak.account.domain.User;
import com.green.chakak.chakak.account.domain.UserProfile;
import com.green.chakak.chakak.account.domain.UserType;
import com.green.chakak.chakak.account.service.repository.UserJpaRepository;
import com.green.chakak.chakak.account.service.repository.UserProfileJpaRepository;
import com.green.chakak.chakak.account.service.repository.UserTypeRepository;
import com.green.chakak.chakak.account.service.request.UserProfileRequest;
import com.green.chakak.chakak.account.service.request.UserRequest;
import com.green.chakak.chakak.account.service.request.UserTypeRequest;
import com.green.chakak.chakak.account.service.response.UserProfileResponse;
import com.green.chakak.chakak.account.service.response.UserResponse;
import com.green.chakak.chakak.admin.domain.Admin;
import com.green.chakak.chakak.admin.domain.LoginAdmin;
import com.green.chakak.chakak.admin.service.repository.AdminJpaRepository;
import com.green.chakak.chakak.admin.service.request.AdminRequest;
import com.green.chakak.chakak.admin.service.response.AdminResponse;
import com.green.chakak.chakak.booking.domain.BookingCancelInfo;
import com.green.chakak.chakak.booking.domain.BookingInfo;
import com.green.chakak.chakak.booking.domain.BookingStatus;
import com.green.chakak.chakak.booking.service.repository.BookingCancelInfoJpaRepository;
import com.green.chakak.chakak.booking.service.repository.BookingInfoJpaRepository;
import com.green.chakak.chakak.booking.service.request.BookingInfoRequest;
import com.green.chakak.chakak.booking.service.response.BookingCancelInfoResponse;
import com.green.chakak.chakak.booking.service.response.BookingInfoResponse;
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
import com.green.chakak.chakak.photographer.domain.PhotographerCategory;
import com.green.chakak.chakak.photographer.domain.PhotographerMap;
import com.green.chakak.chakak.photographer.domain.PhotographerProfile;
import com.green.chakak.chakak.photographer.service.repository.PhotographerCategoryRepository;
import com.green.chakak.chakak.photographer.service.repository.PhotographerMapRepository;
import com.green.chakak.chakak.photographer.service.repository.PhotographerRepository;
import com.green.chakak.chakak.photographer.service.request.PhotographerCategoryRequest;
import com.green.chakak.chakak.photographer.service.request.PhotographerRequest;
import com.green.chakak.chakak.photographer.service.response.PhotographerResponse;
import com.green.chakak.chakak.portfolios.domain.Portfolio;
import com.green.chakak.chakak.portfolios.domain.PortfolioCategory;
import com.green.chakak.chakak.portfolios.domain.PortfolioImage;
import com.green.chakak.chakak.portfolios.domain.PortfolioMap;
import com.green.chakak.chakak.portfolios.service.repository.PortfolioCategoryJpaRepository;
import com.green.chakak.chakak.portfolios.service.repository.PortfolioImageJpaRepository;
import com.green.chakak.chakak.portfolios.service.repository.PortfolioJpaRepository;
import com.green.chakak.chakak.portfolios.service.repository.PortfolioMapJpaRepository;
import com.green.chakak.chakak.portfolios.service.request.PortfolioRequest;
import com.green.chakak.chakak.portfolios.service.response.PortfolioCategoryResponse;
import com.green.chakak.chakak.portfolios.service.response.PortfolioResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestAttribute;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final AdminJpaRepository adminJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final UserTypeRepository userTypeRepository;
    private final UserProfileJpaRepository userProfileJpaRepository;
    private final PhotographerRepository photographerRepository;
    private final PortfolioJpaRepository portfolioRepository;
    private final PortfolioMapJpaRepository portfolioMapRepository;
    private final PortfolioImageJpaRepository portfolioImageRepository;
    private final PhotographerMapRepository photographerMapRepository;
    private final PhotographerCategoryRepository photographerCategoryRepository;
    private final PhotoServiceJpaRepository photoServiceJpaRepository;
    private final PriceInfoJpaRepository priceInfoJpaRepository;
    private final PhotoMappingRepository photoMappingRepository;
    private final PhotoCategoryJpaRepository photoCategoryJpaRepository;
    private final PortfolioCategoryJpaRepository categoryRepository;
    private final BookingCancelInfoJpaRepository bookingCancelInfoJpaRepository;
    private final BookingInfoJpaRepository bookingInfoJpaRepository;


    // 관리자 로그인 기능
    public AdminResponse.AdminLoginDto login(AdminRequest.LoginRequest req, UserType userType) {


        Admin admin = adminJpaRepository.findByNameAndAdminPassword(req.getName(), req.getPassword())
                .orElseThrow(() -> new Exception401("이름 또는 비밀번호가 올바르지 않습니다."));


        LoginAdmin loginAdmin = LoginAdmin.fromEntity(admin);
        String token = JwtUtil.createForAdmin(loginAdmin);

        return AdminResponse.AdminLoginDto.adminSet(admin, token, admin.getAdminName());
    }


    //  전체 유저 조회
    public Page<AdminResponse.AdminUserListDto> findAllUsers(LoginAdmin loginAdmin, Pageable pageable) {
        return userJpaRepository.findAll(pageable)
                .map(AdminResponse.AdminUserListDto::from);
    }

    // 단건 조회
    public AdminResponse.AdminUserDetailDto getUserDetail(Long userId) {
        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new Exception400("유저가 존재하지 않습니다."));
        return AdminResponse.AdminUserDetailDto.from(user);
    }

    // 포토그래퍼만 조회
    public List<AdminResponse.UserListResponseDto> getPhotographersOnly() {
        UserType photographerType = userTypeRepository.findByTypeCode("photographer")
                .orElseThrow(() -> new Exception400("포토그래퍼 타입을 찾을 수 없습니다."));

        return userJpaRepository.findAll()  // 전체 조회
                .stream()
                .filter(user -> user.getUserType().getTypeCode().equals("photographer"))
                .map(AdminResponse.UserListResponseDto::from)
                .collect(Collectors.toList());
    }

    // 일반 사용자만 조회
    public List<AdminResponse.UserListResponseDto> findUserOnly() {
        UserType userType = userTypeRepository.findByTypeCode("user")
                .orElseThrow(() -> new Exception400("유저 타입을 찾을 수 없습니다."));

        return userJpaRepository.findAll()  // 전체 조회
                .stream()
                .filter(user -> user.getUserType().getTypeCode().equals("user"))
                .map(AdminResponse.UserListResponseDto::from)
                .collect(Collectors.toList());
    }

    // 유저 정지
    @Transactional
    public void suspendUserByAdmin(Long userId, LoginAdmin loginAdmin) {
        // 관리자 존재 여부 확인
        Admin admin = adminJpaRepository.findById(loginAdmin.getAdminId()).orElseThrow(() -> new Exception404("존재하지 않는 관리자 입니다."));

        // 회원 존재여부 확인
        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new Exception404("존재하지 않는 유저입니다."));
        try {
            user.setStatus(User.UserStatus.SUSPENDED);

        } catch (Exception e) {
            if (user.getStatus() == User.UserStatus.SUSPENDED)
                throw new Exception400("이미 처리되어 있습니다");
        }


    }

    // 유저 활성화
    @Transactional
    public void ActiveUserByAdmin(Long userId, LoginAdmin loginAdmin) {
        // 관리자 존재 여부 확인
        Admin admin = adminJpaRepository.findById(loginAdmin.getAdminId()).orElseThrow(() -> new Exception404("존재하지 않는 관리자 입니다."));

        // 회원 존재여부 확인
        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new Exception404("존재하지 않는 유저입니다."));
        try {
            user.setStatus(User.UserStatus.ACTIVE);

        } catch (Exception e) {
            if (user.getStatus() == User.UserStatus.ACTIVE) {
                throw new Exception400("이미 처리되어 있습니다");
            }

        }

    }

    // 유저 삭제

    @Transactional
    public void deleteUserByAdmin(Long id, LoginAdmin loginAdmin) {

        if (!userJpaRepository.existsById(id)) {
            throw new Exception404("존재하지 않는 사용자입니다.");
        }
        userJpaRepository.deleteById(id);
    }


    // 유저 수정

    @Transactional
    public UserResponse.UpdateResponse updateUserByAdmin(Long userId, UserRequest.UpdateRequest req, LoginAdmin loginAdmin) {

        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new Exception404("존재하지 않는 사용자입니다."));
        if (req.getEmail() != null && !req.getEmail().isEmpty()) {
            if (!user.getEmail().equals(req.getEmail()) && userJpaRepository.existsByEmail(req.getEmail())) {
                throw new Exception400("이미 사용 중인 이메일입니다.");
            }
            user.changeEmail(req.getEmail());
        }
        if (req.getPassword() != null && !req.getPassword().isEmpty()) {
            user.setPassword(req.getPassword());
        }
        User updatedUser = userJpaRepository.save(user);
        return UserResponse.UpdateResponse.from(updatedUser);
    }

    // 유저 프로필 생성
    @Transactional
    public UserProfileResponse.DetailDTO createdProfileByAdmin(Long userId, UserProfileRequest.CreateDTO createDTO,
                                                               LoginAdmin loginAdmin){
        if (loginAdmin == null) {
            throw new Exception403("관리자 권한이 필요합니다.");
        }

        User user = userJpaRepository.findById(createDTO.getUserInfoId()).orElseThrow(
                () -> new Exception404("존재하지 않는 유저입니다.")
        );

        userProfileJpaRepository.findByUserId(userId).ifPresent(up -> {
            throw new Exception400("이미 프로필이 존재합니다.");
        });

        userProfileJpaRepository.findByNickName(createDTO.getNickName()).ifPresent(up -> {
            throw new Exception400("이미 사용중인 닉네임입니다.");
        });

        UserProfile savedProfile = userProfileJpaRepository.save(createDTO.toEntity(user));
        return new UserProfileResponse.DetailDTO(savedProfile);
    }

    // 유저 프로필 수정

    @Transactional
    public UserProfileResponse.UpdateDTO updateProfileByAdmin(Long userId, UserProfileRequest.UpdateDTO updateDTO) {
        UserProfile userProfile = userProfileJpaRepository.findByUserId(userId)
                .orElseThrow(() -> new Exception404("수정할 프로필이 존재하지 않습니다."));

        userProfileJpaRepository.findByNickName(updateDTO.getNickName()).ifPresent(userProfile1 -> {
            if (!userProfile1.getUserProfileId().equals(userProfile.getUserProfileId()))
                throw new Exception400("이미 사용중인 닉네임입니다.");
        });

        userProfile.update(updateDTO);
        return new UserProfileResponse.UpdateDTO(userProfile);
    }



    // UserType 관련

    // 수정
    @Transactional
    public UserType updateUserTypeByAdmin(Long id, UserTypeRequest request) {
        UserType userType = userTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 회원 유형이 존재하지 않습니다."));

        userType.setTypeName(request.getTypeName());
        userType.setTypeCode(request.getTypeCode());
        userType.setUpdatedAt(LocalDateTime.now());

        return userTypeRepository.save(userType);

    }

    // 삭제
    public void deleteUserTypeByAdmin(Long id) {
        userTypeRepository.deleteById(id);
    }


    // 포토그래퍼 카테고리 관련

    @Transactional(readOnly = true)
    public List<PhotographerResponse.CategoryDTO> getAllCategoriesByAdmin() {
        return photographerCategoryRepository.findAll().stream()
                .map(PhotographerResponse.CategoryDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * 카테고리 ID로 조회
     */
    @Transactional(readOnly = true)
    public PhotographerResponse.CategoryDTO getCategoryByIdByAdmin(Long categoryId) {
        return photographerCategoryRepository.findById(categoryId)
                .map(PhotographerResponse.CategoryDTO::new)
                .orElseThrow(() -> new Exception404("카테고리를 찾을 수 없습니다."));
    }

    /**
     * 카테고리명으로 조회
     */
    @Transactional(readOnly = true)
    public PhotographerResponse.CategoryDTO getCategoryByNameByAdmin(String categoryName) {
        return photographerCategoryRepository.findByCategoryName(categoryName)
                .map(PhotographerResponse.CategoryDTO::new)
                .orElseThrow(() -> new Exception404("카테고리를 찾을 수 없습니다."));
    }

    /**
     * 특정 카테고리에 속한 포토그래퍼들 조회
     */
    @Transactional(readOnly = true)
    public List<PhotographerResponse.ListDTO> getPhotographersByCategoryByAdmin(Long categoryId) {
        PhotographerCategory category = photographerCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new Exception404("카테고리를 찾을 수 없습니다."));

        List<PhotographerMap> maps = photographerMapRepository.findByPhotographerCategoryWithPhotographer(category);

        return maps.stream()
                .map(photographerMap -> new PhotographerResponse.ListDTO(photographerMap.getPhotographerProfile()))
                .collect(Collectors.toList());
    }

    /**
     * 카테고리명 중복 확인
     */
    @Transactional(readOnly = true)
    public boolean existsByCategoryName(String categoryName) {
        return photographerCategoryRepository.existsByCategoryName(categoryName);
    }




    // 포토그래퍼 관련

    @Transactional
    public PhotographerResponse.SaveDTO createProfileByAdmin(PhotographerRequest.SaveProfile saveDTO) {

        // 1. dto의 UserId로 회원 존재여부 조회
        User searchUser = userJpaRepository.findById(saveDTO.getUserId()).orElseThrow(() -> new Exception404("해당 유저가 존재하지 않습니다."));

        // 2. 이미 프로필이 등록되어 있는지 확인 (중복 생성 방지)
        if (photographerRepository.existsByUser_UserId(searchUser.getUserId())) {
            throw new Exception400("이미 등록된 프로필이 존재합니다.");
        }

        // 3. 포토그래퍼 프로필 생성 및 저장
        if (photographerRepository.existsByUser_UserId(searchUser.getUserId()))
            throw new Exception400("이미 등록된 프로필이 존재합니다.");
        PhotographerProfile photographerProfile = saveDTO.toEntity(searchUser);
        PhotographerProfile saved = photographerRepository.save(photographerProfile);
        List<PhotographerMap> maps = new ArrayList<>();

        // 4. 카테고리 매핑 생성
        if (saveDTO.getCategoryIds() != null && !saveDTO.getCategoryIds().isEmpty()) {
            createCategoryMappingsByAdmin(saved, saveDTO.getCategoryIds());
            maps = photographerMapRepository.findByPhotographerProfileWithCategory(saved);
        }
        return new PhotographerResponse.SaveDTO(saved, maps);
    }


    // 포토그래퍼 프로필과 카테고리 목록을 받아 매핑 데이터를 생성하고 저장
    @Transactional
    private void createCategoryMappingsByAdmin(PhotographerProfile profile, List<Long> categoryIds) {
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
    public PhotographerResponse.UpdateDTO updateProfileByAdmin(Long photographerId, PhotographerRequest.UpdateProfile updateDTO) {
        PhotographerProfile photographer = getPhotographerById(photographerId);

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
                createCategoryMappingsByAdmin(photographer, updateDTO.getCategoryIds());
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
    public PhotographerResponse.DetailDTO getProfileDetailByAdmin(Long photographerId) {
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
    public List<PhotographerResponse.ListDTO> getActiveProfileByAdmin() {
        List<PhotographerProfile> photographers = photographerRepository.findByStatus("ACTIVE");

        return photographers.stream()
                .map(PhotographerResponse.ListDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * 지역별 포토그래퍼 조회
     */
    @Transactional(readOnly = true)
    public List<PhotographerResponse.ListDTO> getPhotographersByLocationbyAdmin(String location) {
        List<PhotographerProfile> photographers = photographerRepository.findByLocationAndStatus(location, "ACTIVE");

        return photographers.stream()
                .map(PhotographerResponse.ListDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * 상호명으로 포토그래퍼 검색
     */
    @Transactional(readOnly = true)
    public List<PhotographerResponse.ListDTO> searchByBusinessNameByAdmin(String businessName) {
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
    /**
     * 포토그래퍼 활성화 (관리자용)
     */
    @Transactional
    public PhotographerResponse.UpdateDTO activatePhotographerByAdmin(Long photographerId, LoginAdmin loginAdmin) {
        // 1. ID로 프로필을 직접 조회 (소유권 체크 없음)
        PhotographerProfile photographer = getPhotographerById(photographerId);

        // 2. 상태 변경
        if(!photographer.getStatus().equals("ACTIVE")) {
            photographer.changeStatus("ACTIVE");
        } else {
            throw new Exception400("이미 활성화된 포토그래퍼입니다.");
        }


        // 3. 결과 반환
        List<PhotographerMap> maps = photographerMapRepository.findByPhotographerProfileWithCategory(photographer);
        return new PhotographerResponse.UpdateDTO(photographer, maps);
    }

    /**
     * 포토그래퍼 비활성화
     */
    /**
     * 포토그래퍼 비활성화 (관리자용)
     */
    @Transactional
    public PhotographerResponse.UpdateDTO deactivatePhotographerByAdmin(Long photographerId, LoginAdmin loginAdmin) {
        // ID로 프로필을 직접 조회 (소유권 체크 없음)
        PhotographerProfile photographer = getPhotographerById(photographerId);

        // 상태 변경
        if(!photographer.getStatus().equals("INACTIVE")) {
            photographer.changeStatus("INACTIVE");
        } else {
            throw new Exception400("이미 비활성화된 포토그래퍼입니다.");
        }

        // 결과 반환
        List<PhotographerMap> maps = photographerMapRepository.findByPhotographerProfileWithCategory(photographer);
        return new PhotographerResponse.UpdateDTO(photographer, maps);
    }



    /**
     * 포토그래퍼에 카테고리 추가
     */
    /**
     * 포토그래퍼에 카테고리 추가 (관리자용)
     */
    @Transactional
    public PhotographerResponse.mapDTO addCategoryToPhotographerByAdmin(Long photographerId, PhotographerCategoryRequest.AddCategoryToPhotographer request, LoginAdmin loginAdmin) {
        // 1. ID로 프로필을 직접 조회 (소유권 체크 없음)
        PhotographerProfile photographer = getPhotographerById(photographerId);

        // 2. 추가할 카테고리 조회
        PhotographerCategory category = photographerCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new Exception404("카테고리를 찾을 수 없습니다."));

        // 3. 중복 매핑 확인 (데이터 무결성을 위해 유지)
        if (photographerMapRepository.existsByPhotographerProfileAndPhotographerCategory(photographer, category)) {
            throw new Exception400("이미 등록된 카테고리입니다.");
        }

        // 4. 매핑 객체 생성 및 저장
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
    public List<PhotographerResponse.mapDTO> getPhotographerCategoriesByAdmin(Long photographerId) {
        PhotographerProfile photographer = getPhotographerById(photographerId);
        List<PhotographerMap> maps = photographerMapRepository.findByPhotographerProfileWithCategory(photographer);
        return maps.stream().map(PhotographerResponse.mapDTO::new).collect(Collectors.toList());
    }



    /**
     * 포토그래퍼에서 카테고리 제거
     */
    /**
     * 포토그래퍼에서 카테고리 제거 (관리자용)
     */
    @Transactional
    public void removeCategoryFromPhotographerByAdmin(Long photographerId, Long categoryId) {
        // ID로 프로필을 직접 조회 (소유권 체크 없음)
        PhotographerProfile photographer = getPhotographerById(photographerId);

        // 제거할 카테고리 조회
        PhotographerCategory category = photographerCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new Exception404("카테고리를 찾을 수 없습니다."));

        // 삭제할 매핑 정보 조회
        PhotographerMap mapping = photographerMapRepository
                .findByPhotographerProfileAndPhotographerCategory(photographer, category)
                .orElseThrow(() -> new Exception404("매핑을 찾을 수 없습니다."));

        // 매핑 정보 삭제
        photographerMapRepository.delete(mapping);
    }

    /**
     * 포토그래퍼 완전 삭제 (물리적 삭제)
     */
    /**
     * 포토그래퍼 완전 삭제 (관리자용)
     */
    @Transactional
    public void removePhotographerByAdmin(Long photographerId) {
        // ID로 프로필을 직접 조회 (소유권 체크 없음)
        PhotographerProfile photographer = getPhotographerById(photographerId);

        // 관련 매핑 데이터 먼저 삭제 (데이터 무결성을 위해 필수)
        List<PhotographerMap> mappings = photographerMapRepository.findByPhotographerProfile(photographer);
        photographerMapRepository.deleteAll(mappings);

        // 포토그래퍼 최종 삭제
        photographerRepository.deleteById(photographerId);
    }


    // 포토 관련


    // 포토 서비스

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

            // 가격 정보 조회 및 설정
            List<PriceInfoResponse.PriceInfoListDTO> priceInfoList = getPriceInfoListByPhotoServiceId(serviceInfo.getServiceId());
            mainDTO.setPriceInfoList(priceInfoList);

            // 카테고리 정보 조회 및 설정
            List<PhotoCategoryResponse.PhotoCategoryListDTO> categoryList = getServiceCategoriesByAdmin(serviceInfo.getServiceId());
            mainDTO.setCategoryList(categoryList);

            serviceInfoList.add(mainDTO);
        }
        return serviceInfoList;
    }



    // 포토 서비스 상세조회
    public PhotoServiceResponse.PhotoServiceDetailDTO serviceDetailByAdmin(Long id) {


        PhotoServiceInfo photoServiceInfo = photoServiceJpaRepository.findByIdWithUser(id)
                .orElseThrow(() -> new Exception404("해당 게시물이 존재하지 않습니다."));


        PhotoServiceResponse.PhotoServiceDetailDTO dto = new PhotoServiceResponse.PhotoServiceDetailDTO(photoServiceInfo);


        dto.setCanEdit(true);


        return dto;
    }


    // 포토 서비스 저장

    @Transactional
    public void saveServiceByAdmin(Long userId, PhotoServiceInfoRequest.SaveDTO saveDTO, LoginAdmin loginAdmin) {

        log.info("saveDTO 값 확인 : {}", saveDTO);

        PhotographerProfile userProfileInfo = photographerRepository.findByUser_UserId(userId).orElseThrow(() -> new Exception404("해당 유저가 존재하지 않습니다."));

        PhotoServiceInfo savedInfo = photoServiceJpaRepository.save(saveDTO.toEntity(userProfileInfo));

        if (savedInfo.getServiceId() == null) {
            throw new Exception500("서비스 생성이 처리되지 않았습니다.");
        }

        // 2. 가격 정보들 등록 (새로 추가)
        if (saveDTO.getPriceInfoList() != null && !saveDTO.getPriceInfoList().isEmpty()) {
            createPriceInfosByadmin(savedInfo, saveDTO.getPriceInfoList());
        } else {
            throw new Exception400("서비스의 가격 정보가 없습니다");
        }


        // 3. 카테고리 매핑 생성
        if (saveDTO.getCategoryIdList() != null && !saveDTO.getCategoryIdList().isEmpty()) {
            PhotoMappingRequest.SaveDTO mappingDTO = PhotoMappingRequest.SaveDTO.builder()
                    .serviceId(savedInfo.getServiceId())
                    .categoryIdList(saveDTO.getCategoryIdList())
                    .build();

            createMappingByAdmin(userId, mappingDTO, loginAdmin); // 같은 메서드 재사용!
        }
    }

    @Transactional
    public void updateServiceByAdmin(Long userId, Long serviceId, PhotoServiceInfoRequest.UpdateDTO reqDTO, LoginAdmin loginAdmin) {

        PhotographerProfile userProfileInfo = photographerRepository.findByUser_UserId(userId).orElseThrow(() -> new Exception404("해당 유저가 존재하지 않습니다."));


        // 2. 수정할 서비스 조회
        PhotoServiceInfo photoService = photoServiceJpaRepository.findById(serviceId)
                .orElseThrow(() -> new Exception404("해당 서비스가 존재하지 않습니다."));

        if (!photoService.getPhotographerProfile().getPhotographerProfileId()
                .equals(userProfileInfo.getPhotographerProfileId())) {
            throw new Exception400("해당 유저의 서비스가 아닙니다.");
        }


        // 4. 엔티티 수정
        photoService.updateFromDto(reqDTO);

        // 5. 가격 정보 수정 (기존 삭제 후 새로 생성)
        if (reqDTO.getPriceInfoList() != null && !reqDTO.getPriceInfoList().isEmpty()) {
            // 기존 가격 정보 삭제
            priceInfoJpaRepository.deleteByPhotoServiceInfo_serviceId(serviceId);

            // 새로운 가격 정보 생성
            updatePriceInfosByAdmin(photoService, reqDTO.getPriceInfoList());
        }

        // 6. 카테고리 매핑 수정 (기존 삭제 후 새로 생성)
        if (reqDTO.getCategoryIdList() != null && !reqDTO.getCategoryIdList().isEmpty()) {
            // 기존 매핑 삭제
            photoMappingRepository.deleteByPhotoServiceInfo_ServiceId(serviceId);

            // 새로운 매핑 생성
            updateCategoryMappingsByAdmin(photoService, reqDTO.getCategoryIdList());
        }

    }

    // 가격 정보 업데이트 헬퍼 메서드
    private void updatePriceInfosByAdmin(PhotoServiceInfo savedInfo, List<PriceInfoRequest.CreateDTO> priceInfoList) {
        for (PriceInfoRequest.CreateDTO priceInfoDTO : priceInfoList) {
            PriceInfo priceInfo = priceInfoDTO.toEntity(savedInfo);
            priceInfoJpaRepository.save(priceInfo);
        }
    }

    // 카테고리 매핑 업데이트 헬퍼 메서드
    private void updateCategoryMappingsByAdmin(PhotoServiceInfo savedInfo, List<Long> categoryIdList) {
        for (Long categoryId : categoryIdList) {
            PhotoServiceCategory category = photoCategoryJpaRepository.findById(categoryId)
                    .orElseThrow(() -> new Exception404("카테고리 ID " + categoryId + "가 존재하지 않습니다."));

            PhotoServiceMapping mapping = PhotoServiceMapping.builder()
                    .photoServiceInfo(savedInfo)
                    .photoServiceCategory(category)
                    .build();

            photoMappingRepository.save(mapping);
        }
    }

    @Transactional
    public void deleteServiceByAdmin(Long userId, Long serviceId, LoginAdmin loginAdmin) {

        // 1. 로그인 사용자의 프로필 조회
        PhotographerProfile userProfileInfo = photographerRepository
                .findByUser_UserId(userId)
                .orElseThrow(() -> new Exception404("해당 유저가 존재하지 않습니다."));

        // 2. 삭제할 서비스 조회
        PhotoServiceInfo photoService = photoServiceJpaRepository.findById(serviceId)
                .orElseThrow(() -> new Exception404("해당 서비스가 존재하지 않습니다."));

        // 3. 유효성 검증 (서비스 주인이 맞는지 확인)
        if (!photoService.getPhotographerProfile().getPhotographerProfileId().equals(userProfileInfo.getPhotographerProfileId())) {
            throw new Exception403("해당 유저의 서비스가 아닙니다.");
        }

        // 4. 삭제 실행
        photoServiceJpaRepository.deleteById(serviceId);
    }


    // 카테고리 생성
    @Transactional
    public void saveCategoryByAdmin(PhotoCategoryRequest.SaveDTO saveDTO, LoginAdmin loginAdmin) {


        PhotoServiceCategory category = saveDTO.toEntity();
        PhotoServiceCategory savedCategory = photoCategoryJpaRepository.save(category);

        if (savedCategory.getCategoryId() == null) {
            throw new Exception500("카테고리 생성이 처리되지 않았습니다.");
        }
    }

    // 카테고리 수정
    @Transactional
    public void updateCategoryByAdmin(Long id, PhotoCategoryRequest.UpdateDTO updateDTO, LoginAdmin loginAdmin) {

        PhotoServiceCategory category = photoCategoryJpaRepository.findById(id)
                .orElseThrow(() -> new Exception404("해당 카테고리가 존재하지 않습니다."));

        category.updateFromDto(updateDTO);
        photoCategoryJpaRepository.save(category);
    }

    // 카테고리 삭제
    @Transactional
    public void deleteCategoryByAdmin(Long id, LoginAdmin loginAdmin) {

        PhotoServiceCategory category = photoCategoryJpaRepository.findById(id)
                .orElseThrow(() -> new Exception404("해당 카테고리가 존재하지 않습니다."));

        photoCategoryJpaRepository.deleteById(id);
    }

    // 포토카테고리 리스트

    public List<PhotoCategoryResponse.PhotoCategoryListDTO> categoryListByAdmin() {
        List<PhotoServiceCategory> categories = photoCategoryJpaRepository.findAll();

        List<PhotoCategoryResponse.PhotoCategoryListDTO> categoryList = new ArrayList<>();

        for (PhotoServiceCategory category : categories) {
            PhotoCategoryResponse.PhotoCategoryListDTO categoryDTO = new PhotoCategoryResponse.PhotoCategoryListDTO(category);
            categoryList.add(categoryDTO);
        }

        return categoryList;
    }

    // 카테고리 상세 조회
    public PhotoCategoryResponse.PhotoCategoryDetailDTO categoryDetailByAdmin(Long id) {
        PhotoServiceCategory category = photoCategoryJpaRepository.findById(id)
                .orElseThrow(() -> new Exception404("해당 카테고리가 존재하지 않습니다."));

        return new PhotoCategoryResponse.PhotoCategoryDetailDTO(category);
    }



    // 포토매핑 관련 서비스들

    public List<PhotoMappingResponse.PhotoMappingListDTO> getMappingListByAdmin(Long serviceId, Long categoryId) {

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
    public PhotoMappingResponse.PhotoMappingDetailDTO getMappingDetailByAdmin(Long id) {
        PhotoServiceMapping mapping = photoMappingRepository.findById(id)
                .orElseThrow(() -> new Exception404("해당 매핑이 존재하지 않습니다."));

        return new PhotoMappingResponse.PhotoMappingDetailDTO(mapping);
    }


    // 매핑 생성
    @Transactional
    public void createMappingByAdmin(Long userId, PhotoMappingRequest.SaveDTO saveDTO, LoginAdmin loginAdmin) {

        // 1. 유저 프로필 조회
        PhotographerProfile userProfile = photographerRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new Exception404("해당 유저가 존재하지 않습니다."));
        // 서비스 존재 여부 확인
        PhotoServiceInfo photoServiceInfo = photoServiceJpaRepository.findById(saveDTO.getServiceId())
                .orElseThrow(() -> new Exception404("해당 서비스가 존재하지 않습니다."));

        if (!photoServiceInfo.getPhotographerProfile().getUser().getUserId().equals(userId)) {
            throw new Exception400("해당 유저의 서비스가 아닙니다.");
        }

        // 여러 카테고리 처리
        for (Long categoryId : saveDTO.getCategoryIdList()) {
            // 카테고리 존재 여부 확인
            PhotoServiceCategory photoServiceCategory = photoCategoryJpaRepository.findById(categoryId)
                    .orElseThrow(() -> new Exception404("카테고리 ID " + categoryId + "가 존재하지 않습니다."));

            // 중복 매핑 체크
            if (photoMappingRepository.existsByPhotoServiceInfoAndPhotoServiceCategory(photoServiceInfo, photoServiceCategory)) {
                log.warn("이미 연결된 서비스-카테고리입니다. serviceId: {}, categoryId: {}", saveDTO.getServiceId(), categoryId);
                continue;
            }

            // 매핑 생성
            PhotoServiceMapping mapping = PhotoServiceMapping.builder()
                    .photoServiceInfo(photoServiceInfo)
                    .photoServiceCategory(photoServiceCategory)
                    .build();
            photoMappingRepository.save(mapping);
        }
    }


    // 매핑 삭제
    @Transactional
    public void removeMappingByAdmin(Long serviceId) {

        List<PhotoServiceMapping> mapping = photoMappingRepository.findByPhotoServiceInfo_ServiceId(serviceId);

        if(mapping.isEmpty()) {
            throw new Exception404("해당 서비스 ID와 관련된 매핑 데이터가 존재하지 않습니다.");
        }

        photoMappingRepository.deleteByPhotoServiceInfo_serviceId(serviceId);
    }


    // 특정 서비스의 카테고리들 조회
    public List<PhotoCategoryResponse.PhotoCategoryListDTO> getServiceCategoriesByAdmin(Long serviceId) {
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
    public List<PhotoServiceResponse.PhotoServiceListDTO> getCategoryServicesByAdmin(Long categoryId) {
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
    public PriceInfoResponse.PriceInfoListDTO createPriceInfoByAdmin(Long photoServiceInfoId, PriceInfoRequest.CreateDTO request) {
        PhotoServiceInfo photoServiceInfo = photoServiceJpaRepository.findById(photoServiceInfoId)
                .orElseThrow(() -> new Exception404("PhotoService를 찾을 수 없습니다: " + photoServiceInfoId));

        PriceInfo priceInfo = request.toEntity(photoServiceInfo);
        PriceInfo savedPriceInfo = priceInfoJpaRepository.save(priceInfo);
        return new PriceInfoResponse.PriceInfoListDTO(savedPriceInfo);
    }


    // 새로운 메서드 추가
    public List<PriceInfoResponse.PriceInfoListDTO> getPriceInfoListByPhotoServiceId(Long photoServiceInfoId) {
        if (!photoServiceJpaRepository.existsById(photoServiceInfoId)) {
            throw new Exception404("PhotoService를 찾을 수 없습니다: " + photoServiceInfoId);
        }

        List<PriceInfo> priceInfoList = priceInfoJpaRepository.findByPhotoServiceInfo_serviceId(photoServiceInfoId);

        List<PriceInfoResponse.PriceInfoListDTO> result = new ArrayList<>();
        for (PriceInfo priceInfo : priceInfoList) {
            PriceInfoResponse.PriceInfoListDTO dto = new PriceInfoResponse.PriceInfoListDTO(priceInfo);
            result.add(dto);
        }
        return result;
    }

    // 가격 정보 단일 조회
    public PriceInfoResponse.PriceInfoDetailDTO getPriceInfo(Long priceInfoId) {
        PriceInfo priceInfo = priceInfoJpaRepository.findById(priceInfoId)
                .orElseThrow(() -> new Exception404("가격 정보를 찾을 수 없습니다: " + priceInfoId));

        return new PriceInfoResponse.PriceInfoDetailDTO(priceInfo);
    }

    // 가격 정보 수정
    @Transactional
    public PriceInfoResponse.PriceInfoListDTO updatePriceInfoByAdmin(PriceInfoRequest.UpdateDTO request) {
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
    public void deletePriceInfoByAdmin(Long priceInfoId) {
        if (!priceInfoJpaRepository.existsById(priceInfoId)) {
            throw new Exception404("가격 정보를 찾을 수 없습니다: " + priceInfoId);
        }
        priceInfoJpaRepository.deleteById(priceInfoId);
    }

    // PhotoService 삭제 시 연관된 가격 정보들 삭제 (PhotoService에서 호출)
    @Transactional
    public void deletePriceInfosByPhotoServiceIdByAdmin(Long photoServiceInfoId) {
        priceInfoJpaRepository.deleteByPhotoServiceInfo_serviceId(photoServiceInfoId);
    }

    // PhotoService 등록 시 가격 정보들 일괄 등록 (PhotoService에서 호출)
    @Transactional
    public List<PriceInfo> createPriceInfosByadmin(PhotoServiceInfo photoServiceInfo, List<PriceInfoRequest.CreateDTO> priceInfoRequests) {

        if (priceInfoRequests == null || priceInfoRequests.isEmpty()) {
            return Collections.emptyList();
        }

        List<PriceInfo> priceInfoList = priceInfoRequests.stream()
                .map(request -> request.toEntity(photoServiceInfo))
                .collect(Collectors.toList());

        return priceInfoJpaRepository.saveAll(priceInfoList);
    }



    // 포트폴리오 관련

    // ============ 포트폴리오 CRUD ============

    /**
     * 카테고리 단건 조회
     */
    public PortfolioCategoryResponse.DetailDTO getPortCategoryByAdmin(Long categoryId) {
        try {
            log.info("카테고리 조회: ID = {}", categoryId);

            validateCategoryId(categoryId);

            PortfolioCategory category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new Exception404("존재하지 않는 카테고리입니다: " + categoryId));

            return PortfolioCategoryResponse.DetailDTO.from(category);

        } catch (Exception400 | Exception404 e) {
            throw e;
        } catch (Exception e) {
            log.error("카테고리 조회 중 예상치 못한 오류 발생", e);
            throw new Exception500("카테고리 조회 중 서버 오류가 발생했습니다");
        }
    }

    /**
     * 활성화된 카테고리 목록 조회
     */
    public List<PortfolioCategoryResponse.DetailDTO> getPortActiveCategoriesByAdmin() {
        try {
            log.info("활성화된 카테고리 목록 조회");

            List<PortfolioCategory> categories = categoryRepository.findByIsActiveTrue();

            return categories.stream()
                    .map(PortfolioCategoryResponse.DetailDTO::from)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("활성화된 카테고리 목록 조회 중 예상치 못한 오류 발생", e);
            throw new Exception500("카테고리 목록 조회 중 서버 오류가 발생했습니다");
        }
    }

    /**
     * 포트폴리오 생성
     */
    @Transactional
    public PortfolioResponse.DetailDTO createPortfolioByAdmin(Long userId, PortfolioRequest.CreateDTO request) {
        try {
            log.info("포트폴리오 생성 시작: userId = {}", userId);

            User user = userJpaRepository.findById(userId)
                    .orElseThrow(() -> new Exception404("해당 유저가 존재하지 않습니다."));

            // 입력값 검증
            validateCreateRequest(request);
            validateLoginUserByAdmin(user);

            // 로그인한 사용자의 사진작가 프로필 조회
            PhotographerProfile photographer = photographerRepository.findByUser_UserId(user.getUserId())
                    .orElseThrow(() -> new Exception404("사진작가 프로필이 존재하지 않습니다. 사진작가 등록을 먼저 해주세요."));

            Portfolio portfolio = request.toEntity(photographer);
            Portfolio savedPortfolio = portfolioRepository.save(portfolio);

            // 카테고리 매핑 생성
            if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
                createCategoryMappingsByAdmin(savedPortfolio, request.getCategoryIds());
            }

            log.info("포트폴리오 생성 완료: ID = {}", savedPortfolio.getPortfolioId());
            return PortfolioResponse.DetailDTO.from(savedPortfolio);

        } catch (Exception404 | Exception400 e) {
            throw e; // 이미 정의된 예외는 그대로 전파
        } catch (Exception e) {
            log.error("포트폴리오 생성 중 예상치 못한 오류 발생", e);
            throw new Exception500("포트폴리오 생성 중 서버 오류가 발생했습니다");
        }
    }

    /**
     * 포트폴리오 수정
     */
    @Transactional
    public PortfolioResponse.DetailDTO updatePortfolioByAdmin(Long userId, Long portfolioId, PortfolioRequest.UpdateDTO request) {
        try {
            log.info("포트폴리오 수정 시작: portfolioId = {}", portfolioId);

            User user = userJpaRepository.findById(userId)
                    .orElseThrow(() -> new Exception404("해당 유저가 존재하지 않습니다."));

            // 입력값 검증
            validatePortfolioId(portfolioId);
            validateUpdateRequest(request);
            validateLoginUserByAdmin(user);

            Portfolio portfolio = portfolioRepository.findById(portfolioId)
                    .orElseThrow(() -> new Exception404("존재하지 않는 포트폴리오입니다: " + portfolioId));

            if (!portfolio.getPhotographerProfile().getUser().getUserId().equals(user.getUserId())) {
                throw new Exception403("해당 유저의 포트폴리오가 아닙니다.");
            }


            // 필드 업데이트
            if (request.getTitle() != null) {
                portfolio.setTitle(request.getTitle());
            }
            if (request.getDescription() != null) {
                portfolio.setDescription(request.getDescription());
            }
            if (request.getThumbnailUrl() != null) {
                portfolio.setThumbnailUrl(request.getThumbnailUrl());
            }

            // 카테고리 매핑 업데이트
            if (request.getCategoryIds() != null) {
                updateCategoryMappings(portfolio, request.getCategoryIds());
            }

            log.info("포트폴리오 수정 완료: portfolioId = {}", portfolioId);
            return PortfolioResponse.DetailDTO.from(portfolio);

        } catch (Exception403 | Exception404 | Exception400 e) {
            throw e;
        } catch (Exception e) {
            log.error("포트폴리오 수정 중 예상치 못한 오류 발생", e);
            throw new Exception500("포트폴리오 수정 중 서버 오류가 발생했습니다");
        }
    }

    /**
     * 포트폴리오 상세 조회 (조회수 증가)
     */
    @Transactional
    public PortfolioResponse.DetailDTO getPortfolioDetailByAdmin(Long portfolioId) {
        try {
            log.info("포트폴리오 상세 조회: portfolioId = {}", portfolioId);

            validatePortfolioId(portfolioId);

            Portfolio portfolio = portfolioRepository.findById(portfolioId)
                    .orElseThrow(() -> new Exception404("존재하지 않는 포트폴리오입니다: " + portfolioId));

            // 조회수 증가 로직은 엔티티에 없으므로 주석 처리
            // portfolioRepository.incrementViewCount(portfolioId);

            return PortfolioResponse.DetailDTO.from(portfolio);

        } catch (Exception404 | Exception400 e) {
            throw e;
        } catch (Exception e) {
            log.error("포트폴리오 상세 조회 중 예상치 못한 오류 발생", e);
            throw new Exception500("포트폴리오 조회 중 서버 오류가 발생했습니다");
        }
    }

    /**
     * 포트폴리오 삭제
     */
    @Transactional
    public void deletePortfolioByAdmin(Long userId, Long portfolioId) {
        try {
            log.info("포트폴리오 삭제 시작: portfolioId = {}", portfolioId);

            User user = userJpaRepository.findById(userId)
                    .orElseThrow(() -> new Exception404("해당 유저가 존재하지 않습니다."));

            validatePortfolioId(portfolioId);
            validateLoginUserByAdmin(user);

            Portfolio portfolio = portfolioRepository.findById(portfolioId)
                    .orElseThrow(() -> new Exception404("존재하지 않는 포트폴리오입니다: " + portfolioId));

            if (!portfolio.getPhotographerProfile().getUser().getUserId().equals(user.getUserId())) {
                throw new Exception403("해당 유저의 포트폴리오가 아닙니다.");
            }

            // 연관된 매핑과 이미지부터 삭제
            portfolioMapRepository.deleteByPortfolio_PortfolioId(portfolioId);
            portfolioImageRepository.deleteByPortfolio_PortfolioId(portfolioId);

            portfolioRepository.deleteById(portfolioId);
            log.info("포트폴리오 삭제 완료: portfolioId = {}", portfolioId);

        } catch (Exception403 | Exception404 | Exception400 e) {
            throw e;
        } catch (Exception e) {
            log.error("포트폴리오 삭제 중 예상치 못한 오류 발생", e);
            throw new Exception500("포트폴리오 삭제 중 서버 오류가 발생했습니다");
        }
    }

    // ============ 포트폴리오 조회/검색 ============

    /**
     * 포트폴리오 목록 조회 (페이징)
     */
    public Page<PortfolioResponse.ListDTO> getPortfolioListByAdmin(PortfolioRequest.SearchDTO searchRequest) {
        try {
            log.info("포트폴리오 목록 조회: sortBy = {}", searchRequest.getSortBy());

            validateSearchRequest(searchRequest);

            Pageable pageable = PageRequest.of(searchRequest.getPage(), searchRequest.getSize());
            Page<Portfolio> portfolioPage;

            // 엔티티에 viewCount, likeCount, isPublic 필드가 없으므로, 최신순으로만 조회하도록 수정
            portfolioPage = portfolioRepository.findAllByOrderByCreatedAtDesc(pageable);

            return portfolioPage.map(PortfolioResponse.ListDTO::from);

        } catch (Exception400 e) {
            throw e;
        } catch (Exception e) {
            log.error("포트폴리오 목록 조회 중 예상치 못한 오류 발생", e);
            throw new Exception500("포트폴리오 목록 조회 중 서버 오류가 발생했습니다");
        }
    }

    /**
     * 포트폴리오 검색
     */
    public Page<PortfolioResponse.ListDTO> searchPortfoliosByAdmin(PortfolioRequest.SearchDTO searchRequest) {
        try {
            log.info("포트폴리오 검색: keyword = {}", searchRequest.getKeyword());

            validateSearchRequest(searchRequest);

            Pageable pageable = PageRequest.of(searchRequest.getPage(), searchRequest.getSize());

            if (searchRequest.getKeyword() != null && !searchRequest.getKeyword().trim().isEmpty()) {
                return portfolioRepository.findByTitleContaining(searchRequest.getKeyword(), pageable)
                        .map(PortfolioResponse.ListDTO::from);
            }
            return getPortfolioListByAdmin(searchRequest);

        } catch (Exception400 e) {
            throw e;
        } catch (Exception e) {
            log.error("포트폴리오 검색 중 예상치 못한 오류 발생", e);
            throw new Exception500("포트폴리오 검색 중 서버 오류가 발생했습니다");
        }
    }

    /**
     * 사진작가별 포트폴리오 조회
     */
    public List<PortfolioResponse.ListDTO> getPhotographerPortfoliosByAdmin(Long photographerId) {
        try {
            log.info("사진작가 포트폴리오 조회: photographerId = {}", photographerId);

            validatePhotographerId(photographerId);

            PhotographerProfile photographer = photographerRepository.findById(photographerId)
                    .orElseThrow(() -> new Exception404("존재하지 않는 사진작가입니다: " + photographerId));

            List<Portfolio> portfolios = portfolioRepository.findByPhotographerProfile(photographer);

            return portfolios.stream()
                    .map(PortfolioResponse.ListDTO::from)
                    .collect(Collectors.toList());

        } catch (Exception404 | Exception400 e) {
            throw e;
        } catch (Exception e) {
            log.error("사진작가 포트폴리오 조회 중 예상치 못한 오류 발생", e);
            throw new Exception500("사진작가 포트폴리오 조회 중 서버 오류가 발생했습니다");
        }
    }

    // ============ 이미지 관리 ============

    /**
     * 포트폴리오에 이미지 추가
     */
    @Transactional
    public PortfolioResponse.ImageDTO addImagePortByAdmin(Long userId, PortfolioRequest.AddImageDTO request,
                                                          LoginAdmin admin) {
        try {
            log.info("이미지 추가: portfolioId = {}", request.getPortfolioId());

            User user = userJpaRepository.findById(userId)
                    .orElseThrow(() -> new Exception404("해당 유저가 존재하지 않습니다."));


            validateAddImageRequest(request);
            validateLoginUserByAdmin(user);

            Portfolio portfolio = portfolioRepository.findById(request.getPortfolioId())
                    .orElseThrow(() -> new Exception404("존재하지 않는 포트폴리오입니다: " + request.getPortfolioId()));

            // isMain이 null일 경우 false로 설정
            Boolean isMain = (request.getIsMain() != null) ? request.getIsMain() : false;

            // 이미 메인 이미지가 존재하면 해제
            if (isMain) {
                portfolioImageRepository.findByPortfolioAndIsMainTrue(portfolio)
                        .ifPresent(mainImage -> mainImage.setIsMain(false));
            }

            PortfolioImage image = new PortfolioImage();
            image.setPortfolio(portfolio);
            image.setImageUrl(request.getImageData());
            image.setIsMain(isMain);

            PortfolioImage savedImage = portfolioImageRepository.save(image);
            log.info("이미지 추가 완료: imageId = {}", savedImage.getPortfolioImageId());

            return PortfolioResponse.ImageDTO.from(savedImage);

        } catch (Exception404 | Exception400 e) {
            throw e;
        } catch (Exception e) {
            log.error("이미지 추가 중 예상치 못한 오류 발생", e);
            throw new Exception500("이미지 추가 중 서버 오류가 발생했습니다");
        }
    }

    /**
     * 이미지 삭제
     */
    @Transactional
    public void deletePortImageByAdmin(Long userId, Long imageId, LoginAdmin loginAdmin) {
        try {
            log.info("이미지 삭제: imageId = {}", imageId);

            User user = userJpaRepository.findById(userId)
                    .orElseThrow(() -> new Exception404("해당 유저가 존재하지 않습니다."));


            validateLoginUserByAdmin(user);
            validateImageId(imageId);

            PortfolioImage image = portfolioImageRepository.findById(imageId)
                    .orElseThrow(() -> new Exception404("존재하지 않는 이미지입니다: " + imageId));

            Long ownerId = image.getPortfolio()
                    .getPhotographerProfile()
                    .getUser()
                    .getUserId();

            if (!ownerId.equals(userId)) {
                throw new Exception403("해당 유저의 이미지가 아닙니다: userId=" + userId);
            }

            portfolioImageRepository.deleteById(imageId);
            log.info("이미지 삭제 완료: imageId = {}", imageId);

        } catch (Exception404 | Exception400 e) {
            throw e;
        } catch (Exception e) {
            log.error("이미지 삭제 중 예상치 못한 오류 발생", e);
            throw new Exception500("이미지 삭제 중 서버 오류가 발생했습니다");
        }
    }

    // ============ 카테고리 매핑 관리 ============

    /**
     * 포트폴리오에 카테고리 추가
     */
    @Transactional
    public void addCategoryToPortfolioByAdmin(Long userId, Long portfolioId, Long categoryId) {
        try {
            log.info("카테고리 추가: portfolioId = {}, categoryId = {}", portfolioId, categoryId);

            User user = userJpaRepository.findById(userId)
                    .orElseThrow(() -> new Exception404("해당 유저가 존재하지 않습니다."));


            validateLoginUserByAdmin(user);
            validatePortfolioId(portfolioId);
            validateCategoryId(categoryId);

            Portfolio portfolio = portfolioRepository.findById(portfolioId)
                    .orElseThrow(() -> new Exception404("존재하지 않는 포트폴리오입니다: " + portfolioId));

            if (!portfolio.getPhotographerProfile().getUser().getUserId().equals(userId)) {
                throw new Exception403("해당 유저의 포트폴리오가 아닙니다.");
            }

            PortfolioCategory category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new Exception404("존재하지 않는 카테고리입니다: " + categoryId));



            if (!portfolioMapRepository.existsByPortfolioAndPortfolioCategory(portfolio, category)) {
                PortfolioMap mapping = new PortfolioMap();
                mapping.setPortfolio(portfolio);
                mapping.setPortfolioCategory(category);
                portfolioMapRepository.save(mapping);
            }

        } catch (Exception404 | Exception400 | Exception403 e) {
            throw e;
        } catch (Exception e) {
            log.error("카테고리 추가 중 예상치 못한 오류 발생", e);
            throw new Exception500("카테고리 추가 중 서버 오류가 발생했습니다");
        }
    }

    // ============ Private 헬퍼 메서드 ============

    private void createCategoryMappingsByAdmin(Portfolio portfolio, List<Long> categoryIds) {
        for (Long categoryId : categoryIds) {
            validateCategoryId(categoryId);

            PortfolioCategory category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new Exception404("존재하지 않는 카테고리입니다: " + categoryId));

            if (!portfolioMapRepository.existsByPortfolioAndPortfolioCategory(portfolio, category)) {
                PortfolioMap mapping = new PortfolioMap();
                mapping.setPortfolio(portfolio);
                mapping.setPortfolioCategory(category);
                portfolioMapRepository.save(mapping);
            }
        }
    }

    private void updateCategoryMappings(Portfolio portfolio, List<Long> categoryIds) {
        portfolioMapRepository.deleteByPortfolio(portfolio);
        if (categoryIds != null && !categoryIds.isEmpty()) {
            createCategoryMappingsByAdmin(portfolio, categoryIds);
        }
    }

    // ============ 유효성 검증 메서드 ============

    private void validateCreateRequest(PortfolioRequest.CreateDTO request) {
        if (request == null) {
            throw new Exception400("포트폴리오 생성 요청 데이터가 없습니다");
        }
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new Exception400("포트폴리오 제목은 필수입니다");
        }
    }

    private void validateLoginUserByAdmin(User user) {
        if (user.getUserId() <= 0) {
            throw new Exception400("유효하지 않은 사용자 ID입니다");
        }
    }

    private void validateUpdateRequest(PortfolioRequest.UpdateDTO request) {
        if (request == null) {
            throw new Exception400("포트폴리오 수정 요청 데이터가 없습니다");
        }
    }

    private void validateAddImageRequest(PortfolioRequest.AddImageDTO request) {
        if (request == null) {
            throw new Exception400("이미지 추가 요청 데이터가 없습니다");
        }
        validatePortfolioId(request.getPortfolioId());
        if (request.getImageData() == null || request.getImageData().trim().isEmpty()) {
            throw new Exception400("이미지 URL은 필수입니다");
        }
    }

    private void validateSearchRequest(PortfolioRequest.SearchDTO request) {
        if (request == null) {
            throw new Exception400("검색 요청 데이터가 없습니다");
        }
        if (request.getPage() < 0) {
            throw new Exception400("페이지 번호는 0 이상이어야 합니다");
        }
        if (request.getSize() <= 0 || request.getSize() > 100) {
            throw new Exception400("페이지 크기는 1 이상 100 이하여야 합니다");
        }
    }

    private void validatePortfolioId(Long portfolioId) {
        if (portfolioId == null || portfolioId <= 0) {
            throw new Exception400("유효하지 않은 포트폴리오 ID입니다: " + portfolioId);
        }
    }

    private void validatePhotographerId(Long photographerId) {
        if (photographerId == null || photographerId <= 0) {
            throw new Exception400("유효하지 않은 사진작가 ID입니다: " + photographerId);
        }
    }

    private void validateCategoryId(Long categoryId) {
        if (categoryId == null || categoryId <= 0) {
            throw new Exception400("유효하지 않은 카테고리 ID입니다: " + categoryId);
        }
    }

    private void validateImageId(Long imageId) {
        if (imageId == null || imageId <= 0) {
            throw new Exception400("유효하지 않은 이미지 ID입니다: " + imageId);
        }
    }

    // 예약 관련  -- 취소내역

    public BookingCancelInfoResponse.BookingCancelInfoGetResponse getBookingCancelInfoByAdmin(Long bookingCancelInfoId, Long userId ,LoginAdmin loginAdmin) {

        if (loginAdmin == null) {
            throw new Exception403("관리자 권한이 필요합니다.");
        }

        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new Exception404("존재하지 않는 유저입니다."));

        BookingCancelInfo bookingCancelInfo = bookingCancelInfoJpaRepository.findById(bookingCancelInfoId)
                .orElseThrow(() -> new Exception404("존재하지 않는 내역입니다."));


        String userTypeName = user.getUserType().getTypeName();

        switch (userTypeName) {
            case "user" -> {
                if (!bookingCancelInfo.getUserProfile().getUser().getUserId().equals(user.getUserId())) {
                    throw new Exception403("해당 유저의 내역이 아닙니다.");
                }
            }
            case "photographer" -> {
                if (!bookingCancelInfo.getBookingInfo().getPhotographerProfile().getUser().getUserId()
                        .equals(user.getUserId())) {
                    throw new Exception403("해당 유저의 내역이 아닙니다.");
                }
            }
            default -> throw new Exception403("지원하지 않는 유저 유형입니다.");
        }
        return BookingCancelInfoResponse.BookingCancelInfoGetResponse.from(bookingCancelInfo);
    }

    // 예약관련

    public List<BookingInfoResponse.BookingUserListDTO> getUserBookingsByAdmin(Long userId, LoginAdmin loginAdmin){

        if (loginAdmin == null) {
            throw new Exception403("관리자 권한이 필요합니다.");
        }

        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new Exception404("존재하지 않는 유저입니다."));

        return bookingInfoJpaRepository.findByUserId(user.getUserId())
                .stream()
                .map(BookingInfoResponse.BookingUserListDTO::new)
                .toList();
    }

    // [포토그래퍼]
    public List<BookingInfoResponse.BookingPhotographerListDTO> getPhotographerBookingsByAdmin(Long userId, LoginAdmin loginAdmin){

        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new Exception404("존재하지 않는 유저입니다."));

        return bookingInfoJpaRepository.findByPhotographerId(user.getUserId())
                .stream()
                .map(BookingInfoResponse.BookingPhotographerListDTO::new)
                .toList();
    }


    // 예약 상세 조회
    public BookingInfoResponse.BookingDetailDTO getBookingDetailByAdmin(Long userId, Long photographerId, Long bookingInfoId, LoginAdmin loginAdmin){

        if (loginAdmin == null) {
            throw new Exception403("관리자 권한이 필요합니다.");
        }

        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new Exception404("존재하지 않는 유저입니다."));

        BookingInfo bookingInfo = bookingInfoJpaRepository.findById(bookingInfoId)
                .orElseThrow(() -> new Exception404("존재하지 않는 예약입니다."));


        Long bookingUserId = bookingInfo.getUserProfile().getUser().getUserId();
        Long bookingPhotographerId = bookingInfo.getPhotographerProfile().getUser().getUserId();

        // 관리자 검증: 예약자와 포토그래퍼 모두 일치해야 함
        if (!bookingUserId.equals(userId) || !bookingPhotographerId.equals(photographerId)) {
            throw new Exception403("해당 유저와 포토그래퍼 간의 예약이 아닙니다.");
        }

        return new BookingInfoResponse.BookingDetailDTO(bookingInfo);
    }


    // 예약 생성
    @Transactional
    public void createBookingByAdmin(BookingInfoRequest.CreateDTO createDTO, LoginAdmin loginAdmin) {
        if (loginAdmin == null) {
            throw new Exception403("관리자 권한이 필요합니다.");
        }

        UserProfile userProfile = userProfileJpaRepository.findByUserId(createDTO.getUserProfileId())
                .orElseThrow(() -> new Exception404("예약을 받을 사용자를 찾을 수 없습니다."));

        PhotographerProfile photographerProfile = photographerRepository.findById(createDTO.getPhotographerProfileId())
                .orElseThrow(() -> new Exception404("존재하지 않는 작가입니다."));

        PhotoServiceInfo photoServiceInfo = photoServiceJpaRepository.findById(createDTO.getPhotoServiceId())
                .orElseThrow(() -> new Exception404("존재하지 않는 포토 서비스입니다."));

        PriceInfo priceInfo = priceInfoJpaRepository.findById(createDTO.getPriceInfoId())
                .orElseThrow(() -> new Exception404("존재하지 않는 가격 정보입니다."));

        BookingInfo bookingInfo = createDTO.toEntity(userProfile, photographerProfile, priceInfo, photoServiceInfo);


        bookingInfo.setStatus(BookingStatus.PENDING);

        bookingInfoJpaRepository.save(bookingInfo);
    }


    // [포토그래퍼] 예약 확정
    @Transactional
    public void confirmBookingByAdmin(AdminRequest.ConfirmBookingDTO request, LoginAdmin loginAdmin) {
        BookingInfo bookingInfo = bookingInfoJpaRepository.findById(request.getBookingInfoId())
                .orElseThrow(() -> new Exception404("존재하지 않는 예약 내역입니다."));

        if (loginAdmin == null) {
            throw new Exception403("관리자 권한이 필요합니다.");
        }

        // 관리자 권한으로 확인 → 포토그래퍼 본인 여부 체크 제거
        if(!bookingInfo.getStatus().canChangeTo(BookingStatus.CONFIRMED)){
            throw new Exception400("예약대기 상태일 때만 예약을 확정할 수 있습니다. 현재 상태: "
                    + bookingInfo.getStatus().getDescription());
        }

        bookingInfo.setStatus(BookingStatus.CONFIRMED);
    }
    // [사용자] 예약 취소
    @Transactional
    public void cancelBookingByAdmin(AdminRequest.ConfirmBookingDTO request, LoginAdmin loginAdmin){

        if (loginAdmin == null) {
            throw new Exception403("관리자 권한이 필요합니다.");
        }

        BookingInfo bookingInfo = bookingInfoJpaRepository.findById(request.getBookingInfoId())
                .orElseThrow(() -> new Exception404("존재하지 않는 예약 내역입니다."));

        if(!bookingInfo.getStatus().canChangeTo(BookingStatus.CANCELED)){
            throw new Exception400("예약대기 상태일 때만 예약을 확정할 수 있습니다. 현재 상태: "
                    + bookingInfo.getStatus().getDescription());
        }

        bookingInfo.setStatus(BookingStatus.CANCELED);
    }


    // [포토그래퍼] 촬영 완료 처리
    @Transactional
    public void completeBookingByAdmin(AdminRequest.ConfirmBookingDTO request, LoginAdmin loginAdmin){

        BookingInfo bookingInfo = bookingInfoJpaRepository.findById(request.getBookingInfoId())
                .orElseThrow(() -> new Exception404("존재하지 않는 예약 내역입니다."));

        if (loginAdmin == null) {
            throw new Exception403("관리자 권한이 필요합니다.");
        }

        // CONFIRMED 상태일 때만 촬영 완료 처리 가능
        if(!bookingInfo.getStatus().canChangeTo(BookingStatus.COMPLETED)){
            throw new Exception400("예약확정 상태일 때만 촬영 완료로 처리할 수 있습니다. 현재 상태: " + bookingInfo.getStatus().getDescription());
        }
        bookingInfo.setStatus(BookingStatus.COMPLETED);
    }

    // TODO - 리뷰 남길 시 사용할 서비스
    // [사용자] 리뷰 작성 후 상태 변경
    @Transactional
    public void reviewBooking(Long bookingInfoId, LoginUser loginUser){
        BookingInfo bookingInfo = bookingInfoJpaRepository.findById(bookingInfoId)
                .orElseThrow(() -> new Exception404("존재하지 않는 예약 내역입니다."));

        // 예약자 본인만 리뷰 작성 가능
        if (!bookingInfo.getUserProfile().getUser().getUserId().equals(loginUser.getId())){
            throw new Exception403("해당 서비스에 리뷰를 남길 수 없습니다.");
        }

        // COMPLETED 상태일 때만 리뷰 작성 가능
        if(!bookingInfo.getStatus().canChangeTo(BookingStatus.REVIEWED)){
            throw new Exception400("촬영완료 상태일 때만 리뷰를 작성할 수 있습니다. 현재 상태: " + bookingInfo.getStatus().getDescription());
        }
        bookingInfo.setStatus(BookingStatus.REVIEWED);
    }


}








