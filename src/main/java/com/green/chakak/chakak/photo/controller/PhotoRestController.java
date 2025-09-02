package com.green.chakak.chakak.photo.controller;

import com.green.chakak.chakak.account.user.LoginUser;
import com.green.chakak.chakak.account.user.User;
import com.green.chakak.chakak.global.errors.exception.Exception400;
import com.green.chakak.chakak.global.errors.exception.Exception500;
import com.green.chakak.chakak.global.utils.ApiUtil;
import com.green.chakak.chakak.global.utils.Define;
import com.green.chakak.chakak.photo.domain.PhotoServiceInfo;
import com.green.chakak.chakak.photo.service.PhotoService;
import com.green.chakak.chakak.photo.service.request.PhotoServiceInfoRequest;
import com.green.chakak.chakak.photo.service.response.PhotoServiceResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/photo/services")
public class PhotoRestController {

    private final PhotoService photoService;

    // 포토그래퍼 서비스 목록 조회
    @GetMapping("")
    public ResponseEntity<?> list(@RequestParam(required = false) String keyword,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size){

        List<PhotoServiceResponse.PhotoServiceListDTO> serviceList = photoService.serviceList(page,size,keyword);

        return ResponseEntity.ok(new ApiUtil<>(serviceList));

    }

    // 포토그래퍼 서비스 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id,
                                    @RequestAttribute(value = Define.LOGIN_USER, required = false) LoginUser loginUser) {

        PhotoServiceResponse.PhotoServiceDetailDTO serviceDetail = photoService.serviceDetail(id, loginUser);

        return ResponseEntity.ok(new ApiUtil<>(serviceDetail));
    }

    // 포토그래퍼 서비스 생성
    @PostMapping("")
    public ResponseEntity<?> create(@Valid @RequestBody PhotoServiceInfoRequest.SaveDTO saveDTO, Errors errors,
                                    @RequestAttribute(value = Define.LOGIN_USER) LoginUser loginUser
    ) {

        photoService.saveService(saveDTO, loginUser);

        return ResponseEntity.ok(new ApiUtil<>("서비스 생성이 완료 되었습니다"));
    }

    // 포토그래퍼 서비스 수정
    @PatchMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @Valid @RequestBody PhotoServiceInfoRequest.UpdateDTO updateDTO, Errors errors,
                                    @RequestAttribute(value = Define.LOGIN_USER) LoginUser loginUser) {

        photoService.updateService(id, updateDTO, loginUser);

        return ResponseEntity.ok(new ApiUtil<>("서비스 수정이 완료 되었습니다."));
    }

    // 포토그래퍼 서비스 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiUtil<String>> delete(@PathVariable Long id,
                                                  @RequestAttribute(Define.LOGIN_USER) LoginUser loginUser) {

        photoService.deleteService(id, loginUser);
        return ResponseEntity.ok(new ApiUtil<>("서비스 삭제가 완료 되었습니다."));
    }
}
