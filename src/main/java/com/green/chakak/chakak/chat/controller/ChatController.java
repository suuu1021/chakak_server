package com.green.chakak.chakak.chat.controller;

import com.green.chakak.chakak.chat.service.ChatMessageDto;
import com.green.chakak.chakak.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    /**
     * WebSocket 메시지 처리를 위한 핸들러 메소드
     * 클라이언트가 "/app/chat/room/{roomId}" 경로로 메시지를 보내면 이 메소드가 호출됩니다.
     * @param roomId 채팅방 ID
     * @param messageDto 클라이언트로부터 받은 메시지 데이터
     */
    @MessageMapping("/chat/room/{roomId}")
    public void sendMessage(@DestinationVariable Long roomId, ChatMessageDto messageDto) {
        // 경로에서 추출한 roomId를 DTO에 설정
        messageDto.setChatRoomId(roomId);
        // 서비스로 메시지 처리 위임
        chatService.processMessage(messageDto);
    }
}
