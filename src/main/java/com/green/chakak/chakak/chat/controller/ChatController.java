package com.green.chakak.chakak.chat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.green.chakak.chakak.chat.service.ChatMessageDto;
import com.green.chakak.chakak.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final ObjectMapper objectMapper; // JSON 변환을 위해 추가

    /**
     * WebSocket 메시지 처리를 위한 핸들러 메소드
     * 클라이언트가 "/app/chat/room/{roomId}" 경로로 메시지를 보내면 이 메소드가 호출됩니다.
     * @param roomId 채팅방 ID
     * @param payload 클라이언트로부터 받은 순수 JSON 문자열
     */
    @MessageMapping("/chat/room/{roomId}")
    public void sendMessage(@DestinationVariable Long roomId, @Payload String payload) {
        log.info("RAW JSON received for room {}: {}", roomId, payload);

        try {
            // 수신한 JSON 문자열을 DTO로 직접 변환 시도
            ChatMessageDto messageDto = objectMapper.readValue(payload, ChatMessageDto.class);
            
            // 경로에서 추출한 roomId를 DTO에 설정
            messageDto.setChatRoomId(roomId);
            
            log.info("JSON successfully deserialized. Processing message...");
            // 서비스로 메시지 처리 위임
            chatService.processMessage(messageDto);
        } catch (Exception e) {
            log.error("!!!!! JSON Deserialization FAILED !!!!! for payload: {}", payload, e);
        }
    }
}
