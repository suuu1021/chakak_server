package com.green.chakak.chakak.account.service.repository;

import com.green.chakak.chakak.account.domain.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserTypeRepository extends JpaRepository<UserType, Long> {
    Optional<UserType> findByTypeCode(String typeCode);
}
