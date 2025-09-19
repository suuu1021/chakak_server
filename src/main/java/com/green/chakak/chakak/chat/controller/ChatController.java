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
    private final ObjectMapper objectMapper;

    @MessageMapping("/chat/room/{roomId}")
    public void sendMessage(@DestinationVariable Long roomId, @Payload String payload) {
        log.info("RAW JSON received for room {}: {}", roomId, payload);

        try {
            ChatMessageDto messageDto = objectMapper.readValue(payload, ChatMessageDto.class);
            messageDto.setChatRoomId(roomId);
            log.info("JSON successfully deserialized. Processing message...");
            chatService.processMessage(messageDto);
        } catch (Exception e) {
            log.error("!!!!! JSON Deserialization FAILED !!!!! for payload: {}", payload, e);
        }
    }
}
