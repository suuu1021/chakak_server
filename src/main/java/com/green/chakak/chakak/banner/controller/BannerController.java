package com.green.chakak.chakak.banner.controller;

import com.green.chakak.chakak._global.utils.ApiUtil;
import com.green.chakak.chakak._global.utils.Define;
import com.green.chakak.chakak.admin.domain.LoginAdmin;
import com.green.chakak.chakak.banner.service.BannerService;
import com.green.chakak.chakak.banner.service.request.BannerRequest;
import com.green.chakak.chakak.banner.service.response.BannerResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/banners")
public class BannerController {

    private final BannerService bannerService;


    @GetMapping("/active")
    public ResponseEntity<?> getActiveBanners(@RequestAttribute(value = Define.LOGIN_ADMIN, required = false) LoginAdmin loginAdmin) {

        if (loginAdmin == null) {
            loginAdmin = LoginAdmin.builder()
                    .adminId(0L)
                    .adminName("testAdmin")
                    .userTypeName("admin")
                    .build();
        }
        List<BannerResponse.BannerListDTO> banners = bannerService.getActiveBanners();
        return ResponseEntity.ok(new ApiUtil<>(banners));
    }


    @GetMapping("/list")
    public ResponseEntity<?> getBannerList(@RequestParam(required = false) String keyword,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size,
                                           @RequestAttribute(value = Define.LOGIN_ADMIN, required = false) LoginAdmin loginAdmin) {

        if (loginAdmin == null) {
            loginAdmin = LoginAdmin.builder()
                    .adminId(0L)
                    .adminName("testAdmin")
                    .userTypeName("admin")
                    .build();
        }
        List<BannerResponse.BannerListDTO> banners = bannerService.getBannerList(page, size, keyword);
        return ResponseEntity.ok(new ApiUtil<>(banners));
    }


    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getBannerDetail(@PathVariable Long id,
                                             @RequestAttribute(value = Define.LOGIN_ADMIN, required = false) LoginAdmin loginAdmin) {

        if (loginAdmin == null) {
            loginAdmin = LoginAdmin.builder()
                    .adminId(0L)
                    .adminName("testAdmin")
                    .userTypeName("admin")
                    .build();
        }
        BannerResponse.BannerDetailDTO banner = bannerService.getBannerDetail(id);
        return ResponseEntity.ok(new ApiUtil<>(banner));
    }


    @PostMapping("")
    public ResponseEntity<?> createBanner(@Valid @RequestBody BannerRequest.CreateDTO createDTO,
                                          Errors errors,
                                          @RequestAttribute(value = Define.LOGIN_ADMIN, required = false) LoginAdmin loginAdmin) {

        if (loginAdmin == null) {
            loginAdmin = LoginAdmin.builder()
                    .adminId(0L)
                    .adminName("testAdmin")
                    .userTypeName("admin")
                    .build();
        }
        bannerService.saveBanner(createDTO);
        return ResponseEntity.ok(new ApiUtil<>("배너 생성이 완료되었습니다."));
    }


    @PatchMapping("/{id}")
    public ResponseEntity<?> updateBanner(@PathVariable Long id,
                                          @Valid @RequestBody BannerRequest.UpdateDTO updateDTO,
                                          Errors errors,
                                          @RequestAttribute(value = Define.LOGIN_ADMIN, required = false) LoginAdmin loginAdmin) {

        if (loginAdmin == null) {
            loginAdmin = LoginAdmin.builder()
                    .adminId(0L)
                    .adminName("testAdmin")
                    .userTypeName("admin")
                    .build();
        }
        bannerService.updateBanner(id, updateDTO);
        return ResponseEntity.ok(new ApiUtil<>("배너 수정이 완료되었습니다."));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ApiUtil<String>> deleteBanner(@PathVariable Long id,
                                                        @RequestAttribute(value = Define.LOGIN_ADMIN, required = false) LoginAdmin loginAdmin) {

        if (loginAdmin == null) {
            loginAdmin = LoginAdmin.builder()
                    .adminId(0L)
                    .adminName("testAdmin")
                    .userTypeName("admin")
                    .build();
        }


        bannerService.deleteBanner(id);
        return ResponseEntity.ok(new ApiUtil<>("배너 삭제가 완료되었습니다."));
    }


    @PatchMapping("/{id}/toggle")
    public ResponseEntity<?> toggleBannerStatus(@PathVariable Long id,
                                                @RequestAttribute(value = Define.LOGIN_ADMIN, required = false) LoginAdmin loginAdmin) {

        if (loginAdmin == null) {
            loginAdmin = LoginAdmin.builder()
                    .adminId(0L)
                    .adminName("testAdmin")
                    .userTypeName("admin")
                    .build();
        }
        BannerResponse.BannerDetailDTO updatedBanner = bannerService.toggleBannerStatus(id);
        return ResponseEntity.ok(new ApiUtil<>(updatedBanner));
    }


    @PatchMapping("/{id}/order")
    public ResponseEntity<?> updateDisplayOrder(@PathVariable Long id,
                                                @RequestParam Integer order,
                                                @RequestAttribute(value = Define.LOGIN_ADMIN, required = false) LoginAdmin loginAdmin) {

        if (loginAdmin == null) {
            loginAdmin = LoginAdmin.builder()
                    .adminId(0L)
                    .adminName("testAdmin")
                    .userTypeName("admin")
                    .build();
        }
        bannerService.updateDisplayOrder(id, order);
        return ResponseEntity.ok(new ApiUtil<>("표시 순서가 변경되었습니다."));
    }

}