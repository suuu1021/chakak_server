package com.green.chakak.chakak.chat.service;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 채팅방 생성/조회 요청 DTO
 * userProfileId와 photographerProfileId 중 하나는 반드시 값이 있어야 합니다.
 */
@Getter
@NoArgsConstructor
public class ChatRoomRequestDto {
    private Long userProfileId;
    private Long photographerProfileId;
}
