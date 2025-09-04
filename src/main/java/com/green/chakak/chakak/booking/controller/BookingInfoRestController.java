package com.green.chakak.chakak.booking.controller;

import com.green.chakak.chakak.account.domain.LoginUser;
import com.green.chakak.chakak.booking.service.request.BookingInfoRequest;
import com.green.chakak.chakak.booking.service.BookingInfoService;
import com.green.chakak.chakak.global.utils.ApiUtil;
import com.green.chakak.chakak.global.utils.Define;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class BookingInfoRestController {

    private final BookingInfoService bookingInfoService;

    // [사용자] 나의 예약 목록
    @GetMapping("/booking/{userId}/list")
    public ResponseEntity<?> userBookingList(@PathVariable(name = "userId")Long userId,
                                             @RequestAttribute(value = Define.LOGIN_USER) LoginUser loginUser){
        return ResponseEntity.ok(bookingInfoService.bookingUserListDTO(loginUser,userId));
    }

    // [포토그래퍼] 나의 예약 목록
    @GetMapping("/booking/photographer/{userId}/list")
    public ResponseEntity<?> photographerBookingList(@PathVariable(name = "userId")Long userId,
                                             @RequestAttribute(value = Define.LOGIN_USER) LoginUser loginUser){
        return ResponseEntity.ok(bookingInfoService.bookingPhotographerListDTO(loginUser,userId));
    }

    // [세부사항] 예약 사항 세부목록
    @GetMapping("/booking/{bookingInfoId}/detail")
    public ResponseEntity<?> bookingDetail(@PathVariable(name = "bookingInfoId")Long bookingInfoId,
                                           @RequestAttribute(value = Define.LOGIN_USER)LoginUser loginUser){
        return ResponseEntity.ok(bookingInfoService.findByDetailList(loginUser,bookingInfoId));
    }

    // 예약하기
    @PostMapping("/booking/save")
    public ResponseEntity<?> saveBooking(@Valid @RequestBody BookingInfoRequest.CreateDTO createDTO, Errors errors,
                                         @RequestAttribute(value = Define.LOGIN_USER) LoginUser loginUser){
        bookingInfoService.save(createDTO,loginUser);
        return ResponseEntity.ok(new ApiUtil<>("서비스 생성이 완료 되었습니다"));
    }

    // 예약 확정 하기(포토그래퍼)
    @PutMapping("booking/{bookingInfoId}/photographer-confirm")
    public ResponseEntity<?> photographerConfirm(@PathVariable(name = "bookingInfoId") Long bookingInfoId,
                                        @RequestAttribute(Define.LOGIN_USER) LoginUser loginUser){
        bookingInfoService.userCancelStatus(bookingInfoId, "예약승낙", loginUser);
        return ResponseEntity.ok(new ApiUtil<>("예약 승낙 처리가 완료 되었습니다"));
    }
    // 예약 거절 하기(포토그래퍼)
    @PutMapping("booking/{bookingInfoId}/photographer-cancel")
    public ResponseEntity<?> photographerCancel(@PathVariable(name = "bookingInfoId") Long bookingInfoId,
                                        @RequestAttribute(Define.LOGIN_USER) LoginUser loginUser){
        bookingInfoService.userCancelStatus(bookingInfoId, "예약거절", loginUser);
        return ResponseEntity.ok(new ApiUtil<>("예약 거절 처리가 완료 되었습니다"));
    }

    // 촬영 완료 하기
    @PutMapping("booking/{bookingInfoId}/photographer-service-end")
    public ResponseEntity<?> photographerServiceEnd(@PathVariable(name = "bookingInfoId") Long bookingInfoId,
                                                @RequestAttribute(Define.LOGIN_USER) LoginUser loginUser){
        bookingInfoService.userCancelStatus(bookingInfoId, "촬영완료", loginUser);
        return ResponseEntity.ok(new ApiUtil<>("촬영 완료 처리가 완료 되었습니다"));
    }


    // 예약 취소하기(유저)
    @PutMapping("booking/{bookingInfoId}/user-cancel")
    public ResponseEntity<?> userCancel(@PathVariable(name = "bookingInfoId") Long bookingInfoId,
                                        @RequestAttribute(Define.LOGIN_USER) LoginUser loginUser){
        bookingInfoService.userCancelStatus(bookingInfoId, "예약취소", loginUser);
        return ResponseEntity.ok(new ApiUtil<>("예약 취소 처리가 완료 되었습니다"));
    }



}
