package com.green.chakak.chakak.admin.controller;

import com.green.chakak.chakak._global.errors.exception.Exception403;
import com.green.chakak.chakak._global.utils.ApiUtil;
import com.green.chakak.chakak._global.utils.Define;
import com.green.chakak.chakak.account.domain.UserType;
import com.green.chakak.chakak.account.service.UserProfileService;
import com.green.chakak.chakak.account.service.UserService;
import com.green.chakak.chakak.account.service.UserTypeService;
import com.green.chakak.chakak.account.service.request.UserProfileRequest;
import com.green.chakak.chakak.account.service.request.UserRequest;
import com.green.chakak.chakak.account.service.request.UserTypeRequest;
import com.green.chakak.chakak.account.service.response.UserProfileResponse;
import com.green.chakak.chakak.account.service.response.UserResponse;
import com.green.chakak.chakak.admin.domain.LoginAdmin;
import com.green.chakak.chakak.admin.service.AdminService;
import com.green.chakak.chakak.admin.service.request.AdminRequest;
import com.green.chakak.chakak.admin.service.response.AdminResponse;
import com.green.chakak.chakak.photo.service.PhotoService;
import com.green.chakak.chakak.photo.service.request.PhotoCategoryRequest;
import com.green.chakak.chakak.photo.service.request.PhotoMappingRequest;
import com.green.chakak.chakak.photo.service.request.PhotoServiceInfoRequest;
import com.green.chakak.chakak.photo.service.response.PhotoCategoryResponse;
import com.green.chakak.chakak.photo.service.response.PhotoMappingResponse;
import com.green.chakak.chakak.photo.service.response.PhotoServiceResponse;
import com.green.chakak.chakak.photographer.service.request.PhotographerCategoryRequest;
import com.green.chakak.chakak.photographer.service.request.PhotographerRequest;
import com.green.chakak.chakak.photographer.service.response.PhotographerResponse;
import com.green.chakak.chakak.portfolios.service.PortfolioService;
import com.green.chakak.chakak.portfolios.service.request.PortfolioRequest;
import com.green.chakak.chakak.portfolios.service.response.PortfolioCategoryResponse;
import com.green.chakak.chakak.portfolios.service.response.PortfolioResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("api/admin")
@RequiredArgsConstructor
public class AdminRestController {

    private final AdminService adminService;
    private final PhotoService photoService;
    private final UserProfileService userProfileService;
    private final UserService userService;
    private final UserTypeService userTypeService;
    private final PortfolioService portfolioService;


    // 관리자 로그인
    @PostMapping("/login")
    public ResponseEntity<ApiUtil<AdminResponse.AdminLoginDto>> login(
            @Valid @RequestBody AdminRequest.LoginRequest req) {

        // 로그인 처리 (JWT 발급 포함)
        AdminResponse.AdminLoginDto loginDto = adminService.login(req, null);

        // 헤더에 Authorization: Bearer <token> 추가 + DTO 반환
        return ResponseEntity.ok()
                .header(Define.AUTH, Define.BEARER + loginDto.getAccessToken())
                .body(new ApiUtil<>(loginDto));
    }


