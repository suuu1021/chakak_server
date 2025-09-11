package com.green.chakak.chakak.chat.repository;

import com.green.chakak.chakak.chat.domain.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findAllByChatRoom_ChatRoomId(Long chatRoomId);

    Optional<ChatMessage> findFirstByChatRoom_ChatRoomIdOrderByCreatedAtDesc(Long chatRoomId);

    long countByChatRoom_ChatRoomIdAndSenderIdNotAndIsReadFalse(Long chatRoomId, Long myUserId);

    @Modifying
    @Query("UPDATE ChatMessage cm SET cm.isRead = true " +
           "WHERE cm.chatRoom.chatRoomId = :chatRoomId AND cm.senderId != :myUserId AND cm.isRead = false")
    int markAllAsRead(@Param("chatRoomId") Long chatRoomId, @Param("myUserId") Long myUserId);
}
