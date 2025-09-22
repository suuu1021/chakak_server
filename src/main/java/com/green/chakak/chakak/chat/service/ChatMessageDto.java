package com.green.chakak.chakak.chat.service;

import com.green.chakak.chakak.chat.domain.ChatMessage;
import com.green.chakak.chakak.chat.domain.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
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

    // 이미지 관련 새 필드들
    private String imageBase64; // 클라이언트에서 받을 base64 데이터
    private String imageOriginalName; // 원본 파일명

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
                .imageOriginalName(this.imageOriginalName)
                .isRead(false) // 메시지는 기본적으로 읽지 않음 상태로 생성
                .build();
    }
}
