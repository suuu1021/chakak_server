package com.green.chakak.chakak.booking.service;

import com.green.chakak.chakak.account.domain.LoginUser;
import com.green.chakak.chakak.account.domain.User;
import com.green.chakak.chakak.account.domain.UserProfile;
import com.green.chakak.chakak.account.service.repository.UserProfileJpaRepository;
import com.green.chakak.chakak.booking.domain.BookingInfo;
import com.green.chakak.chakak.booking.domain.BookingStatus;
import com.green.chakak.chakak.booking.service.repository.BookingInfoJpaRepository;
import com.green.chakak.chakak.booking.service.request.BookingInfoRequest;
import com.green.chakak.chakak.booking.service.response.BookingInfoResponse;
import com.green.chakak.chakak._global.errors.exception.Exception400;
import com.green.chakak.chakak._global.errors.exception.Exception403;
import com.green.chakak.chakak._global.errors.exception.Exception404;
import com.green.chakak.chakak.photo.domain.PhotoServiceInfo;
import com.green.chakak.chakak.photo.domain.PriceInfo;
import com.green.chakak.chakak.photo.service.repository.PhotoServiceJpaRepository;
import com.green.chakak.chakak.photographer.domain.PhotographerProfile;
import com.green.chakak.chakak.photographer.service.repository.PhotographerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingInfoService {

    private final BookingInfoJpaRepository bookingInfoJpaRepository;
    private final UserProfileJpaRepository userProfileJpaRepository;
    private final PhotographerRepository photographerRepository;
    private final PhotoServiceJpaRepository photoServiceJpaRepository;
    private final com.green.chakak.chakak.photo.service.repository.PriceInfoJpaRepository priceInfoJpaRepository;


    // [사용자] 나의 예약 목록 조회
    public List<BookingInfoResponse.BookingUserListDTO> getUserBookings(LoginUser loginUser){
        // 로그인한 사용자의 ID로 직접 조회하여 다른 사용자의 정보를 볼 수 없도록 함
        return bookingInfoJpaRepository.findByUserId(loginUser.getId())  // 이 부분만 변경
                .stream()
                .map(BookingInfoResponse.BookingUserListDTO::new)
                .toList();
        }

        // [포토그래퍼] 나의 예약 목록 조회
        public List<BookingInfoResponse.BookingUserListDTO> getPhotographerBookings(LoginUser loginUser){
            return bookingInfoJpaRepository.findByPhotographerId(loginUser.getId())  // 이것도 변경 가능
                    .stream()
                    .map(BookingInfoResponse.BookingUserListDTO::new)
                    .toList();
        }


    // 예약 상세 조회
    public BookingInfoResponse.BookingDetailDTO getBookingDetail(LoginUser loginUser, Long bookingInfoId){
        BookingInfo bookingInfo = bookingInfoJpaRepository.findById(bookingInfoId)
                .orElseThrow(() -> new Exception404("존재하지 않는 예약입니다."));

        // 예약자 또는 포토그래퍼 본인만 조회 가능하도록 권한 확인
        Long userId = bookingInfo.getUserProfile().getUser().getUserId();
        Long photographerUserId = bookingInfo.getPhotographerProfile().getUser().getUserId();

        if(!loginUser.getId().equals(userId) && !loginUser.getId().equals(photographerUserId)){
            throw new Exception403("해당 예약 정보를 조회할 권한이 없습니다.");
        }
        return new BookingInfoResponse.BookingDetailDTO(bookingInfo);
    }


    // 예약 생성 (포토그래퍼가 제안)
    @Transactional
    public void createBooking(BookingInfoRequest.CreateDTO createDTO, LoginUser loginUser){
        // 예약 제안은 포토그래퍼만 가능
        if (!"photographer".equalsIgnoreCase(loginUser.getUserTypeName())) {
            throw new Exception403("포토그래퍼만 예약을 생성할 수 있습니다.");
        }

        UserProfile userProfile = userProfileJpaRepository.findByUserId(createDTO.getUserProfileId())
                .orElseThrow(() -> new Exception404("예약을 받을 사용자를 찾을 수 없습니다."));

        PhotographerProfile photographerProfile = photographerRepository.findById(createDTO.getPhotographerProfileId())
                .orElseThrow(() -> new Exception404("존재하지 않는 작가입니다."));

        PhotoServiceInfo photoServiceInfo = photoServiceJpaRepository.findById(createDTO.getPhotoServiceId())
                .orElseThrow(() -> new Exception404("존재하지 않는 포토 서비스입니다."));

        PriceInfo priceInfo = priceInfoJpaRepository.findById(createDTO.getPriceInfoId())
                .orElseThrow(() -> new Exception404("존재하지 않는 가격 정보입니다."));

        BookingInfo bookingInfo = createDTO.toEntity(userProfile, photographerProfile, priceInfo, photoServiceInfo);
        bookingInfoJpaRepository.save(bookingInfo);
    }


    // [사용자] 예약 취소
    @Transactional
    public void cancelBooking(Long bookingInfoId, LoginUser loginUser){
        BookingInfo bookingInfo = bookingInfoJpaRepository.findById(bookingInfoId)
                .orElseThrow(() -> new Exception404("존재하지 않는 예약 내역입니다."));

        // 예약자 본인만 취소 가능
        if(!bookingInfo.getUserProfile().getUser().getUserId().equals(loginUser.getId())){
            throw new Exception403("해당 예약 정보를 수정할 권한이 없습니다.");
        }

        // PENDING 상태일 때만 취소 가능
        if(!bookingInfo.getStatus().canChangeTo(BookingStatus.CANCELED)){
            throw new Exception400("예약대기 상태일 때만 취소할 수 있습니다. 현재 상태: " + bookingInfo.getStatus().getDescription());
        }
        bookingInfo.setStatus(BookingStatus.CANCELED);
    }

    // [포토그래퍼] 예약 확정
    @Transactional
    public void confirmBooking(Long bookingInfoId, LoginUser loginUser){
        BookingInfo bookingInfo = bookingInfoJpaRepository.findById(bookingInfoId)
                .orElseThrow(() -> new Exception404("존재하지 않는 예약 내역입니다."));

        // 포토그래퍼 본인만 확정 가능
        if(!bookingInfo.getPhotographerProfile().getUser().getUserId().equals(loginUser.getId())){
            throw new Exception403("해당 예약 정보를 수정할 권한이 없습니다.");
        }

        // PENDING 상태일 때만 확정 가능
        if(!bookingInfo.getStatus().canChangeTo(BookingStatus.CONFIRMED)){
            throw new Exception400("예약대기 상태일 때만 예약을 확정할 수 있습니다. 현재 상태: " + bookingInfo.getStatus().getDescription());
        }
        bookingInfo.setStatus(BookingStatus.CONFIRMED);
    }

    // [포토그래퍼] 촬영 완료 처리
    @Transactional
    public void completeBooking(Long bookingInfoId, LoginUser loginUser){
        BookingInfo bookingInfo = bookingInfoJpaRepository.findById(bookingInfoId)
                .orElseThrow(() -> new Exception404("존재하지 않는 예약 내역입니다."));

        // 포토그래퍼 본인만 완료 처리 가능
        if(!bookingInfo.getPhotographerProfile().getUser().getUserId().equals(loginUser.getId())){
            throw new Exception403("해당 예약 정보를 수정할 권한이 없습니다.");
        }

        // CONFIRMED 상태일 때만 촬영 완료 처리 가능
        if(!bookingInfo.getStatus().canChangeTo(BookingStatus.COMPLETED)){
            throw new Exception400("예약확정 상태일 때만 촬영 완료로 처리할 수 있습니다. 현재 상태: " + bookingInfo.getStatus().getDescription());
        }
        bookingInfo.setStatus(BookingStatus.COMPLETED);
    }

    // TODO - 리뷰 남길 시 사용할 서비스
    // [사용자] 리뷰 작성 후 상태 변경
    @Transactional
    public void reviewBooking(Long bookingInfoId, LoginUser loginUser){
        BookingInfo bookingInfo = bookingInfoJpaRepository.findById(bookingInfoId)
                .orElseThrow(() -> new Exception404("존재하지 않는 예약 내역입니다."));

        // 예약자 본인만 리뷰 작성 가능
        if (!bookingInfo.getUserProfile().getUser().getUserId().equals(loginUser.getId())){
            throw new Exception403("해당 서비스에 리뷰를 남길 수 없습니다.");
        }

        // COMPLETED 상태일 때만 리뷰 작성 가능
        if(!bookingInfo.getStatus().canChangeTo(BookingStatus.REVIEWED)){
            throw new Exception400("촬영완료 상태일 때만 리뷰를 작성할 수 있습니다. 현재 상태: " + bookingInfo.getStatus().getDescription());
        }
        bookingInfo.setStatus(BookingStatus.REVIEWED);
    }
}
