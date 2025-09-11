package com.green.chakak.chakak.chat.service;

import com.green.chakak.chakak.chat.domain.ChatMessage;
import com.green.chakak.chakak.chat.domain.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {

    private Long chatRoomId;
    private ChatMessage.SenderType senderType;
    private Long senderId;
    private ChatMessage.MessageType messageType;
    private String message;
    private Integer paymentAmount;
    private String paymentOrderId;

    //dto -> chatmessage 변환
    public ChatMessage toEntity(ChatRoom chatRoom) {
        return ChatMessage.builder()
                .chatRoom(chatRoom)
                .senderType(this.senderType)
                .senderId(this.senderId)
                .messageType(this.messageType)
                .message(this.message)
                .paymentAmount(this.paymentAmount)
                .paymentOrderId(this.paymentOrderId)
                .isRead(false) // 메시지는 기본적으로 읽지 않음 상태로 생성
                .build();
    }
}
