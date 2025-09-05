package com.green.chakak.chakak.booking.service.repository;

import com.green.chakak.chakak.account.domain.UserProfile;
import com.green.chakak.chakak.booking.domain.BookingCancelInfo;
import com.green.chakak.chakak.photographer.domain.PhotographerProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingCancelInfoJpaRepository extends JpaRepository<BookingCancelInfo, Long> {

    Page<BookingCancelInfo> findAllByUserProfile(UserProfile userProfile, Pageable pageable);

    Page<BookingCancelInfo> findAllByBookingInfo_PhotographerProfile(PhotographerProfile photographerProfile, Pageable pageable);
}
