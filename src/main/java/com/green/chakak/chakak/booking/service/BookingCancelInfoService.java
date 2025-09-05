package com.green.chakak.chakak.booking.service;

import com.green.chakak.chakak._global.errors.exception.Exception403;
import com.green.chakak.chakak._global.errors.exception.Exception404;
import com.green.chakak.chakak._global.utils.PageUtil.PageResponse;
import com.green.chakak.chakak.account.domain.LoginUser;
import com.green.chakak.chakak.account.domain.UserProfile;
import com.green.chakak.chakak.account.service.repository.UserProfileJpaRepository;
import com.green.chakak.chakak.booking.domain.BookingCancelInfo;
import com.green.chakak.chakak.booking.service.repository.BookingCancelInfoJpaRepository;
import com.green.chakak.chakak.booking.service.request.BookingCancelInfoRequest;
import com.green.chakak.chakak.booking.service.response.BookingCancelInfoResponse;
import com.green.chakak.chakak.photographer.domain.PhotographerProfile;
import com.green.chakak.chakak.photographer.service.repository.PhotographerRepository;
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
    private final UserProfileJpaRepository userProfileJpaRepository;
    private final PhotographerRepository photographerRepository;

    // 단일 조회
    public BookingCancelInfoResponse.BookingCancelInfoGetResponse getBookingCancelInfo(Long bookingCancelInfoId, LoginUser loginUser) {
        BookingCancelInfo bookingCancelInfo = bookingCancelInfoJpaRepository.findById(bookingCancelInfoId)
                .orElseThrow(() -> new Exception404("존재하지 않는 내역입니다."));

        String userTypeName = loginUser.getUserTypeName();

        switch (userTypeName) {
            case "user": {
                if (!bookingCancelInfo.getUserProfile().getUser().getUserId().equals(loginUser.getId())) {
                    throw new Exception403("해당 내역을 조회할 권한이 없습니다.");
                }
                break;
            }
            case "photographer": {
                if (!bookingCancelInfo.getBookingInfo().getPhotographerProfile().getUser().getUserId().equals(loginUser.getId())) {
                    throw new Exception403("해당 내역을 조회할 권한이 없습니다.");
                }
                break;
            }
            default: {
                throw new Exception403("해당 내역을 조회할 권한이 없습니다.");
            }
        }
        return BookingCancelInfoResponse.BookingCancelInfoGetResponse.from(bookingCancelInfo);
    }

    // 로그인 유저의 예약 취소 내역 페이징 조회 (유저/포토그래퍼)
    @Transactional(readOnly = true)
    public PageResponse<BookingCancelInfoResponse.BookingCancelInfoListResponse> getMyCancelInfoList(LoginUser loginUser, Pageable pageable) {
        Page<BookingCancelInfo> page;
        String userTypeName = loginUser.getUserTypeName();

        switch (userTypeName) {
            case "user": {
                UserProfile userProfile = userProfileJpaRepository.findByUserId(loginUser.getId())
                        .orElseThrow(() -> new Exception404("해당 유저의 프로필을 찾을 수 없습니다."));
                page = bookingCancelInfoJpaRepository.findAllByUserProfile(userProfile, pageable);
                break;
            }
            case "photographer": {
                PhotographerProfile photographerProfile = photographerRepository.findByUser_UserId(loginUser.getId())
                        .orElseThrow(() -> new Exception404("해당 포토그래퍼의 프로필을 찾을 수 없습니다."));
                page = bookingCancelInfoJpaRepository.findAllByBookingInfo_PhotographerProfile(photographerProfile, pageable);
                break;
            }
            default: {
                page = Page.empty(pageable);
                break;
            }
        }

        return PageResponse.from(page.map(BookingCancelInfoResponse.BookingCancelInfoListResponse::from));
    }

    // 통합형 예약 취소 내역 페이징 조회 (관리자용 - ID 기반)
    @Transactional(readOnly = true)
    public PageResponse<BookingCancelInfoResponse.BookingCancelInfoListResponse> getPagedCancelInfoList(BookingCancelInfoRequest.BookingCancelInfoListRequest req) {
        Page<BookingCancelInfo> page;
        if (req.getUserId() != null) {
            UserProfile userProfile = userProfileJpaRepository.findById(req.getUserId()).orElse(null);
            if (userProfile != null) {
                page = bookingCancelInfoJpaRepository.findAllByUserProfile(userProfile, PageRequest.of(req.getPage(), req.getSize()));
            } else {
                page = Page.empty(PageRequest.of(req.getPage(), req.getSize()));
            }
        } else if (req.getPhotographerId() != null) {
            PhotographerProfile photographerProfile = photographerRepository.findById(req.getPhotographerId()).orElse(null);
            if (photographerProfile != null) {
                page = bookingCancelInfoJpaRepository.findAllByBookingInfo_PhotographerProfile(photographerProfile, PageRequest.of(req.getPage(), req.getSize()));
            } else {
                page = Page.empty(PageRequest.of(req.getPage(), req.getSize()));
            }
        } else {
            page = bookingCancelInfoJpaRepository.findAll(PageRequest.of(req.getPage(), req.getSize()));
        }

        return PageResponse.from(page.map(BookingCancelInfoResponse.BookingCancelInfoListResponse::from));
    }
}
