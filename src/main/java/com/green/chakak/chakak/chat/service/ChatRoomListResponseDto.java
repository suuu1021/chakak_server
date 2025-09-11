package com.green.chakak.chakak.chat.service;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ChatRoomListResponseDto {

    private final Long chatRoomId;
    private final String opponentNickname;
    private final String opponentProfileImageUrl; // 상대방 프로필 이미지 URL
    private final String lastMessage;
    private final String lastMessageCreatedAt;
    private final long unreadMessageCount; // 안 읽은 메시지 수

    @Builder
    public ChatRoomListResponseDto(Long chatRoomId, String opponentNickname, String opponentProfileImageUrl, String lastMessage, String lastMessageCreatedAt, long unreadMessageCount) {
        this.chatRoomId = chatRoomId;
        this.opponentNickname = opponentNickname;
        this.opponentProfileImageUrl = opponentProfileImageUrl;
        this.lastMessage = lastMessage;
        this.lastMessageCreatedAt = lastMessageCreatedAt;
        this.unreadMessageCount = unreadMessageCount;
    }
}
