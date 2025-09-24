package com.green.chakak.chakak.admin.controller;

import com.green.chakak.chakak._global.errors.exception.Exception403;
import com.green.chakak.chakak._global.utils.ApiUtil;
import com.green.chakak.chakak._global.utils.Define;
import com.green.chakak.chakak.account.domain.LoginUser;
import com.green.chakak.chakak.account.domain.UserType;
import com.green.chakak.chakak.account.service.request.UserProfileRequest;
import com.green.chakak.chakak.account.service.request.UserRequest;
import com.green.chakak.chakak.account.service.request.UserTypeRequest;
import com.green.chakak.chakak.account.service.response.UserProfileResponse;
import com.green.chakak.chakak.account.service.response.UserResponse;
import com.green.chakak.chakak.admin.domain.LoginAdmin;
import com.green.chakak.chakak.admin.service.AdminService;
import com.green.chakak.chakak.admin.service.request.AdminRequest;
import com.green.chakak.chakak.admin.service.response.AdminResponse;
import com.green.chakak.chakak.booking.service.request.BookingInfoRequest;
import com.green.chakak.chakak.booking.service.response.BookingCancelInfoResponse;
import com.green.chakak.chakak.booking.service.response.BookingInfoResponse;
import com.green.chakak.chakak.photo.service.request.PhotoCategoryRequest;
import com.green.chakak.chakak.photo.service.request.PhotoMappingRequest;
import com.green.chakak.chakak.photo.service.request.PhotoServiceInfoRequest;
import com.green.chakak.chakak.photo.service.response.PhotoCategoryResponse;
import com.green.chakak.chakak.photo.service.response.PhotoMappingResponse;
import com.green.chakak.chakak.photo.service.response.PhotoServiceResponse;
import com.green.chakak.chakak.photographer.service.request.PhotographerCategoryRequest;
import com.green.chakak.chakak.photographer.service.request.PhotographerRequest;
import com.green.chakak.chakak.photographer.service.response.PhotographerResponse;
import com.green.chakak.chakak.portfolios.service.request.PortfolioRequest;
import com.green.chakak.chakak.portfolios.service.response.PortfolioCategoryResponse;
import com.green.chakak.chakak.portfolios.service.response.PortfolioResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/admin")
@RequiredArgsConstructor
public class AdminRestController {

    private final AdminService adminService;


