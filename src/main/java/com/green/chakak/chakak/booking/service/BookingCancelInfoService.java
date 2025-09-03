package com.green.chakak.chakak.booking.service;

import com.green.chakak.chakak.booking.domain.BookingCancelInfo;
import com.green.chakak.chakak.booking.service.repository.BookingCancelInfoJpaRepository;
import com.green.chakak.chakak.booking.service.request.BookingCancelInfoRequest;
import com.green.chakak.chakak.booking.service.response.BookingCancelInfoResponse;
import com.green.chakak.chakak.global.utils.PageUtil.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class BookingCancelInfoService {
    private final BookingCancelInfoJpaRepository bookingCancelInfoJpaRepository;


    // 유저가 캔슬 내역 목록 조회
    @Transactional
    public List<BookingCancelInfoResponse.BookingCancelInfoListResponse> getBookingCancelInfoList(BookingCancelInfoRequest.BookingCancelInfoListRequest req) {
        List<BookingCancelInfo> bookingCancelInfoList = bookingCancelInfoJpaRepository.findAll();
        return bookingCancelInfoList.stream()
                .map(BookingCancelInfoResponse.BookingCancelInfoListResponse::from)
                .toList();
    }

    // 단일 조회
    public BookingCancelInfoResponse.BookingCancelInfoGetResponse getBookingCancelInfo(Long bookingCancelInfoId) {
        BookingCancelInfo bookingCancelInfo = bookingCancelInfoJpaRepository.findById(bookingCancelInfoId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 내역입니다.."));
        return BookingCancelInfoResponse.BookingCancelInfoGetResponse.from(bookingCancelInfo);
    }


    // 유저 프로필별 예약 취소 내역 페이징 조회
    @Transactional(readOnly = true)
    public PageResponse<BookingCancelInfoResponse.BookingCancelInfoListResponse> getPagedCancelInfoListByUserProfileId(Long userProfileId, Pageable pageable) {
        Page<BookingCancelInfo> page = bookingCancelInfoJpaRepository.findAllByUserProfile_UserProfileId(userProfileId, pageable);
        Page<BookingCancelInfoResponse.BookingCancelInfoListResponse> mapped = page.map(BookingCancelInfoResponse.BookingCancelInfoListResponse::from);
        return PageResponse.from(mapped);
    }

    // 포토그래퍼 프로필별 예약 취소 내역 페이징 조회
    @Transactional(readOnly = true)
    public PageResponse<BookingCancelInfoResponse.BookingCancelInfoListResponse> getPagedCancelInfoListByPhotographerProfileId(Long photographerProfileId, Pageable pageable) {
        Page<BookingCancelInfo> page = bookingCancelInfoJpaRepository.findAllByBookingInfo_PhotographerProfile_PhotographerProfileId(photographerProfileId, pageable);
        Page<BookingCancelInfoResponse.BookingCancelInfoListResponse> mapped = page.map(BookingCancelInfoResponse.BookingCancelInfoListResponse::from);
        return PageResponse.from(mapped);
    }

    // 통합형 예약 취소 내역 페이징 조회 (유저/포토그래퍼)
    @Transactional(readOnly = true)
    public PageResponse<BookingCancelInfoResponse.BookingCancelInfoListResponse> getPagedCancelInfoList(BookingCancelInfoRequest.BookingCancelInfoListRequest req) {
        Page<BookingCancelInfo> page;
        if (req.getUserId() != null) {
            page = bookingCancelInfoJpaRepository.findAllByUserProfile_UserProfileId(req.getUserId(), PageRequest.of(req.getPage(), req.getSize()));
        } else if (req.getPhotographerId() != null) {
            page = bookingCancelInfoJpaRepository.findAllByBookingInfo_PhotographerProfile_PhotographerProfileId(req.getPhotographerId(), PageRequest.of(req.getPage(), req.getSize()));
        } else {
            page = bookingCancelInfoJpaRepository.findAll(PageRequest.of(req.getPage(), req.getSize()));
        }
        Page<BookingCancelInfoResponse.BookingCancelInfoListResponse> mapped = page.map(BookingCancelInfoResponse.BookingCancelInfoListResponse::from);
        return PageResponse.from(mapped);
    }

    // 쓰기, 수정, 삭제 필요 없을듯
}
