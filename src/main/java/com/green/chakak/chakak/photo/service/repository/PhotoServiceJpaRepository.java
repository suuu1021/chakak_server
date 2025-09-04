package com.green.chakak.chakak.photo.service.repository;

import com.green.chakak.chakak.photo.domain.PhotoServiceInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PhotoServiceJpaRepository extends JpaRepository<PhotoServiceInfo, Long> {

    @Query("SELECT ps FROM PhotoServiceInfo ps ORDER BY ps.serviceId DESC")
    Page<PhotoServiceInfo> findAllServiceInfo(Pageable pageable);

    @Query("SELECT ps FROM PhotoServiceInfo ps WHERE ps.title like %:keyword% ORDER BY ps.serviceId DESC")
    Page<PhotoServiceInfo> findAllServiceInfoByKeyword(Pageable pageable, String keyword);

}
