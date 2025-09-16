package com.green.chakak.chakak.admin.service.repository;

import com.green.chakak.chakak.admin.domain.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AdminJpaRepository extends JpaRepository <Admin, Long> {
    @Query("Select a From Admin a where a.adminName = :name And a.password = :password")
    Optional<Admin> findByNameAndAdminPassword(@Param("name") String name,
                                               @Param("password") String password);
}
