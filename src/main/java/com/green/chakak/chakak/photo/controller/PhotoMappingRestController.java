package com.green.chakak.chakak.photo.controller;

import com.green.chakak.chakak.account.domain.LoginUser;
import com.green.chakak.chakak.global.utils.ApiUtil;
import com.green.chakak.chakak.global.utils.Define;
import com.green.chakak.chakak.photo.service.PhotoService;
import com.green.chakak.chakak.photo.service.request.PhotoMappingRequest;
import com.green.chakak.chakak.photo.service.response.PhotoCategoryResponse;
import com.green.chakak.chakak.photo.service.response.PhotoMappingResponse;
import com.green.chakak.chakak.photo.service.response.PhotoServiceResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/photo/mappings")
public class PhotoMappingRestController {

    private final PhotoService photoService;

    // 매핑 목록 조회
    @GetMapping("")
    public ResponseEntity<ApiUtil<List<PhotoMappingResponse.PhotoMappingListDTO>>> getMappingList(
            @RequestParam(required = false) Long serviceId,
            @RequestParam(required = false) Long categoryId) {

        List<PhotoMappingResponse.PhotoMappingListDTO> mappingList =
                photoService.getMappingList(serviceId, categoryId);
        return ResponseEntity.ok(new ApiUtil<>(mappingList));
    }

    // 매핑 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiUtil<PhotoMappingResponse.PhotoMappingDetailDTO>> getMappingDetail(@PathVariable Long id) {
        PhotoMappingResponse.PhotoMappingDetailDTO mappingDetail = photoService.getMappingDetail(id);
        return ResponseEntity.ok(new ApiUtil<>(mappingDetail));
    }

    // 매핑 생성
    @PostMapping("")
    public ResponseEntity<ApiUtil<String>> createMapping(
            @Valid @RequestBody PhotoMappingRequest.SaveDTO saveDTO,
            @RequestAttribute(Define.LOGIN_USER) LoginUser loginUser) {

        photoService.createMapping(saveDTO, loginUser);
        return ResponseEntity.ok(new ApiUtil<>("서비스-카테고리 매핑이 생성되었습니다"));
    }

    // 매핑 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiUtil<String>> removeMapping(
            @PathVariable Long id,
            @RequestAttribute(Define.LOGIN_USER) LoginUser loginUser) {

        photoService.removeMapping(id, loginUser);
        return ResponseEntity.ok(new ApiUtil<>("서비스-카테고리 매핑이 삭제되었습니다"));
    }

    // 특정 서비스의 카테고리들 조회
    @GetMapping("/service/{serviceId}/categories")
    public ResponseEntity<ApiUtil<List<PhotoCategoryResponse.PhotoCategoryListDTO>>> getServiceCategories(@PathVariable Long serviceId) {
        List<PhotoCategoryResponse.PhotoCategoryListDTO> categories = photoService.getServiceCategories(serviceId);
        return ResponseEntity.ok(new ApiUtil<>(categories));
    }

    // 특정 카테고리의 서비스들 조회
    @GetMapping("/category/{categoryId}/services")
    public ResponseEntity<ApiUtil<List<PhotoServiceResponse.PhotoServiceListDTO>>> getCategoryServices(@PathVariable Long categoryId) {
        List<PhotoServiceResponse.PhotoServiceListDTO> services = photoService.getCategoryServices(categoryId);
        return ResponseEntity.ok(new ApiUtil<>(services));
    }
}
