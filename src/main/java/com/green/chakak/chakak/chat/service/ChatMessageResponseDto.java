package com.green.chakak.chakak.chat.service;

import com.green.chakak.chakak.chat.domain.ChatMessage;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class ChatMessageResponseDto {

    private final Long chatMessageId;
    private final Long chatRoomId;
    private final ChatMessage.SenderType senderType;
    private final Long senderId;
    private final ChatMessage.MessageType messageType;
    private final String message;
    private final Integer paymentAmount;
    private final String paymentOrderId;
    private final String createdAt;

    @Builder
    private ChatMessageResponseDto(Long chatMessageId, Long chatRoomId, ChatMessage.SenderType senderType, Long senderId, ChatMessage.MessageType messageType, String message, Integer paymentAmount, String paymentOrderId, String createdAt) {
        this.chatMessageId = chatMessageId;
        this.chatRoomId = chatRoomId;
        this.senderType = senderType;
        this.senderId = senderId;
        this.messageType = messageType;
        this.message = message;
        this.paymentAmount = paymentAmount;
        this.paymentOrderId = paymentOrderId;
        this.createdAt = createdAt;
    }

    // chatmessage -> dto 변환
    public static ChatMessageResponseDto from(ChatMessage entity) {
        return ChatMessageResponseDto.builder()
                .chatMessageId(entity.getChatMessageId())
                .chatRoomId(entity.getChatRoom().getChatRoomId())
                .senderType(entity.getSenderType())
                .senderId(entity.getSenderId())
                .messageType(entity.getMessageType())
                .message(entity.getMessage())
                .paymentAmount(entity.getPaymentAmount())
                .paymentOrderId(entity.getPaymentOrderId())
                .createdAt(entity.getCreatedAt().toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }
}
