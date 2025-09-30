package com.green.chakak.chakak.chat.repository;

import com.green.chakak.chakak.account.domain.User;
import com.green.chakak.chakak.account.domain.UserProfile;
import com.green.chakak.chakak.chat.domain.ChatRoom;
import com.green.chakak.chakak.photographer.domain.PhotographerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByUserProfileAndPhotographerProfile(UserProfile userProfile, PhotographerProfile photographerProfile);

    /**
     * [수정] 특정 사용자가 참여하고 있는 모든 채팅방을 조회합니다.
     * UserProfile 또는 PhotographerProfile에 연결된 User를 기준으로 조회합니다.
     */
    @Query("SELECT cr FROM ChatRoom cr " +
           "WHERE cr.userProfile.user = :user OR cr.photographerProfile.user = :user " +
           "ORDER BY cr.createdAt DESC")
    List<ChatRoom> findAllByUser(@Param("user") User user);
}
