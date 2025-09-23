package com.green.chakak.chakak.photographer.controller;

import com.green.chakak.chakak._global.utils.ApiUtil;
import com.green.chakak.chakak._global.utils.Define;
import com.green.chakak.chakak.account.domain.LoginUser;
import com.green.chakak.chakak.photographer.service.PhotographerService;
import com.green.chakak.chakak.photographer.service.request.PhotographerCategoryRequest;
import com.green.chakak.chakak.photographer.service.request.PhotographerRequest;
import com.green.chakak.chakak.photographer.service.response.PhotographerResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/photographers")
public class PhotographerController {

    private final PhotographerService photographerService;

    // 포토그래퍼 프로필 저장
    @PostMapping("/create")
    public ResponseEntity<?> createProfile(@Valid @RequestBody PhotographerRequest.SaveProfile saveProfile
    ) {
        PhotographerResponse.SaveDTO response = photographerService.createProfile(saveProfile);
        URI location = URI.create(String.format("/api/photographers/%d", response.getPhotographerId()));
        return ResponseEntity.created(location).body(new ApiUtil<>("프로필 등록이 완료되었습니다"));
    }

    // 포토그래퍼 프로필 상세 조회
    @GetMapping("/{photographerId}")
    public ResponseEntity<?> getPhotographerDetail(@PathVariable Long photographerId) {
        PhotographerResponse.DetailDTO response = photographerService.getProfileDetail(photographerId);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    // 포토그래퍼 프로필 목록 조회
    @GetMapping
    public ResponseEntity<?> getActivePhotographers() {
        List<PhotographerResponse.ListDTO> response = photographerService.getActiveProfile();
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    // 포토그래퍼 프로필 수정
    @PutMapping("/{photographerId}/update")
    public ResponseEntity<?> updateProfile(@PathVariable Long photographerId,
                                           @Valid @RequestBody PhotographerRequest.UpdateProfile updateProfile,
                                           @RequestAttribute(Define.LOGIN_USER) LoginUser loginUser) {
        PhotographerResponse.UpdateDTO response = photographerService.updateProfile(photographerId, updateProfile, loginUser);
        return ResponseEntity.ok(new ApiUtil<>("프로필 정보 수정이 완료 되었습니다"));
    }

    // 프로필 활성화
    @PatchMapping("/{photographerId}/activate")
    public ResponseEntity<?> activateProfile(@PathVariable Long photographerId,
                                             @RequestAttribute(Define.LOGIN_USER) LoginUser loginUser) {
        PhotographerResponse.UpdateDTO response = photographerService.activatePhotographer(photographerId, loginUser);
        return ResponseEntity.ok(new ApiUtil<>("프로필 활성화가 완료되었습니다"));
    }

    // 프로필 비활성화
    @PatchMapping("/{photographerId}/deactivate")
    public ResponseEntity<?> deactivateProfile(@PathVariable Long photographerId,
                                               @RequestAttribute(Define.LOGIN_USER) LoginUser loginUser) {
        PhotographerResponse.UpdateDTO response = photographerService.deactivatePhotographer(photographerId, loginUser);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    // 지역별 조회
    @GetMapping("/location/{location}")
    public ResponseEntity<?> getPhotographersByLocation(@PathVariable String location) {
        List<PhotographerResponse.ListDTO> response = photographerService.getPhotographersByLocation(location);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    // 상호명 검색
    @GetMapping("/search")
    public ResponseEntity<?> searchByBusinessName(@RequestParam String businessName) {
        List<PhotographerResponse.ListDTO> response = photographerService.searchByBusinessName(businessName);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    // 포토그래퍼 프로필 삭제
    @DeleteMapping("/{photographerId}/delete")
    public ResponseEntity<?> removePhotographer(@PathVariable Long photographerId,
                                                @RequestAttribute(Define.LOGIN_USER) LoginUser loginUser) {
        photographerService.removePhotographer(photographerId, loginUser);
        return ResponseEntity.noContent().build();
    }

    // 포토그래퍼에게 카테고리 추가
    @PostMapping("/{photographerId}/categories")
    public ResponseEntity<?> addCategoryToPhotographer(@PathVariable Long photographerId,
                                                       @Valid @RequestBody PhotographerCategoryRequest.AddCategoryToPhotographer request,
                                                       @RequestAttribute(Define.LOGIN_USER) LoginUser loginUser) {
        PhotographerResponse.mapDTO response = photographerService.addCategoryToPhotographer(photographerId, request, loginUser);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    // 포토그래퍼에게서 카테고리 제거
    @DeleteMapping("/{photographerId}/categories/{categoryId}")
    public ResponseEntity<?> removeCategoryFromPhotographer(@PathVariable Long photographerId,
                                                            @PathVariable Long categoryId,
                                                            @RequestAttribute(Define.LOGIN_USER) LoginUser loginUser) {
        photographerService.removeCategoryFromPhotographer(photographerId, categoryId, loginUser);
        return ResponseEntity.ok(new ApiUtil<>("카테고리가 성공적으로 제거되었습니다."));
    }
    // 현재 로그인한 포토그래퍼 프로필 조회
    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(@RequestAttribute(Define.LOGIN_USER) LoginUser loginUser) {
        PhotographerResponse.DetailDTO response = photographerService.getMyProfile(loginUser);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    // PhotographerController.java에 추가
    @GetMapping("/profile/user/{userId}")
    public ResponseEntity<?> getPhotographerByUserId(@PathVariable Long userId) {
        PhotographerResponse.MappingDTO response = photographerService.getPhotographerByUserId(userId);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }
}
