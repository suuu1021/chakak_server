package com.green.chakak.chakak.booking.service;

import com.green.chakak.chakak.account.domain.LoginUser;
import com.green.chakak.chakak.account.domain.User;
import com.green.chakak.chakak.account.service.repository.UserJpaRepository;
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
import com.green.chakak.chakak.photo.service.repository.PriceInfoJpaRepository;
import com.green.chakak.chakak.photographer.domain.PhotographerCategory;
import com.green.chakak.chakak.photographer.domain.PhotographerProfile;
import com.green.chakak.chakak.photographer.service.repository.PhotographerCategoryRepository;
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
    private final UserJpaRepository userJpaRepository;
    private final UserProfileJpaRepository userProfileJpaRepository;
    private final PhotographerRepository photographerRepository;
    private final PhotoServiceJpaRepository photoServiceJpaRepository;
    private final PhotographerCategoryRepository photographerCategoryRepository;
    private final PriceInfoJpaRepository priceInfoJpaRepository;


    // 예약 조회(유저 입장)
    public List<BookingInfoResponse.BookingUserListDTO> bookingUserListDTO(LoginUser loginUser, Long userId){
        User user = userJpaRepository.findById(loginUser.getId())
                .orElseThrow(() -> new Exception404("존재하지 않는 유저입니다."));

        if(!user.getUserId().equals(userId)){
            throw new Exception403("본인의 예약만 조회가능합니다.");
        }

        return bookingInfoJpaRepository.findByUserId(userId)
                .stream()
                .map(BookingInfoResponse.BookingUserListDTO::new)
                .toList();
    }


    // 예약 조회(포토그래퍼 입장)
    @Transactional
    public List<BookingInfoResponse.BookingUserListDTO> bookingPhotographerListDTO(LoginUser loginUser, Long userId){
        User user = userJpaRepository.findById(loginUser.getId())
                .orElseThrow(() -> new Exception404("존재하지 않는 유저입니다."));

        if(!user.getUserId().equals(userId)){
            throw new Exception403("본인의 예약만 조회가능합니다.");
        }
        return bookingInfoJpaRepository.findByPhotographerId(userId)
                .stream()
                .map(BookingInfoResponse.BookingUserListDTO::new)
                .toList();
    }


    // 예약 상세 조회
    @Transactional
    public BookingInfoResponse.BookingDetailDTO findByDetailList(LoginUser loginUser, Long bookingInfoId){
        BookingInfo bookingInfo = bookingInfoJpaRepository.findById(bookingInfoId)
                .orElseThrow(() -> new Exception404("존재하지 않는 예약입니다."));

        Long userId = bookingInfo.getUserProfile().getUser().getUserId();
        Long photographerUserId = bookingInfo.getPhotographerProfile().getUser().getUserId();

        if(!loginUser.getId().equals(userId) && !loginUser.getId().equals(photographerUserId)){
            throw new Exception403("해당 예약 정보를조회할 권한이 없습니다.");
        }
        return new BookingInfoResponse.BookingDetailDTO(bookingInfo);
    }


    // 예약하기
    @Transactional
    public void save(BookingInfoRequest.CreateDTO createDTO, LoginUser loginUser){
        UserProfile userProfile = userProfileJpaRepository.findByUserId(loginUser.getId())
                .orElseThrow(() -> new Exception404("존재하지 않는 일반 사용자입니다."));

        PhotographerProfile photographerProfile = photographerRepository.findById(createDTO.getPhotographerProfileId())
                .orElseThrow(() -> new Exception404("존재하지 않는 작가입니다."));

        PhotoServiceInfo photoServiceInfo = photoServiceJpaRepository.findById(createDTO.getPhotoServiceId())
                .orElseThrow(() -> new Exception404("존재하지 않는 포토서비스입니다"));

        PriceInfo priceInfo = priceInfoJpaRepository.findById(createDTO.getPriceInfoId())
                .orElseThrow(() -> new Exception404("존재하지 않는 가격정보 입니다."));

        BookingInfo bookingInfo = createDTO.toEntity(userProfile, photographerProfile, priceInfo, photoServiceInfo);
        bookingInfoJpaRepository.save(bookingInfo);
    }


    // (유저입장)예약 취소 CANCELED
    @Transactional
    public void userCanceledStatus(Long bookingInfoId, BookingStatus status ,LoginUser loginUser){
        BookingInfo bookingInfo = bookingInfoJpaRepository.findById(bookingInfoId)
                .orElseThrow(() -> new Exception404("존재하지 않는 예약 내역입니다."));
        if(!bookingInfo.getUserProfile().getUser().getUserId().equals(loginUser.getId())){
            throw new Exception403("해당 예약 정보를 수정할 권한이 없습니다.");
        }
        if(bookingInfo.getStatus() != BookingStatus.PENDING){
            throw new Exception400("예약대기 상태일시만 취소처리 가능합니다.");
        }
        bookingInfo.setStatus(status);
    }



