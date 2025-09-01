package com.green.chakak.chakak.account.user;

import org.hibernate.usertype.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    //Optional<User> findByNickname(String nickname);
    
    //Optional<User> findBySocialTypeAndSocialId(User.SocialType socialType, String socialId);
    
    //Optional<User> findByRefreshToken(String refreshToken);
    
    boolean existsByEmail(String email);
    
    boolean existsByNickname(String nickname);
    
//    @Query("SELECT m FROM user m WHERE m.userType = :userType AND m.status = 'ACTIVE'")
//    Optional<User> findActivePhotographers(@Param("userType") UserType userType);
}
