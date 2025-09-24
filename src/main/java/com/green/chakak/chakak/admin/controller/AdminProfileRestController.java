package com.green.chakak.chakak.admin.controller;

import com.green.chakak.chakak.admin.service.AdminProfileService;
import com.green.chakak.chakak.admin.service.request.AdminProfileRequest;
import com.green.chakak.chakak.admin.service.response.AdminProfileResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/profile")
@RequiredArgsConstructor
@Slf4j
public class AdminProfileRestController {

    private final AdminProfileService adminProfileService;


    @PostMapping("/{adminId}")
    public ResponseEntity<AdminProfileResponse.DetailDTO> createAdminProfile(
            @PathVariable Long adminId,
            @Valid @RequestBody AdminProfileRequest.CreateDTO dto) {

        log.info("관리자 프로필 생성 요청 - adminId: {}", adminId);

        try {
            AdminProfileResponse.DetailDTO response = adminProfileService.createAdminProfile(adminId, dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            log.error("관리자 프로필 생성 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }


    @GetMapping("/{adminId}")
    public ResponseEntity<AdminProfileResponse.DetailDTO> getAdminProfile(@PathVariable Long adminId) {

        log.info("관리자 프로필 조회 요청 - adminId: {}", adminId);

        try {
            AdminProfileResponse.DetailDTO response = adminProfileService.getAdminProfile(adminId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("관리자 프로필 조회 실패: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }


    @PutMapping("/{adminId}")
    public ResponseEntity<AdminProfileResponse.UpdateDTO> updateAdminProfile(
            @PathVariable Long adminId,
            @Valid @RequestBody AdminProfileRequest.UpdateDTO dto) {

        log.info("관리자 프로필 수정 요청 - adminId: {}", adminId);

        try {
            AdminProfileResponse.UpdateDTO response = adminProfileService.updateAdminProfile(adminId, dto);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("관리자 프로필 수정 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }


    @DeleteMapping("/{adminId}")
    public ResponseEntity<Void> deleteAdminProfile(@PathVariable Long adminId) {

        log.info("관리자 프로필 삭제 요청 - adminId: {}", adminId);

        try {
            adminProfileService.deleteAdminProfile(adminId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("관리자 프로필 삭제 실패: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}