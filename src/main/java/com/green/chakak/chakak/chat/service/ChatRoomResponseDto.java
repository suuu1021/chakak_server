package com.green.chakak.chakak.chat.service;

import com.green.chakak.chakak.chat.domain.ChatRoom;
import lombok.Getter;

@Getter
public class ChatRoomResponseDto {
    private final Long chatRoomId;

    private ChatRoomResponseDto(Long chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public static ChatRoomResponseDto from(ChatRoom chatRoom) {
        return new ChatRoomResponseDto(chatRoom.getChatRoomId());
    }
}
