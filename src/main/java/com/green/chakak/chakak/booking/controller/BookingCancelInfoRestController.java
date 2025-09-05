package com.green.chakak.chakak.booking.controller;

import com.green.chakak.chakak.account.domain.LoginUser;
import com.green.chakak.chakak._global.utils.JwtUtil;
import com.green.chakak.chakak.booking.service.BookingCancelInfoService;
import com.green.chakak.chakak.booking.service.request.BookingCancelInfoRequest;
import com.green.chakak.chakak.booking.service.response.BookingCancelInfoResponse;
import com.green.chakak.chakak._global.utils.PageUtil.PageResponse;
import jakarta.servlet.http.HttpServletRequest;
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

    // 단일 조회 (로그인 유저 권한 확인 추가)
    @GetMapping("/{bookingCancelInfoId}")
    public ResponseEntity<?> getBookingCancelInfo(@PathVariable Long bookingCancelInfoId, HttpServletRequest request) {
        String jwt = JwtUtil.resolveToken(request.getHeader("Authorization"));
        LoginUser loginUser = JwtUtil.verify(jwt);

        BookingCancelInfoResponse.BookingCancelInfoGetResponse response =
                bookingCancelInfoService.getBookingCancelInfo(bookingCancelInfoId, loginUser);
        return ResponseEntity.ok(response);
    }

    // 내 예약 취소 내역 페이징 조회
    @GetMapping("/my/paged")
    public ResponseEntity<PageResponse<BookingCancelInfoResponse.BookingCancelInfoListResponse>> getMyCancelInfoList(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        String jwt = JwtUtil.resolveToken(request.getHeader("Authorization"));
        LoginUser loginUser = JwtUtil.verify(jwt);
        Pageable pageable = PageRequest.of(page, size);

        PageResponse<BookingCancelInfoResponse.BookingCancelInfoListResponse> response =
                bookingCancelInfoService.getMyCancelInfoList(loginUser, pageable);
        return ResponseEntity.ok(response);
    }

    //통합형 예약 취소 내역 페이징 조회 (관리자용)
    @GetMapping("/paged")
    public ResponseEntity<PageResponse<BookingCancelInfoResponse.BookingCancelInfoListResponse>> getPagedCancelInfoList(BookingCancelInfoRequest.BookingCancelInfoListRequest req) {
        PageResponse<BookingCancelInfoResponse.BookingCancelInfoListResponse> response =
                bookingCancelInfoService.getPagedCancelInfoList(req);
        return ResponseEntity.ok(response);
    }
}
