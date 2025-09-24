package com.green.chakak.chakak.booking.controller;

import com.green.chakak.chakak._global.utils.Define;
import com.green.chakak.chakak.account.domain.LoginUser;
import com.green.chakak.chakak.admin.domain.LoginAdmin;
import com.green.chakak.chakak.booking.service.BookingCancelInfoService;
import com.green.chakak.chakak.booking.service.request.BookingCancelInfoRequest;
import com.green.chakak.chakak.booking.service.response.BookingCancelInfoResponse;
import com.green.chakak.chakak._global.utils.PageUtil.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/booking_cancel_info")
public class BookingCancelInfoRestController {

    private final BookingCancelInfoService bookingCancelInfoService;

    // [수정] 단일 조회 (ArgumentResolver 사용)
    @GetMapping("/{bookingCancelInfoId}")
    public ResponseEntity<?> getBookingCancelInfo(@PathVariable Long bookingCancelInfoId, LoginUser loginUser) { // LoginUser 자동 주입
        BookingCancelInfoResponse.BookingCancelInfoGetResponse response =
                bookingCancelInfoService.getBookingCancelInfo(bookingCancelInfoId, loginUser);
        return ResponseEntity.ok(response);
    }

    // [수정] 내 예약 취소 내역 페이징 조회 (ArgumentResolver 사용)
    @GetMapping("/my/paged")
    public ResponseEntity<PageResponse<BookingCancelInfoResponse.BookingCancelInfoListResponse>> getMyCancelInfoList(
            LoginUser loginUser, // LoginUser 자동 주입
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        PageResponse<BookingCancelInfoResponse.BookingCancelInfoListResponse> response =
                bookingCancelInfoService.getMyCancelInfoList(loginUser, pageable);
        return ResponseEntity.ok(response);
    }

    // [유지] 통합형 예약 취소 내역 페이징 조회 (관리자용)
    @GetMapping("/paged")
    public ResponseEntity<PageResponse<BookingCancelInfoResponse.BookingCancelInfoListResponse>> getPagedCancelInfoList(BookingCancelInfoRequest.BookingCancelInfoListRequest req,
                                                                                                                        @RequestAttribute(value = Define.LOGIN_ADMIN, required = false) LoginAdmin loginAdmin) {
        if (loginAdmin == null) {
            loginAdmin = LoginAdmin.builder()
                    .adminId(0L)
                    .adminName("testAdmin")
                    .userTypeName("admin")
                    .build();
        }
        PageResponse<BookingCancelInfoResponse.BookingCancelInfoListResponse> response =
                bookingCancelInfoService.getPagedCancelInfoList(req);
        return ResponseEntity.ok(response);
    }
}
