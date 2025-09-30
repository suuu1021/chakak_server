package com.green.chakak.chakak.account.service.repository;

import com.green.chakak.chakak.account.domain.User;
import org.hibernate.usertype.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.password = :password")
    Optional<User> findByEmailAndUserPassword(String email, String password);
    
    boolean existsByEmail(String email);

    @Query("SELECT m FROM User m WHERE m.userType = :userType AND m.status = 'ACTIVE'")
    Optional<User> findActivePhotographers(@Param("userType") UserType userType);
}
