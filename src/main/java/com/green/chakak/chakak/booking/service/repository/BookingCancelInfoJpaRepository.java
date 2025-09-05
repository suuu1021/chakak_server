package com.green.chakak.chakak.booking.service.repository;

import com.green.chakak.chakak.booking.domain.BookingCancelInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingCancelInfoJpaRepository extends JpaRepository<BookingCancelInfo, Long> {

    @Query("SELECT b FROM BookingCancelInfo b WHERE b.userProfile.userProfileId = :userProfileId")
    Page<BookingCancelInfo> findAllByUserProfileId(@Param("userProfileId") Long userProfileId, Pageable pageable);

    @Query("SELECT b FROM BookingCancelInfo b WHERE b.bookingInfo.photographerProfile.photographerProfileId = :photographerProfileId")
    Page<BookingCancelInfo> findAllByPhotographerProfileId(@Param("photographerProfileId") Long photographerProfileId, Pageable pageable);
}
