package com.green.chakak.chakak.booking.service.repository;

import com.green.chakak.chakak.booking.domain.BookingCancelInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingCancelInfoJpaRepository extends JpaRepository<BookingCancelInfo, Long> {
    Page<BookingCancelInfo> findAllByUserProfile_UserProfileId(Long userProfileId, Pageable pageable);

    Page<BookingCancelInfo> findAllByBookingInfo_PhotographerProfile_PhotographerProfileId(Long photographerProfileId, Pageable pageable);
}
