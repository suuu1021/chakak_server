package com.green.chakak.chakak.booking.service;

import com.green.chakak.chakak.booking.domain.BookingCancelInfo;
import com.green.chakak.chakak.booking.service.repository.BookingCancelInfoJpaRepository;
import com.green.chakak.chakak.booking.service.request.BookingCancelInfoRequest;
import com.green.chakak.chakak.booking.service.response.BookingCancelInfoResponse;
import com.green.chakak.chakak._global.utils.PageUtil.PageResponse;
import com.green.chakak.chakak._global.errors.exception.Exception404;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class BookingCancelInfoService {
    private final BookingCancelInfoJpaRepository bookingCancelInfoJpaRepository;


    // 단일 조회
    public BookingCancelInfoResponse.BookingCancelInfoGetResponse getBookingCancelInfo(Long bookingCancelInfoId) {
        BookingCancelInfo bookingCancelInfo = bookingCancelInfoJpaRepository.findById(bookingCancelInfoId).orElseThrow(() -> new Exception404("존재하지 않는 내역입니다.."));
        return BookingCancelInfoResponse.BookingCancelInfoGetResponse.from(bookingCancelInfo);
    }


    // 유저 프로필별 예약 취소 내역 페이징 조회
    @Transactional(readOnly = true)
    public PageResponse<BookingCancelInfoResponse.BookingCancelInfoListResponse> getPagedCancelInfoListByUserProfileId(Long userProfileId, Pageable pageable) {
        Page<BookingCancelInfo> page = bookingCancelInfoJpaRepository.findAllByUserProfileId(userProfileId, pageable);
        Page<BookingCancelInfoResponse.BookingCancelInfoListResponse> mapped = page.map(BookingCancelInfoResponse.BookingCancelInfoListResponse::from);
        return PageResponse.from(mapped);
    }

    // 포토그래퍼 프로필별 예약 취소 내역 페이징 조회
    @Transactional(readOnly = true)
    public PageResponse<BookingCancelInfoResponse.BookingCancelInfoListResponse> getPagedCancelInfoListByPhotographerProfileId(Long photographerProfileId, Pageable pageable) {
        Page<BookingCancelInfo> page = bookingCancelInfoJpaRepository.findAllByPhotographerProfileId(photographerProfileId, pageable);
        Page<BookingCancelInfoResponse.BookingCancelInfoListResponse> mapped = page.map(BookingCancelInfoResponse.BookingCancelInfoListResponse::from);
        return PageResponse.from(mapped);
    }

    // 통합형 예약 취소 내역 페이징 조회 (유저/포토그래퍼)
    @Transactional(readOnly = true)
    public PageResponse<BookingCancelInfoResponse.BookingCancelInfoListResponse> getPagedCancelInfoList(BookingCancelInfoRequest.BookingCancelInfoListRequest req) {
        Page<BookingCancelInfo> page;
        if (req.getUserId() != null) {
            page = bookingCancelInfoJpaRepository.findAllByUserProfileId(req.getUserId(), PageRequest.of(req.getPage(), req.getSize()));
        } else if (req.getPhotographerId() != null) {
            page = bookingCancelInfoJpaRepository.findAllByPhotographerProfileId(req.getPhotographerId(), PageRequest.of(req.getPage(), req.getSize()));
        } else {
            page = bookingCancelInfoJpaRepository.findAll(PageRequest.of(req.getPage(), req.getSize()));
        }
        Page<BookingCancelInfoResponse.BookingCancelInfoListResponse> mapped = page.map(BookingCancelInfoResponse.BookingCancelInfoListResponse::from);
        return PageResponse.from(mapped);
    }

    // 쓰기, 수정, 삭제 필요 없을듯
}