    // 유저 전체 조회
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(
            @PageableDefault(size = 10, sort = "userId", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin) {
        Page<AdminResponse.AdminUserListDto> users = adminService.findAllUsers(loginAdmin, pageable);
        return ResponseEntity.ok(new ApiUtil<>(users));
    }

    // 포토그래퍼만 조회 (전체 리스트)
    @GetMapping("/users/photographers")
    public ResponseEntity<ApiUtil<List<AdminResponse.UserListResponseDto>>> getPhotographers() {
        List<AdminResponse.UserListResponseDto> list = adminService.getPhotographersOnly();
        return ResponseEntity.ok(new ApiUtil<>(list));
    }

    // 일반 유저만 조회 (전체 리스트)
    @GetMapping("/users/normals")
    public ResponseEntity<ApiUtil<List<AdminResponse.UserListResponseDto>>> getNormalUsers() {
        List<AdminResponse.UserListResponseDto> list = adminService.findUserOnly();
        return ResponseEntity.ok(new ApiUtil<>(list));
    }

    // 유저 관련


    // 유저 수정
    @PutMapping("/users/update/{id}")
    public ResponseEntity<?> updateByUserAdmin(@PathVariable Long id, @Valid @RequestBody UserRequest.UpdateRequest req,
                                        @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin) {

        if (loginAdmin == null) {
            throw new Exception403("관리자 권한이 필요합니다.");
        }
        UserResponse.UpdateResponse response = adminService.updateUserByAdmin(id, req, loginAdmin);
        return ResponseEntity.ok(response);
    }

    // 회원 삭제
    @DeleteMapping("/users/delete/{id}")
    public ResponseEntity<?> deleteUserByAdmin(@PathVariable Long id, @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin) { // LoginUser 자동 주입
        if (loginAdmin == null) {
            throw new Exception403("관리자 권한이 필요합니다.");
        }
        adminService.deleteUserByAdmin(id, loginAdmin);
        return ResponseEntity.ok(new ApiUtil<>("서비스 생성이 완료 되었습니다"));
    }

    // 유저프로파일 관련



    // 프로필 수정

    @PutMapping("v1/users/{userId}/profile/update")
    public ResponseEntity<?> updateProfileByAdmin(@PathVariable Long userId, @Valid @RequestBody UserProfileRequest.UpdateDTO updateDTO,
                                           Errors errors,
                                                  @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin){
        UserProfileResponse.UpdateDTO updateProfileDTO = adminService.updateProfileByAdmin(userId, updateDTO);
        return ResponseEntity.ok(new ApiUtil<>("처리가 완료 되었습니다."));
    }

    // 유저 타입

    // 수정
    @PutMapping("/user-types/{id}")
    public ResponseEntity<?> updateUserTypeByAdmin(@PathVariable Long id, @RequestBody UserTypeRequest request,
                                   @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin) {

        UserType updatedUserType = adminService.updateUserTypeByAdmin(id, request);
        return ResponseEntity.ok(new ApiUtil<>(updatedUserType));



    }

    // 삭제
    @DeleteMapping("/user-types/{id}")
    public void deleteUserTypeByAdmin(@PathVariable Long id,
                               @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin){
        adminService.deleteUserTypeByAdmin(id);
    }

    // 포토그래퍼 관련

    // 포토그래퍼 프로필 저장
    @PostMapping("/photographers")
    public ResponseEntity<?> createProfileBYAdmin(@Valid @RequestBody PhotographerRequest.SaveProfile saveProfile,
                                           @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin
    ) {
        PhotographerResponse.SaveDTO response = adminService.createProfileByAdmin(saveProfile);
        URI location = URI.create(String.format("/api/photographers/%d", response.getPhotographerId()));
        return ResponseEntity.created(location).body(new ApiUtil<>("프로필 등록이 완료되었습니다"));
    }

    // 포토그래퍼 프로필 상세 조회
    @GetMapping("/photographers/detail/{photographerId}")
    public ResponseEntity<?> getPhotographerDetailByAdmin(@PathVariable Long photographerId,
                                                   @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin) {
        PhotographerResponse.DetailDTO response = adminService.getProfileDetailByAdmin(photographerId);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    // 활성 포토그래퍼 프로필 목록
    @GetMapping("/photographers")
    public ResponseEntity<?> getActivePhotographersByAdmin(@RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin) {
        List<PhotographerResponse.ListDTO> response = adminService.getActiveProfileByAdmin();
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    // 포토그래퍼 프로필 수정
    @PutMapping("/photographers/update/{photographerId}")
    public ResponseEntity<?> updateProfileByAdmin(@PathVariable Long photographerId,
                                           @Valid @RequestBody PhotographerRequest.UpdateProfile updateProfile,
                                           @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin) {
        PhotographerResponse.UpdateDTO response = adminService.updateProfileByAdmin(photographerId, updateProfile);
        return ResponseEntity.ok(new ApiUtil<>("프로필 정보 수정이 완료 되었습니다"));
    }

    // 프로필 활성화
    @PatchMapping("/photographers/{photographerId}/activate")
    public ResponseEntity<?> activateProfileByAdmin(@PathVariable Long photographerId,
                                             @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin) {
        PhotographerResponse.UpdateDTO response = adminService.activatePhotographerByAdmin(photographerId, loginAdmin);
        return ResponseEntity.ok(new ApiUtil<>("프로필 활성화가 완료되었습니다"));
    }

    // 프로필 비활성화
    @PatchMapping("/photographers/{photographerId}/deactivate")
    public ResponseEntity<?> deactivateProfileByAdmin(@PathVariable Long photographerId,
                                               @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin) {
        PhotographerResponse.UpdateDTO response = adminService.deactivatePhotographerByAdmin(photographerId, loginAdmin);
        return ResponseEntity.ok(new ApiUtil<>("프로필 비활성화가 완료되었습니다"));
    }

    // 지역별 조회
    @GetMapping("/photographers/location/{location}")
    public ResponseEntity<?> getPhotographersByLocationByAdmin(@PathVariable String location) {
        List<PhotographerResponse.ListDTO> response = adminService.getPhotographersByLocationbyAdmin(location);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    // 상호명 검색
    @GetMapping("/photographers/search")
    public ResponseEntity<?> searchByBusinessNameByAdmin(@RequestParam("businessName") String businessName) {
        List<PhotographerResponse.ListDTO> response = adminService.searchByBusinessNameByAdmin(businessName);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    // 포토그래퍼 프로필 삭제
    @DeleteMapping("/photographers/delete/{id}")
    public ResponseEntity<?> removePhotographerByAdmin(@PathVariable("id") Long photographerId,
                                                @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin) {
        adminService.removePhotographerByAdmin(photographerId);
        return ResponseEntity.noContent().build();
    }

    // 포토그래퍼에게 카테고리 추가
    @PostMapping("/photographers/{photographerId}/categories")
    public ResponseEntity<?> addCategoryToPhotographerByAdmin(@PathVariable Long photographerId,
                                                       @Valid @RequestBody PhotographerCategoryRequest.AddCategoryToPhotographer request,
                                                              @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin) {
        PhotographerResponse.mapDTO response = adminService.addCategoryToPhotographerByAdmin(photographerId, request, loginAdmin);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    // 포토그래퍼 카테고리 삭제
    @DeleteMapping("/photographers/{photographerId}/categories/{categoryId}")
    public ResponseEntity<?> removeCategoryFromPhotographerByAdmin(
            @PathVariable Long photographerId,
            @PathVariable Long categoryId,
            @RequestAttribute("loginAdmin") LoginAdmin loginAdmin) { // 관리자 권한 확인

        // 서비스 계층에 실제 로직 위임
        adminService.removeCategoryFromPhotographerByAdmin(photographerId, categoryId);

        // 성공 응답 반환
        return ResponseEntity.ok(new ApiUtil<>("처리가 완료되었습니다"));
    }

    // 포토그래퍼 서비스 상세 조회
    @GetMapping("/photographers/service-detail/{id}")
    public ResponseEntity<?> detailByAdmin(@PathVariable Long id,
                                           @RequestAttribute(Define.LOGIN_ADMIN) LoginAdmin loginAdmin) {

        PhotoServiceResponse.PhotoServiceDetailDTO serviceDetail = adminService.serviceDetailByAdmin(id);

        return ResponseEntity.ok(new ApiUtil<>(serviceDetail));
    }

    /**
     * 특정 카테고리에 속한 포토그래퍼들 조회
     */
    @GetMapping("/{categoryId}/photographers")
    public ResponseEntity<?> getPhotographersByCategoryByAdmin(@PathVariable Long categoryId) {
        List<PhotographerResponse.ListDTO> response = adminService.getPhotographersByCategoryByAdmin(categoryId);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }





    // 포토관련

    //포토 카테고리 - 생성
    @PostMapping("/photo/categories")
    public ResponseEntity<?> createByAdmin(
            @Valid @RequestBody PhotoCategoryRequest.SaveDTO saveDTO,
            @RequestAttribute(Define.LOGIN_ADMIN) LoginAdmin loginAdmin) {
        if (loginAdmin == null) {
            throw new Exception403("관리자 권한이 필요합니다.");
        }
        adminService.saveCategoryByAdmin(saveDTO, loginAdmin );
        return ResponseEntity.ok(new ApiUtil<>("카테고리 생성이 완료 되었습니다"));
    }

    // 카테고리 수정
    @PatchMapping("/photo/categories/{id}")
    public ResponseEntity<?> updateByAdmin(
            @PathVariable Long id,
            @Valid @RequestBody PhotoCategoryRequest.UpdateDTO updateDTO,
            @RequestAttribute(Define.LOGIN_ADMIN) LoginAdmin loginAdmin) {

        if (loginAdmin == null) {
            throw new Exception403("관리자 권한이 필요합니다.");
        }
        adminService.updateCategoryByAdmin(id, updateDTO, loginAdmin);
        return ResponseEntity.ok(new ApiUtil<>("카테고리 수정이 완료 되었습니다"));
    }


    // 카테고리 삭제
    @DeleteMapping("/photo/categories/{id}")
    public ResponseEntity<?> deleteByAdmin(
            @PathVariable Long id,
            @RequestAttribute(Define.LOGIN_ADMIN) LoginAdmin loginAdmin) {

        if (loginAdmin == null) {
            throw new Exception403("관리자 권한이 필요합니다.");
        }
        adminService.deleteCategoryByAdmin(id, loginAdmin);
        return ResponseEntity.ok(new ApiUtil<>("카테고리 삭제가 완료 되었습니다"));
    }

    @GetMapping("/photo/categories/list")
    public ResponseEntity<?> list(@RequestAttribute(Define.LOGIN_ADMIN) LoginAdmin loginAdmin) {
        List<PhotoCategoryResponse.PhotoCategoryListDTO> categoryList = adminService.categoryListByAdmin();
        return ResponseEntity.ok(new ApiUtil<>(categoryList));
    }




    // 포토 서비스 목록 조회
    @GetMapping("/photo/services/list")
    public ResponseEntity<?> listByAdmin(@RequestParam(required = false) String keyword,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  @RequestAttribute(Define.LOGIN_ADMIN) LoginAdmin loginAdmin){

        List<PhotoServiceResponse.PhotoServiceListDTO> serviceList = adminService.serviceList(page,size,keyword);

        return ResponseEntity.ok(new ApiUtil<>(serviceList));

    }


    // 포토그래퍼 서비스 상세 조회
    @GetMapping("/photo/services/detail/{id}")
    public ResponseEntity<?> detailPhotoByAdmin(@PathVariable Long id,
                                           @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin) {

        PhotoServiceResponse.PhotoServiceDetailDTO serviceDetail = adminService.serviceDetailByAdmin(id);

        return ResponseEntity.ok(new ApiUtil<>(serviceDetail));
    }

//    // 포토그래퍼 서비스 생성
//    @PostMapping("/photo/services/{userId}")
//    public ResponseEntity<?> createPhotoByAdmin(@PathVariable Long userId, @Valid @RequestBody PhotoServiceInfoRequest.SaveDTO saveDTO, Errors errors,
//                                                 @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin)
//    {
//
//        adminService.saveServiceByAdmin(userId, saveDTO, loginAdmin);
//
//        return ResponseEntity.ok(new ApiUtil<>("서비스 생성이 완료 되었습니다"));
//    }

    // 포토그래퍼 서비스 수정
    @PatchMapping("/users/{userId}/photo/services/{serviceId}")
    public ResponseEntity<?> updatePhotoByAdmin(@PathVariable Long userId,
                                                @PathVariable Long serviceId,
                                                @Valid @RequestBody PhotoServiceInfoRequest.UpdateDTO updateDTO,
                                                Errors errors,
                                                @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin) {

        adminService.updateServiceByAdmin(userId, serviceId, updateDTO,  loginAdmin);

        return ResponseEntity.ok(new ApiUtil<>("서비스 수정이 완료 되었습니다."));
    }

    // 포토그래퍼 서비스 삭제
    @DeleteMapping("/users/{userId}/photo/services/{id}")
    public ResponseEntity<ApiUtil<String>> deletePhotoByAdmin(@PathVariable("userId") Long userId, @PathVariable("id") Long serviceId,
                                                  @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin) {

        adminService.deleteServiceByAdmin(userId, serviceId, loginAdmin);
        return ResponseEntity.ok(new ApiUtil<>("서비스 삭제가 완료 되었습니다."));
    }

    // 매핑 목록 조회
    @GetMapping("/photo/mappings/list")
    public ResponseEntity<ApiUtil<List<PhotoMappingResponse.PhotoMappingListDTO>>> getMappingListByAdmin(
            @RequestParam(required = false) Long serviceId,
            @RequestParam(required = false) Long categoryId) {

        List<PhotoMappingResponse.PhotoMappingListDTO> mappingList =
                adminService.getMappingListByAdmin(serviceId, categoryId);
        return ResponseEntity.ok(new ApiUtil<>(mappingList));
    }

    // 매핑 상세 조회
    @GetMapping("/photo/mappings/detail/{id}")
    public ResponseEntity<ApiUtil<PhotoMappingResponse.PhotoMappingDetailDTO>> getMappingDetailByAdmin(@PathVariable Long id) {
        PhotoMappingResponse.PhotoMappingDetailDTO mappingDetail = adminService.getMappingDetailByAdmin(id);
        return ResponseEntity.ok(new ApiUtil<>(mappingDetail));
    }

    // 매핑 생성 (포토 서비스)
    @PostMapping("/photo/mappings/{userId}")
    public ResponseEntity<ApiUtil<String>> createMappingByAdmin(@PathVariable Long userId,
            @Valid @RequestBody PhotoMappingRequest.SaveDTO saveDTO,
            @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin) {

        adminService.createMappingByAdmin(userId, saveDTO, loginAdmin);
        return ResponseEntity.ok(new ApiUtil<>("서비스-카테고리 매핑이 생성되었습니다"));
    }

    // 매핑 삭제
    @DeleteMapping("/photo/mappings/{serviceId}")
    public ResponseEntity<ApiUtil<String>> removeMappingByAdmin(
            @PathVariable Long serviceId,
            @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin) {

        adminService.removeMappingByAdmin(serviceId);
        return ResponseEntity.ok(new ApiUtil<>("서비스-카테고리 매핑이 삭제되었습니다"));
    }

    // 특정 서비스의 카테고리들 조회
    @GetMapping("/photo/mappings/service/{serviceId}/categories")
    public ResponseEntity<ApiUtil<List<PhotoCategoryResponse.PhotoCategoryListDTO>>> getServiceCategories(@PathVariable Long serviceId) {
        List<PhotoCategoryResponse.PhotoCategoryListDTO> categories = adminService.getServiceCategoriesByAdmin(serviceId);
        return ResponseEntity.ok(new ApiUtil<>(categories));
    }

    // 특정 카테고리의 서비스들 조회
    @GetMapping("/photo/mappings/category/{categoryId}/services")
    public ResponseEntity<ApiUtil<List<PhotoServiceResponse.PhotoServiceListDTO>>> getCategoryServices(@PathVariable Long categoryId) {
        List<PhotoServiceResponse.PhotoServiceListDTO> services = adminService.getCategoryServicesByAdmin(categoryId);
        return ResponseEntity.ok(new ApiUtil<>(services));
    }

    // 포트폴리오

    // 카테고리 상세 조회
    @GetMapping("/portfolio-categories/{categoryId}")
    public ResponseEntity<?> getPortCategoryByAdmin(@PathVariable Long categoryId) {
        PortfolioCategoryResponse.DetailDTO response = adminService.getPortCategoryByAdmin(categoryId);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    // 활성 카테고리 목록 조회
    @GetMapping("/portfolio-categories")
    public ResponseEntity<?> getActiveCategories() {
        List<PortfolioCategoryResponse.DetailDTO> response = adminService.getPortActiveCategoriesByAdmin();
        return ResponseEntity.ok(new ApiUtil<>(response));
    }


    // 포트폴리오 생성
    @PostMapping("users/{userId}/portfolios")
    public ResponseEntity<?> createPortfolioByAdmin(@PathVariable Long userId,
                                                    @Valid @RequestBody PortfolioRequest.CreateDTO createRequest,
                                                    @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin) {
                PortfolioResponse.DetailDTO response = adminService.createPortfolioByAdmin(userId, createRequest);
        URI location = URI.create(String.format("/api/portfolios/%d", response.getPortfolioId()));
        return ResponseEntity.created(location).body(new ApiUtil<>(response));
    }

    // 포트폴리오 상세 조회 - 인증 불필요
    @GetMapping("/portfolios/{portfolioId}")
    public ResponseEntity<?> getPortfolioDetailByAdmin(@PathVariable Long portfolioId) {
        PortfolioResponse.DetailDTO response = adminService.getPortfolioDetailByAdmin(portfolioId);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    // 포트폴리오 목록 조회 (페이징) - 인증 불필요
    @GetMapping("/portfolios")
    public ResponseEntity<?> getPortfolioList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "latest") String sortBy) {

        PortfolioRequest.SearchDTO searchRequest = new PortfolioRequest.SearchDTO();
        searchRequest.setPage(page);
        searchRequest.setSize(size);
        searchRequest.setSortBy(sortBy);

        Page<PortfolioResponse.ListDTO> response = adminService.getPortfolioListByAdmin(searchRequest);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    // 포트폴리오 수정 - 로그인 필요
    @PutMapping("users/{userId}/portfolios/{portfolioId}")
    public ResponseEntity<?> updatePortfolio(@PathVariable Long portfolioId,
                                             @Valid @RequestBody PortfolioRequest.UpdateDTO updateRequest,
                                             @PathVariable Long userId,
                                             @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin) {
        PortfolioResponse.DetailDTO response = adminService.updatePortfolioByAdmin( userId, portfolioId, updateRequest);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    // 포트폴리오 삭제 - 로그인 필요
    @DeleteMapping("user/{userId}/portfolios/{portfolioId}")
    public ResponseEntity<?> deletePortfolio(@PathVariable Long userId, @PathVariable Long portfolioId,
                                             @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin) {

       adminService.deletePortfolioByAdmin(userId, portfolioId);
        return ResponseEntity.noContent().build();
    }

    // 포트폴리오 검색 - 인증 불필요
    @GetMapping("/portfolios/search")
    public ResponseEntity<?> searchPortfoliosByAdmin(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PortfolioRequest.SearchDTO searchRequest = new PortfolioRequest.SearchDTO();
        searchRequest.setKeyword(keyword);
        searchRequest.setPage(page);
        searchRequest.setSize(size);

        Page<PortfolioResponse.ListDTO> response = adminService.searchPortfoliosByAdmin(searchRequest);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    // 사진작가별 포트폴리오 조회 - 인증 불필요
    @GetMapping("/portfolios/photographer/{photographerId}")
    public ResponseEntity<?> getPhotographerPortfoliosByAdmin(@PathVariable Long photographerId) {
        List<PortfolioResponse.ListDTO> response = adminService.getPhotographerPortfoliosByAdmin(photographerId);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    // 포트폴리오에 이미지 추가 - 로그인 필요 (권한 체크는 서비스에서)
    @PostMapping("users/{userId}/portfolios/{portfolioId}/images")
    public ResponseEntity<?> addImageToPortfolioByAdmin(@PathVariable Long userId, @PathVariable Long portfolioId,
                                                 @Valid @RequestBody PortfolioRequest.AddImageDTO addImageRequest,
                                                 @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin) {
        // 포트폴리오 소유자 확인은 서비스 레이어에서 처리할 수 있도록 LoginUser 전달

        addImageRequest.setPortfolioId(portfolioId);
        PortfolioResponse.ImageDTO response = adminService.addImagePortByAdmin(userId, addImageRequest, loginAdmin);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    // 이미지 삭제 - 로그인 필요 (권한 체크는 서비스에서)
    @DeleteMapping("users/{userId}/portfolios/images/{imageId}")
    public ResponseEntity<?> deleteImageByAdmin(@PathVariable Long userId, @PathVariable Long imageId,
                                         @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin) {

        adminService.deletePortImageByAdmin(userId, imageId, loginAdmin);
        return ResponseEntity.noContent().build();
    }

    // 포트폴리오에 카테고리 추가 - 로그인 필요 (권한 체크는 서비스에서)
    @PostMapping("users/{userId}/portfolios/{portfolioId}/categories/{categoryId}")
    public ResponseEntity<?> addCategoryToPortfolioByAdmin(@PathVariable Long userId,
                                                    @PathVariable Long portfolioId,
                                                    @PathVariable Long categoryId,
                                                    @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin) {

        adminService.addCategoryToPortfolioByAdmin(userId, portfolioId, categoryId);
        return ResponseEntity.ok(new ApiUtil<>("카테고리가 추가되었습니다."));
    }



}
