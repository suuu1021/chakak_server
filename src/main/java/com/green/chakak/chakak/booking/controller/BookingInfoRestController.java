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
        System.out.println("=== getUserBookings API 호출됨 ===");
        System.out.println("Login User ID: " + loginUser.getId());
        System.out.println("User Type Code: " + loginUser.getUserTypeName());
        System.out.println("User Email: " + loginUser.getEmail());

        var result = bookingInfoService.getUserBookings(loginUser);
        System.out.println("서비스에서 반환된 결과 개수: " + (result != null ? result.size() : "null"));

        if (result != null && !result.isEmpty()) {
            System.out.println("첫 번째 예약 정보: " + result.get(0));
        } else {
            System.out.println("예약 데이터가 비어있거나 null입니다.");
        }

        return ResponseEntity.ok(new ApiUtil<>(result));
    }

    // [포토그래퍼] 나의 예약 목록 조회
    @GetMapping("/photographer/my-list")
    public ResponseEntity<?> getPhotographerBookings(@RequestAttribute(value = Define.LOGIN_USER) LoginUser loginUser){
        System.out.println("=== getPhotographerBookings API 호출됨 ===");
        System.out.println("Login User ID: " + loginUser.getId());
        System.out.println("User Type Code: " + loginUser.getUserTypeName());

        var result = bookingInfoService.getPhotographerBookings(loginUser);
        System.out.println("서비스에서 반환된 결과 개수: " + (result != null ? result.size() : "null"));

        return ResponseEntity.ok(new ApiUtil<>(result));
    }

    // 예약 상세 조회
    @GetMapping("/{bookingInfoId}")
    public ResponseEntity<?> getBookingDetail(@PathVariable(name = "bookingInfoId")Long bookingInfoId,
                                              @RequestAttribute(value = Define.LOGIN_USER)LoginUser loginUser){
        System.out.println("=== getBookingDetail API 호출됨 ===");
        System.out.println("Booking ID: " + bookingInfoId);
        System.out.println("Login User ID: " + loginUser.getId());

        return ResponseEntity.ok(new ApiUtil<>(bookingInfoService.getBookingDetail(loginUser,bookingInfoId)));
    }

    // 예약 생성 (포토그래퍼가 사용자에게 제안)
    @PostMapping
    public ResponseEntity<?> createBooking(@Valid @RequestBody BookingInfoRequest.CreateDTO createDTO,
                                           @RequestAttribute(value = Define.LOGIN_USER) LoginUser loginUser){
        System.out.println("=== createBooking API 호출됨 ===");
        System.out.println("Login User ID: " + loginUser.getId());
        System.out.println("Create DTO: " + createDTO);

        bookingInfoService.createBooking(createDTO,loginUser);
        return ResponseEntity.ok(new ApiUtil<>("예약 생성이 완료되었습니다."));
    }

    // [포토그래퍼] 예약 확정
    @PatchMapping("/{bookingInfoId}/confirm")
    public ResponseEntity<?> confirmBooking(@PathVariable(name = "bookingInfoId") Long bookingInfoId,
                                            @RequestAttribute(Define.LOGIN_USER) LoginUser loginUser){
        System.out.println("=== confirmBooking API 호출됨 ===");
        System.out.println("Booking ID: " + bookingInfoId);
        System.out.println("Login User ID: " + loginUser.getId());

        bookingInfoService.confirmBooking(bookingInfoId, loginUser);
        return ResponseEntity.ok(new ApiUtil<>("예약이 확정되었습니다."));
    }

    // [포토그래퍼] 촬영 완료 처리
    @PatchMapping("/{bookingInfoId}/complete")
    public ResponseEntity<?> completeBooking(@PathVariable(name = "bookingInfoId") Long bookingInfoId,
                                             @RequestAttribute(Define.LOGIN_USER) LoginUser loginUser){
        System.out.println("=== completeBooking API 호출됨 ===");
        System.out.println("Booking ID: " + bookingInfoId);
        System.out.println("Login User ID: " + loginUser.getId());

        bookingInfoService.completeBooking(bookingInfoId, loginUser);
        return ResponseEntity.ok(new ApiUtil<>("촬영 완료로 처리되었습니다."));
    }

    // [사용자] 예약 취소
    @PatchMapping("/{bookingInfoId}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable(name = "bookingInfoId") Long bookingInfoId,
                                           @RequestAttribute(Define.LOGIN_USER) LoginUser loginUser){
        System.out.println("=== cancelBooking API 호출됨 ===");
        System.out.println("Booking ID: " + bookingInfoId);
        System.out.println("Login User ID: " + loginUser.getId());

        bookingInfoService.cancelBooking(bookingInfoId, loginUser);
        return ResponseEntity.ok(new ApiUtil<>("예약이 취소되었습니다."));
    }
}