package com.green.chakak.chakak.account.userType;



import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserTypeRepository extends JpaRepository<UserType, Long> {
    Optional<UserType> findByTypeCode(String typeCode);
}

