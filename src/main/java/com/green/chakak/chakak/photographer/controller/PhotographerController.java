package com.green.chakak.chakak.photographer.controller;

import com.green.chakak.chakak.account.user.User;
import com.green.chakak.chakak.global.utils.Define;
import com.green.chakak.chakak.photographer.service.PhotographerService;
import com.green.chakak.chakak.photographer.service.request.PhotographerRequest;
import com.green.chakak.chakak.photographer.service.response.PhotographerResponse;
import com.green.chakak.chakak.global.utils.ApiUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/photographers")
public class PhotographerController {

    private final PhotographerService photographerService;

    // 포토그래퍼 가입
    @PostMapping
    public ResponseEntity<?> joinAsPhotographer(
            @RequestAttribute(Define.LOGIN_USER) User user,
            @Valid @RequestBody PhotographerRequest.SaveProfile saveProfile
    ) {
        PhotographerResponse.SaveDTO response = photographerService.joinAsPhotographer(user, saveProfile);
        URI location = URI.create(String.format("/api/photographers/%d", response.getPhotographerId()));
        return ResponseEntity.created(location).body(new ApiUtil<>(response));
    }

    // 포토그래퍼 프로필 상세 조회
    @GetMapping("/{photographerId}")
    public ResponseEntity<?> getPhotographerDetail(@PathVariable Long photographerId) {
        PhotographerResponse.DetailDTO response = photographerService.getPhotographerDetail(photographerId);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    // 활성 포토그래퍼 프로필 목록
    @GetMapping
    public ResponseEntity<?> getActivePhotographers() {
        List<PhotographerResponse.ListDTO> response = photographerService.getActivePhotographers();
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    // 포토그래퍼 프로필 정보 수정
    @PutMapping("/{photographerId}")
    public ResponseEntity<?> updatePhotographer(@PathVariable Long photographerId,
                                                @Valid @RequestBody PhotographerRequest.UpdateProfile updateProfile) {
        PhotographerResponse.UpdateDTO response = photographerService.updatePhotographer(photographerId, updateProfile);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    // 프로필 활성화
    @PatchMapping("/{photographerId}/activate")
    public ResponseEntity<?> activatePhotographer(@PathVariable Long photographerId) {
        PhotographerResponse.UpdateDTO response = photographerService.activatePhotographer(photographerId);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    // 프로필 비활성화
    @PatchMapping("/{photographerId}/deactivate")
    public ResponseEntity<?> deactivatePhotographer(@PathVariable Long photographerId) {
        PhotographerResponse.UpdateDTO response = photographerService.deactivatePhotographer(photographerId);
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

    // 사용자 ID로 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getPhotographerByUserId(@PathVariable Long userId) {
        Optional<PhotographerResponse.DetailDTO> response = photographerService.getPhotographerByUserId(userId);
        return response.map(dto -> ResponseEntity.ok(new ApiUtil<>(dto)))
                .orElse(ResponseEntity.notFound().build());
    }

    // 포토그래퍼 프로필 삭제
    @DeleteMapping("/{photographerId}")
    public ResponseEntity<?> removePhotographer(@PathVariable Long photographerId) {
        photographerService.removePhotographer(photographerId);
        return ResponseEntity.noContent().build();
    }
}
