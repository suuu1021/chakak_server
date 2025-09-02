package com.green.chakak.chakak.booking.booking_info;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingInfoJpaRepository extends JpaRepository<BookingInfo, Long> {

    @Query("SELECT b FROM BookingInfo b JOIN FETCH b.userProfile up JOIN FETCH up.user u WHERE u.userId = :userId")
    List<BookingInfo> findByUserId(@Param("userId") Long userId);

}
