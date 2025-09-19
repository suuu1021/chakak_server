package com.green.chakak.chakak.booking.controller;

import com.green.chakak.chakak.account.domain.LoginUser;
import com.green.chakak.chakak.booking.service.request.BookingInfoRequest;
import com.green.chakak.chakak.booking.service.BookingInfoService;
import com.green.chakak.chakak._global.utils.ApiUtil;
import com.green.chakak.chakak._global.utils.Define;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bookings")
public class BookingInfoRestController {

    private final BookingInfoService bookingInfoService;

    // [사용자] 나의 예약 목록 조회
    @GetMapping("/user/my-list")
    public ResponseEntity<?> getUserBookings(@RequestAttribute(value = Define.LOGIN_USER) LoginUser loginUser){
        return ResponseEntity.ok(new ApiUtil<>(bookingInfoService.getUserBookings(loginUser)));
    }

    // [포토그래퍼] 나의 예약 목록 조회
    @GetMapping("/photographer/my-list")
    public ResponseEntity<?> getPhotographerBookings(@RequestAttribute(value = Define.LOGIN_USER) LoginUser loginUser){
        return ResponseEntity.ok(new ApiUtil<>(bookingInfoService.getPhotographerBookings(loginUser)));
    }

    // 예약 상세 조회
    @GetMapping("/{bookingInfoId}")
    public ResponseEntity<?> getBookingDetail(@PathVariable(name = "bookingInfoId")Long bookingInfoId,
                                           @RequestAttribute(value = Define.LOGIN_USER)LoginUser loginUser){
        return ResponseEntity.ok(new ApiUtil<>(bookingInfoService.getBookingDetail(loginUser,bookingInfoId)));
    }

    // 예약 생성 (포토그래퍼가 사용자에게 제안)
    @PostMapping
    public ResponseEntity<?> createBooking(@Valid @RequestBody BookingInfoRequest.CreateDTO createDTO,
                                         @RequestAttribute(value = Define.LOGIN_USER) LoginUser loginUser){
        bookingInfoService.createBooking(createDTO,loginUser);
        return ResponseEntity.ok(new ApiUtil<>("예약 생성이 완료되었습니다."));
    }

    // [포토그래퍼] 예약 확정
    @PatchMapping("/{bookingInfoId}/confirm")
    public ResponseEntity<?> confirmBooking(@PathVariable(name = "bookingInfoId") Long bookingInfoId,
                                        @RequestAttribute(Define.LOGIN_USER) LoginUser loginUser){
        bookingInfoService.confirmBooking(bookingInfoId, loginUser);
        return ResponseEntity.ok(new ApiUtil<>("예약이 확정되었습니다."));
    }

    // [포토그래퍼] 촬영 완료 처리
    @PatchMapping("/{bookingInfoId}/complete")
    public ResponseEntity<?> completeBooking(@PathVariable(name = "bookingInfoId") Long bookingInfoId,
                                                @RequestAttribute(Define.LOGIN_USER) LoginUser loginUser){
        bookingInfoService.completeBooking(bookingInfoId, loginUser);
        return ResponseEntity.ok(new ApiUtil<>("촬영 완료로 처리되었습니다."));
    }

    // [사용자] 예약 취소
    @PatchMapping("/{bookingInfoId}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable(name = "bookingInfoId") Long bookingInfoId,
                                        @RequestAttribute(Define.LOGIN_USER) LoginUser loginUser){
        bookingInfoService.cancelBooking(bookingInfoId, loginUser);
        return ResponseEntity.ok(new ApiUtil<>("예약이 취소되었습니다."));
    }
}
