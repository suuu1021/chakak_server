package com.green.chakak.chakak.booking.booking_info;

import com.green.chakak.chakak.account.user.LoginUser;
import com.green.chakak.chakak.account.user.User;
import com.green.chakak.chakak.account.user.UserJpaRepository;
import com.green.chakak.chakak.account.user_profile.UserProfile;
import com.green.chakak.chakak.account.user_profile.UserProfileJpaRepository;
import com.green.chakak.chakak.booking.domain.BookingInfo;
import com.green.chakak.chakak.global.errors.exception.Exception403;
import com.green.chakak.chakak.global.errors.exception.Exception404;
import com.green.chakak.chakak.photo.domain.PhotoServiceInfo;
import com.green.chakak.chakak.photo.service.repository.PhotoServiceJpaRepository;
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


//    // 예약 조회(포토그래퍼 입장)
//    @Transactional
//    public BookingInfoResponse.BookingPhotographerList findByPhotographerList(){
//
//    }


//    // 예약 상세 조회
//    @Transactional
//    public BookingInfoResponse.BookingDetail findByDetailList(){
//
//    }


    // 예약하기
    @Transactional
    public BookingInfoResponse.SaveDTO save(BookingInfoRequest.CreateDTO createDTO, LoginUser loginUser){
        UserProfile userProfile = userProfileJpaRepository.findByUserId(loginUser.getId())
                .orElseThrow(() -> new Exception404("존재하지 않는 일반 사용자입니다."));

        PhotographerProfile photographerProfile = photographerRepository.findById(createDTO.getPhotographerProfileId())
                .orElseThrow(() -> new Exception404("존재하지 않는 작가입니다."));

        PhotographerCategory photographerCategory = photographerCategoryRepository.findById(createDTO.getPhotographerCategoryId())
                .orElseThrow(() -> new Exception404("존재하지 않는 카테고리입니다."));

        PhotoServiceInfo photoServiceInfo = photoServiceJpaRepository.findById(createDTO.getPhotoServiceId())
                .orElseThrow(() -> new Exception404("존재하지 않는 포토서비스입니다"));
//        BookingInfo bookingInfo = BookingInfo.builder()
//                .userProfile(userProfile)
//                .photographerProfile(photographerProfile)
//                .photoServiceInfo(photoServiceInfo)
//                .build();
        BookingInfo bookingInfo = createDTO.toEntity(userProfile, photographerProfile, photographerCategory, photoServiceInfo);
         BookingInfo savedBookingInfo = bookingInfoJpaRepository.save(bookingInfo);
        return new BookingInfoResponse.SaveDTO(savedBookingInfo);
    }


    // 예약 정보 변경


    // 예약 취소


}