    @PostMapping("/login")
    public ResponseEntity<ApiUtil<AdminResponse.AdminLoginDto>> login(
            @Valid @RequestBody AdminRequest.LoginRequest req) {


        AdminResponse.AdminLoginDto loginDto = adminService.login(req, null);


        return ResponseEntity.ok()
                .header(Define.AUTH, Define.BEARER + loginDto.getAccessToken())
                .body(new ApiUtil<>(loginDto));
    }



    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(
            @PageableDefault(size = 10, sort = "userId", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin) {
        Page<AdminResponse.AdminUserListDto> users = adminService.findAllUsers(loginAdmin, pageable);
        return ResponseEntity.ok(new ApiUtil<>(users));
    }


    @GetMapping("/users/photographers")
    public ResponseEntity<ApiUtil<List<AdminResponse.UserListResponseDto>>> getPhotographers() {
        List<AdminResponse.UserListResponseDto> list = adminService.getPhotographersOnly();
        return ResponseEntity.ok(new ApiUtil<>(list));
    }


    @GetMapping("/users/normals")
    public ResponseEntity<ApiUtil<List<AdminResponse.UserListResponseDto>>> getNormalUsers() {
        List<AdminResponse.UserListResponseDto> list = adminService.findUserOnly();
        return ResponseEntity.ok(new ApiUtil<>(list));
    }

    // 유저 관련

    @PutMapping("/users/update/{id}")
    public ResponseEntity<?> updateByUserAdmin(@Valid @RequestBody UserRequest.UpdateRequest req,
                                        @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin) {

        if (loginAdmin == null) {
            throw new Exception403("관리자 권한이 필요합니다.");
        }
        UserResponse.UpdateResponse response = adminService.updateUserByAdmin(req.toEntity().getUserId(), req, loginAdmin);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/users/delete/{id}")
    public ResponseEntity<?> deleteUserByAdmin(@PathVariable Long id, @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin) {
        if (loginAdmin == null) {
            throw new Exception403("관리자 권한이 필요합니다.");
        }
        adminService.deleteUserByAdmin(id, loginAdmin);
        return ResponseEntity.ok(new ApiUtil<>("서비스 생성이 완료 되었습니다"));
    }

    // 유저프로파일 관련

    @PostMapping("/users/{userId}/profiles")
    public ResponseEntity<?> createProfileByAdmin(
            @PathVariable Long userId,
            @Valid @RequestBody UserProfileRequest.CreateDTO createDTO,
            @RequestAttribute(value = Define.LOGIN_ADMIN, required = false) LoginAdmin loginAdmin) {

        UserProfileResponse.DetailDTO response = adminService.createdProfileByAdmin(userId, createDTO, loginAdmin);
        return ResponseEntity.ok(new ApiUtil<>("처리가 완료 되었습니다."));
    }


    @PutMapping("v1/users/{userId}/profile/update")
    public ResponseEntity<?> updateProfileByAdmin(@PathVariable Long userId, @Valid @RequestBody UserProfileRequest.UpdateDTO updateDTO,
                                           Errors errors,
                                                  @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin){
        UserProfileResponse.UpdateDTO updateProfileDTO = adminService.updateProfileByAdmin(userId, updateDTO);
        return ResponseEntity.ok(new ApiUtil<>("처리가 완료 되었습니다."));
    }

    // 유저 타입

    @PutMapping("/user-types/{id}")
    public ResponseEntity<?> updateUserTypeByAdmin(@PathVariable Long id, @RequestBody UserTypeRequest request,
                                   @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin) {

        UserType updatedUserType = adminService.updateUserTypeByAdmin(id, request);
        return ResponseEntity.ok(new ApiUtil<>(updatedUserType));



    }

    @DeleteMapping("/user-types/{id}")
    public void deleteUserTypeByAdmin(@PathVariable Long id,
                               @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin){
        adminService.deleteUserTypeByAdmin(id);
    }

    // 포토그래퍼 관련

    @PostMapping("/photographers")
    public ResponseEntity<?> createProfileBYAdmin(@Valid @RequestBody PhotographerRequest.SaveProfile saveProfile,
                                           @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin
    ) {
        PhotographerResponse.SaveDTO response = adminService.createProfileByAdmin(saveProfile);
        URI location = URI.create(String.format("/api/photographers/%d", response.getPhotographerId()));
        return ResponseEntity.created(location).body(new ApiUtil<>("프로필 등록이 완료되었습니다"));
    }

    @GetMapping("/photographers/detail/{photographerId}")
    public ResponseEntity<?> getPhotographerDetailByAdmin(@PathVariable Long photographerId,
                                                   @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin) {
        PhotographerResponse.DetailDTO response = adminService.getProfileDetailByAdmin(photographerId);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    @GetMapping("/photographers")
    public ResponseEntity<?> getActivePhotographersByAdmin(@RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin) {
        List<PhotographerResponse.ListDTO> response = adminService.getActiveProfileByAdmin();
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    @PutMapping("/photographers/update/{photographerId}")
    public ResponseEntity<?> updateProfileByAdmin(@PathVariable Long photographerId,
                                           @Valid @RequestBody PhotographerRequest.UpdateProfile updateProfile,
                                           @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin) {
        PhotographerResponse.UpdateDTO response = adminService.updateProfileByAdmin(photographerId, updateProfile);
        return ResponseEntity.ok(new ApiUtil<>("프로필 정보 수정이 완료 되었습니다"));
    }

    @PatchMapping("/photographers/{photographerId}/activate")
    public ResponseEntity<?> activateProfileByAdmin(@PathVariable Long photographerId,
                                             @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin) {
        PhotographerResponse.UpdateDTO response = adminService.activatePhotographerByAdmin(photographerId, loginAdmin);
        return ResponseEntity.ok(new ApiUtil<>("프로필 활성화가 완료되었습니다"));
    }

    @PatchMapping("/photographers/{photographerId}/deactivate")
    public ResponseEntity<?> deactivateProfileByAdmin(@PathVariable Long photographerId,
                                               @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin) {
        PhotographerResponse.UpdateDTO response = adminService.deactivatePhotographerByAdmin(photographerId, loginAdmin);
        return ResponseEntity.ok(new ApiUtil<>("프로필 비활성화가 완료되었습니다"));
    }


    @GetMapping("/photographers/location/{location}")
    public ResponseEntity<?> getPhotographersByLocationByAdmin(@PathVariable String location) {
        List<PhotographerResponse.ListDTO> response = adminService.getPhotographersByLocationbyAdmin(location);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }


    @GetMapping("/photographers/search")
    public ResponseEntity<?> searchByBusinessNameByAdmin(@RequestParam("businessName") String businessName) {
        List<PhotographerResponse.ListDTO> response = adminService.searchByBusinessNameByAdmin(businessName);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }


    @DeleteMapping("/photographers/delete/{id}")
    public ResponseEntity<?> removePhotographerByAdmin(@PathVariable("id") Long photographerId,
                                                @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin) {
        adminService.removePhotographerByAdmin(photographerId);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/photographers/{photographerId}/categories")
    public ResponseEntity<?> addCategoryToPhotographerByAdmin(@PathVariable Long photographerId,
                                                       @Valid @RequestBody PhotographerCategoryRequest.AddCategoryToPhotographer request,
                                                              @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin) {
        PhotographerResponse.mapDTO response = adminService.addCategoryToPhotographerByAdmin(photographerId, request, loginAdmin);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }


    @DeleteMapping("/photographers/{photographerId}/categories/{categoryId}")
    public ResponseEntity<?> removeCategoryFromPhotographerByAdmin(
            @PathVariable Long photographerId,
            @PathVariable Long categoryId,
            @RequestAttribute("loginAdmin") LoginAdmin loginAdmin) { // 관리자 권한 확인


        adminService.removeCategoryFromPhotographerByAdmin(photographerId, categoryId);


        return ResponseEntity.ok(new ApiUtil<>("처리가 완료되었습니다"));
    }


    @GetMapping("/photographers/service-detail/{id}")
    public ResponseEntity<?> detailByAdmin(@PathVariable Long id,
                                           @RequestAttribute(Define.LOGIN_ADMIN) LoginAdmin loginAdmin) {

        PhotoServiceResponse.PhotoServiceDetailDTO serviceDetail = adminService.serviceDetailByAdmin(id);

        return ResponseEntity.ok(new ApiUtil<>(serviceDetail));
    }


    @GetMapping("/{categoryId}/photographers")
    public ResponseEntity<?> getPhotographersByCategoryByAdmin(@PathVariable Long categoryId) {
        List<PhotographerResponse.ListDTO> response = adminService.getPhotographersByCategoryByAdmin(categoryId);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }





    // 포토관련


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





    @GetMapping("/photo/services/list")
    public ResponseEntity<?> listByAdmin(@RequestParam(required = false) String keyword,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  @RequestAttribute(Define.LOGIN_ADMIN) LoginAdmin loginAdmin){

        List<PhotoServiceResponse.PhotoServiceListDTO> serviceList = adminService.serviceList(page,size,keyword);

        return ResponseEntity.ok(new ApiUtil<>(serviceList));

    }



    @GetMapping("/photo/services/detail/{id}")
    public ResponseEntity<?> detailPhotoByAdmin(@PathVariable Long id,
                                           @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin) {

        PhotoServiceResponse.PhotoServiceDetailDTO serviceDetail = adminService.serviceDetailByAdmin(id);

        return ResponseEntity.ok(new ApiUtil<>(serviceDetail));
    }


    @PostMapping("/photo/services/{userId}")
    public ResponseEntity<?> createPhotoByAdmin(@PathVariable Long userId, @Valid @RequestBody PhotoServiceInfoRequest.SaveDTO saveDTO, Errors errors,
                                                 @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin)
    {

        adminService.saveServiceByAdmin(userId, saveDTO, loginAdmin);

        return ResponseEntity.ok(new ApiUtil<>("서비스 생성이 완료 되었습니다"));
    }


    @PatchMapping("/users/{userId}/photo/services/{serviceId}")
    public ResponseEntity<?> updatePhotoByAdmin(@PathVariable Long userId,
                                                @PathVariable Long serviceId,
                                                @Valid @RequestBody PhotoServiceInfoRequest.UpdateDTO updateDTO,
                                                Errors errors,
                                                @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin) {

        adminService.updateServiceByAdmin(userId, serviceId, updateDTO,  loginAdmin);

        return ResponseEntity.ok(new ApiUtil<>("서비스 수정이 완료 되었습니다."));
    }


    @DeleteMapping("/users/{userId}/photo/services/{id}")
    public ResponseEntity<ApiUtil<String>> deletePhotoByAdmin(@PathVariable("userId") Long userId, @PathVariable("id") Long serviceId,
                                                  @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin) {

        adminService.deleteServiceByAdmin(userId, serviceId, loginAdmin);
        return ResponseEntity.ok(new ApiUtil<>("서비스 삭제가 완료 되었습니다."));
    }


    @GetMapping("/photo/mappings/list")
    public ResponseEntity<ApiUtil<List<PhotoMappingResponse.PhotoMappingListDTO>>> getMappingListByAdmin(
            @RequestParam(required = false) Long serviceId,
            @RequestParam(required = false) Long categoryId) {

        List<PhotoMappingResponse.PhotoMappingListDTO> mappingList =
                adminService.getMappingListByAdmin(serviceId, categoryId);
        return ResponseEntity.ok(new ApiUtil<>(mappingList));
    }


    @GetMapping("/photo/mappings/detail/{id}")
    public ResponseEntity<ApiUtil<PhotoMappingResponse.PhotoMappingDetailDTO>> getMappingDetailByAdmin(@PathVariable Long id) {
        PhotoMappingResponse.PhotoMappingDetailDTO mappingDetail = adminService.getMappingDetailByAdmin(id);
        return ResponseEntity.ok(new ApiUtil<>(mappingDetail));
    }


    @PostMapping("/photo/mappings/{userId}")
    public ResponseEntity<ApiUtil<String>> createMappingByAdmin(@PathVariable Long userId,
            @Valid @RequestBody PhotoMappingRequest.SaveDTO saveDTO,
            @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin) {

        adminService.createMappingByAdmin(userId, saveDTO, loginAdmin);
        return ResponseEntity.ok(new ApiUtil<>("서비스-카테고리 매핑이 생성되었습니다"));
    }


    @DeleteMapping("/photo/mappings/{serviceId}")
    public ResponseEntity<ApiUtil<String>> removeMappingByAdmin(
            @PathVariable Long serviceId,
            @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin) {

        adminService.removeMappingByAdmin(serviceId);
        return ResponseEntity.ok(new ApiUtil<>("서비스-카테고리 매핑이 삭제되었습니다"));
    }


    @GetMapping("/photo/mappings/service/{serviceId}/categories")
    public ResponseEntity<ApiUtil<List<PhotoCategoryResponse.PhotoCategoryListDTO>>> getServiceCategories(@PathVariable Long serviceId) {
        List<PhotoCategoryResponse.PhotoCategoryListDTO> categories = adminService.getServiceCategoriesByAdmin(serviceId);
        return ResponseEntity.ok(new ApiUtil<>(categories));
    }


    @GetMapping("/photo/mappings/category/{categoryId}/services")
    public ResponseEntity<ApiUtil<List<PhotoServiceResponse.PhotoServiceListDTO>>> getCategoryServices(@PathVariable Long categoryId) {
        List<PhotoServiceResponse.PhotoServiceListDTO> services = adminService.getCategoryServicesByAdmin(categoryId);
        return ResponseEntity.ok(new ApiUtil<>(services));
    }


    @GetMapping("/portfolio-categories/{categoryId}")
    public ResponseEntity<?> getPortCategoryByAdmin(@PathVariable Long categoryId) {
        PortfolioCategoryResponse.DetailDTO response = adminService.getPortCategoryByAdmin(categoryId);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }


    @GetMapping("/portfolio-categories")
    public ResponseEntity<?> getActiveCategories() {
        List<PortfolioCategoryResponse.DetailDTO> response = adminService.getPortActiveCategoriesByAdmin();
        return ResponseEntity.ok(new ApiUtil<>(response));
    }


    @PostMapping("/users/{userId}/portfolios")
    public ResponseEntity<?> createPortfolioByAdmin(@PathVariable Long userId,
                                                    @Valid @RequestBody PortfolioRequest.CreateDTO createRequest,
                                                    @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin) {
                PortfolioResponse.DetailDTO response = adminService.createPortfolioByAdmin(userId, createRequest);
        URI location = URI.create(String.format("/api/portfolios/%d", response.getPortfolioId()));
        return ResponseEntity.created(location).body(new ApiUtil<>(response));
    }


    @GetMapping("/portfolios/{portfolioId}")
    public ResponseEntity<?> getPortfolioDetailByAdmin(@PathVariable Long portfolioId) {
        PortfolioResponse.DetailDTO response = adminService.getPortfolioDetailByAdmin(portfolioId);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }


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


    @PutMapping("/users/{userId}/portfolios/{portfolioId}")
    public ResponseEntity<?> updatePortfolio(@PathVariable Long portfolioId,
                                             @Valid @RequestBody PortfolioRequest.UpdateDTO updateRequest,
                                             @PathVariable Long userId,
                                             @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin) {
        PortfolioResponse.DetailDTO response = adminService.updatePortfolioByAdmin( userId, portfolioId, updateRequest);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }


    @DeleteMapping("/user/{userId}/portfolios/{portfolioId}")
    public ResponseEntity<?> deletePortfolio(@PathVariable Long userId, @PathVariable Long portfolioId,
                                             @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin) {

       adminService.deletePortfolioByAdmin(userId, portfolioId);
        return ResponseEntity.noContent().build();
    }


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


    @GetMapping("/portfolios/photographer/{photographerId}")
    public ResponseEntity<?> getPhotographerPortfoliosByAdmin(@PathVariable Long photographerId) {
        List<PortfolioResponse.ListDTO> response = adminService.getPhotographerPortfoliosByAdmin(photographerId);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }


    @PostMapping("users/{userId}/portfolios/{portfolioId}/images")
    public ResponseEntity<?> addImageToPortfolioByAdmin(@PathVariable Long userId, @PathVariable Long portfolioId,
                                                 @Valid @RequestBody PortfolioRequest.AddImageDTO addImageRequest,
                                                 @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin) {

        addImageRequest.setPortfolioId(portfolioId);
        PortfolioResponse.ImageDTO response = adminService.addImagePortByAdmin(userId, addImageRequest, loginAdmin);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }


    @DeleteMapping("/users/{userId}/portfolios/images/{imageId}")
    public ResponseEntity<?> deleteImageByAdmin(@PathVariable Long userId, @PathVariable Long imageId,
                                         @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin) {

        adminService.deletePortImageByAdmin(userId, imageId, loginAdmin);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("users/{userId}/portfolios/{portfolioId}/categories/{categoryId}")
    public ResponseEntity<?> addCategoryToPortfolioByAdmin(@PathVariable Long userId,
                                                    @PathVariable Long portfolioId,
                                                    @PathVariable Long categoryId,
                                                    @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin) {

        adminService.addCategoryToPortfolioByAdmin(userId, portfolioId, categoryId);
        return ResponseEntity.ok(new ApiUtil<>("카테고리가 추가되었습니다."));
    }


    // [예약취소] 단일 조회
    @GetMapping("/users/{userId}/booking-cancel-infos/{bookingCancelInfoId}")
    public ResponseEntity<?> getBookingCancelInfo(@PathVariable Long userId, @PathVariable Long bookingCancelInfoId,
                                                  @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin) {
        BookingCancelInfoResponse.BookingCancelInfoGetResponse response =
                adminService.getBookingCancelInfoByAdmin(userId, bookingCancelInfoId, loginAdmin);
        return ResponseEntity.ok(response);
    }


    // 예약 관련

    @GetMapping("/booking-infos/users/{userId}/list")
    public ResponseEntity<?> getUserBookingsByAdmin(
            @PathVariable Long userId,
            @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin) {

        log.info("=== 관리자 유저 예약 목록 조회 API 호출 ===");
        log.info("Admin ID: {}", loginAdmin.getAdminId());
        log.info("조회 대상 User ID: {}", userId);

        List<BookingInfoResponse.BookingUserListDTO> result =
                adminService.getUserBookingsByAdmin(userId, loginAdmin);
        log.info("조회된 예약 개수: {}", result != null ? result.size() : 0);

        if (result != null && !result.isEmpty()) {
            log.info("첫 번째 예약 정보: {}", result.get(0));
        } else {
            log.info("예약 데이터가 비어있습니다.");
        }

        return ResponseEntity.ok(new ApiUtil<>(result));
    }

    @GetMapping("/booking-infos/photographers/{userId}/list")
    public ResponseEntity<?> getPhotographerBookingsByAdmin(
            @PathVariable Long userId,
            @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin) {

        log.info("=== 관리자 포토그래퍼 예약 목록 조회 API 호출 ===");
        log.info("Admin ID: {}", loginAdmin.getAdminId());
        log.info("조회 대상 Photographer ID: {}", userId);

        List<BookingInfoResponse.BookingPhotographerListDTO> result = adminService.getPhotographerBookingsByAdmin(userId, loginAdmin);
        log.info("조회된 예약 개수: {}", result != null ? result.size() : 0);

        return ResponseEntity.ok(new ApiUtil<>(result));
    }

    @GetMapping("/booking-infos/users/{userId}/photographers/{photographerId}/{bookingInfoId}")
    public ResponseEntity<?> getBookingDetailByAdmin(
            @PathVariable Long userId,
            @PathVariable Long photographerId,
            @PathVariable Long bookingInfoId,
            @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin) {

        log.info("=== 관리자 예약 상세 조회 API 호출 ===");
        log.info("Admin ID: {}, Booking ID: {}, User ID: {}, Photographer ID: {}",
                loginAdmin.getAdminId(), bookingInfoId, userId, photographerId);

        BookingInfoResponse.BookingDetailDTO result = adminService.getBookingDetailByAdmin(userId, photographerId, bookingInfoId, loginAdmin);
        log.info("조회된 예약 상세: {}", result);

        return ResponseEntity.ok(new ApiUtil<>(result));
    }

    // 예약 생성 (포토그래퍼가 사용자에게 제안)
    @PostMapping("/booking-infos/users/{userId}/photographers/{photographerId}")
    public ResponseEntity<?> createBooking(@PathVariable(name = "userId") Long userId,
                                           @PathVariable(name = "photographerId") Long photographerId,
                                           @Valid @RequestBody BookingInfoRequest.CreateDTO createDTO,
                                           @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin){
        log.info("=== createBooking API 호출됨 ===");
        log.info("Login User ID: " + loginAdmin.getAdminId());
        log.info("Create DTO: " + createDTO);

        adminService.createBookingByAdmin(createDTO,loginAdmin);
        return ResponseEntity.ok(new ApiUtil<>("예약 생성이 완료되었습니다."));
    }


    // [포토그래퍼] 예약 확정
    @PatchMapping("/{bookingInfoId}/confirm")
    public ResponseEntity<?> confirmBookingByAdmin(@RequestBody @Valid AdminRequest.ConfirmBookingDTO request,
                                                   @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin){


        adminService.confirmBookingByAdmin(request, loginAdmin);
        return ResponseEntity.ok(new ApiUtil<>("예약이 확정되었습니다."));
    }

    // [포토그래퍼] 촬영 완료 처리
    @PatchMapping("/{bookingInfoId}/complete")
    public ResponseEntity<?> completeBooking(@RequestBody @Valid AdminRequest.ConfirmBookingDTO request,
                                             @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin){

        Long bookingInfoId = request.getBookingInfoId();

        System.out.println("=== completeBooking API 호출됨 ===");
        System.out.println("Booking ID: " + bookingInfoId);
        System.out.println("Login User ID: " + loginAdmin.getAdminId());

        adminService.completeBookingByAdmin(request, loginAdmin);
        return ResponseEntity.ok(new ApiUtil<>("촬영 완료로 처리되었습니다."));
    }

    // [사용자] 예약 취소
    @PatchMapping("/{bookingInfoId}/cancel")
    public ResponseEntity<?> cancelBooking(@RequestBody @Valid AdminRequest.ConfirmBookingDTO request,
                                           @RequestAttribute(value = Define.LOGIN_ADMIN) LoginAdmin loginAdmin){
        Long bookingInfoId = request.getBookingInfoId();

        System.out.println("=== cancelBooking API 호출됨 ===");
        System.out.println("Booking ID: " + bookingInfoId);
        System.out.println("Login Admin ID: " + loginAdmin.getAdminId());

        adminService.cancelBookingByAdmin(request, loginAdmin);
        return ResponseEntity.ok(new ApiUtil<>("예약이 취소되었습니다."));
    }


}
