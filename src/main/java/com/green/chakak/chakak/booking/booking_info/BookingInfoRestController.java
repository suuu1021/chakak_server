package com.green.chakak.chakak.booking.booking_info;

import com.green.chakak.chakak.account.user.LoginUser;
import com.green.chakak.chakak.global.utils.ApiUtil;
import com.green.chakak.chakak.global.utils.Define;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.expression.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.jmx.export.annotation.ManagedOperation;
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

    // 예약하기
    @PostMapping("/booking/save")
    public ResponseEntity<?> saveBooking(@Valid @RequestBody BookingInfoRequest.CreateDTO createDTO, Errors errors,
                                           @RequestAttribute(value = Define.LOGIN_USER) LoginUser loginUser){
        bookingInfoService.save(createDTO,loginUser);
        return ResponseEntity.ok(new ApiUtil<>("서비스 생성이 완료 되었습니다"));
    }


    // 예약 변경 하기

    // 예약 취소하기

}
