package com.green.chakak.chakak.photo.service.repository;

import com.green.chakak.chakak.photo.domain.PhotoServiceInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PhotoServiceJpaRepository extends JpaRepository<PhotoServiceInfo, Long> {

    @Query("SELECT ps FROM PhotoServiceInfo ps ORDER BY ps.serviceId DESC")
    Page<PhotoServiceInfo> findAllServiceInfo(Pageable pageable);

    @Query("SELECT p FROM PhotoServiceInfo p " +
            "JOIN FETCH p.photographerProfile pp " +
            "JOIN FETCH pp.user u " +
            "JOIN FETCH u.userType " +  // UserType까지 fetch
            "WHERE p.serviceId = :id")
    Optional<PhotoServiceInfo> findByIdWithUser(@Param("id") Long id);

    @Query("SELECT ps FROM PhotoServiceInfo ps WHERE ps.title like %:keyword% ORDER BY ps.serviceId DESC")
    Page<PhotoServiceInfo> findAllServiceInfoByKeyword(Pageable pageable, String keyword);

}