//     (포토그래퍼입장)예약 승낙
    @Transactional
    public void photographerConfirmedStatus(Long bookingInfoId, BookingStatus status ,LoginUser loginUser){
        BookingInfo bookingInfo = bookingInfoJpaRepository.findById(bookingInfoId)
                .orElseThrow(() -> new Exception404("존재하지 않는 예약 내역입니다."));
        if(!bookingInfo.getPhotographerProfile().getUser().getUserId().equals(loginUser.getId())){
            throw new Exception403("해당 예약 정보를 수정할 권한이 없습니다.");
        }
        if(bookingInfo.getStatus() != BookingStatus.PENDING){
            throw new Exception400("예약대기 상태일시만 예약 승낙 가능합니다.");
        }
        bookingInfo.setStatus(status);
    }

    //     (포토그래퍼입장)예약 거절 REJECTED
    @Transactional
    public void photographerRejectedStatus(Long bookingInfoId, BookingStatus status ,LoginUser loginUser){
        BookingInfo bookingInfo = bookingInfoJpaRepository.findById(bookingInfoId)
                .orElseThrow(() -> new Exception404("존재하지 않는 예약 내역입니다."));
        if(!bookingInfo.getPhotographerProfile().getUser().getUserId().equals(loginUser.getId())){
            throw new Exception403("해당 예약 정보를 수정할 권한이 없습니다.");
        }
        if(bookingInfo.getStatus() != BookingStatus.PENDING && bookingInfo.getStatus() != BookingStatus.CONFIRMED){
            throw new Exception400("예약대기 상태일시와 예약 승락 상태일시만 예약 거절이 가능합니다.");
        }
        bookingInfo.setStatus(status);
    }

    // (포토그래퍼입장) 촬영 완료 COMPLETED
    @Transactional
    public void photographerCompletedStatus(Long bookingInfoId, BookingStatus status, LoginUser loginUser){
        BookingInfo bookingInfo = bookingInfoJpaRepository.findById(bookingInfoId)
                .orElseThrow(() -> new Exception404("존재하지 않는 예약 내역입니다."));
    if(!bookingInfo.getPhotographerProfile().getUser().getUserId().equals(loginUser.getId())){
        throw new Exception403("혜당 예약 정보를 수정할 권한이 없습니다.");
    }
    if(bookingInfo.getStatus() != BookingStatus.CONFIRMED){
        throw new Exception400("예약 승낙된 상태일시만 촬용 완료처리 가능합니다.");
    }
    bookingInfo.setStatus(status);
    }

    // TODO - 리뷰 남길 시 사용할 서비스
    // (유저) 리뷰남길시 상태값 변경(리뷰 작성완료) REVIEWED
    @Transactional
    public void photoReviewedStatus(Long bookingInfoId,BookingStatus status ,LoginUser loginUser){
        BookingInfo bookingInfo = bookingInfoJpaRepository.findById(bookingInfoId)
                .orElseThrow(() -> new Exception404("존재하지 않는 예약 내역입니다"));
        if (!bookingInfo.getUserProfile().getUser().getUserId().equals(loginUser.getId())){
            throw new Exception403("혜당 서비스에 리뷰를 남길 수 없습니다.");
        }
        if(bookingInfo.getStatus() != BookingStatus.COMPLETED){
            throw new Exception400("촬용을 완료한 서비스에만 리뷰 작성처리가 가능합니다.");
        }
        bookingInfo.setStatus(status);
    }


}
