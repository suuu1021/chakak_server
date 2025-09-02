package com.green.chakak.chakak.booking.booking_info;

import com.green.chakak.chakak.account.user.LoginUser;
import com.green.chakak.chakak.account.user.User;
import com.green.chakak.chakak.account.user.UserJpaRepository;
import com.green.chakak.chakak.account.user_profile.UserProfileJpaRepository;
import com.green.chakak.chakak.global.errors.exception.Exception403;
import com.green.chakak.chakak.global.errors.exception.Exception404;
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
    private final UserJpaRepository userJpaRepository;
    private final UserProfileJpaRepository userProfileJpaRepository;
    private final PhotographerRepository photographerRepository;


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


    // 예약 정보 변경


    // 예약 취소


}
