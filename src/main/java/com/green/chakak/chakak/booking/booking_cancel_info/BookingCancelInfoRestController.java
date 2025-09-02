package com.green.chakak.chakak.booking.booking_cancel_info;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/booking_cancel_info")
public class BookingCancelInfoRestController {


    private final BookingCancelInfoService bookingCancelInfoService;
    // 단일 조회
    @GetMapping("/{bookingCancelInfoId}")
    public ResponseEntity<?> getBookingCancelInfo(@PathVariable Long bookingCancelInfoId) {
        BookingCancelInfoResponse.BookingCancelInfoGetResponse response = bookingCancelInfoService.getBookingCancelInfo(bookingCancelInfoId);
        return ResponseEntity.ok(response);
    }

    // 유저 프로필별 예약 취소 내역 페이징 조회
    @GetMapping("/user/{userProfileId}/paged")
    public ResponseEntity<?> getPagedCancelInfoListByUserProfileId(@PathVariable Long userProfileId,
                                                                  @RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "10") int size) {
        var response = bookingCancelInfoService.getPagedCancelInfoListByUserProfileId(userProfileId, PageRequest.of(page, size));
        return ResponseEntity.ok(response);
    }

    // 포토그래퍼 프로필별 예약 취소 내역 페이징 조회
    @GetMapping("/photographer/{photographerProfileId}/paged")
    public ResponseEntity<?> getPagedCancelInfoListByPhotographerProfileId(@PathVariable Long photographerProfileId,
                                                                          @RequestParam(defaultValue = "0") int page,
                                                                          @RequestParam(defaultValue = "10") int size) {
        var response = bookingCancelInfoService.getPagedCancelInfoListByPhotographerProfileId(photographerProfileId, PageRequest.of(page, size));
        return ResponseEntity.ok(response);
    }

    // 통합형 예약 취소 내역 페이징 조회 (유저/포토그래퍼)
    @GetMapping("/paged")
    public ResponseEntity<?> getPagedCancelInfoList(BookingCancelInfoRequest.BookingCancelInfoListRequest req) {
        // userId, photographerId, page, size, sort 모두 DTO에서 받음
        var response = bookingCancelInfoService.getPagedCancelInfoList(req);
        return ResponseEntity.ok(response);
    }

}
