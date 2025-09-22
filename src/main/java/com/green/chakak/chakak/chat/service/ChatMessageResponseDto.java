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

    // 이미지 관련 새 필드들
    private final String imageUrl;
    private final String imageOriginalName;

    @Builder
    private ChatMessageResponseDto(Long chatMessageId, Long chatRoomId, ChatMessage.SenderType senderType, Long senderId, ChatMessage.MessageType messageType, String message, Integer paymentAmount, String paymentOrderId, String createdAt, String imageUrl, String imageOriginalName) {
        this.chatMessageId = chatMessageId;
        this.chatRoomId = chatRoomId;
        this.senderType = senderType;
        this.senderId = senderId;
        this.messageType = messageType;
        this.message = message;
        this.paymentAmount = paymentAmount;
        this.paymentOrderId = paymentOrderId;
        this.createdAt = createdAt;

        // 새 필드들
        this.imageUrl = imageUrl;
        this.imageOriginalName = imageOriginalName;
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
                .imageUrl(entity.getImageUrl())
                .imageOriginalName(entity.getImageOriginalName())
                .createdAt(entity.getCreatedAt().toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }
}
