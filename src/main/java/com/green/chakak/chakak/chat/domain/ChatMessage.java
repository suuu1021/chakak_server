package com.green.chakak.chakak.chat.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.ColumnDefault;

import java.sql.Timestamp;

@Entity
@Table(name = "chat_message")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_message_id")
    private Long chatMessageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @Enumerated(EnumType.STRING)
    @Column(name = "sender_type", nullable = false)
    private SenderType senderType;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false)
    private MessageType messageType;

    @Column(columnDefinition = "TEXT")
    private String message;

    // ChatMessage 엔티티에 필드 추가
    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "image_original_name")
    private String imageOriginalName;

    @Column(name = "payment_amount")
    private Integer paymentAmount;

    @Column(name = "payment_order_id")
    private String paymentOrderId;

    @Column(name = "is_read", nullable = false)
    @ColumnDefault("false")
    private boolean isRead;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    // 결제 관련 추가 필드들
    @Column(name = "payment_description")
    private String paymentDescription;

    @Column(name = "photo_service_info_id")
    private Long photoServiceInfoId;

    @Column(name = "price_info_id")
    private Long priceInfoId;

    @Column(name = "booking_info_id")
    private Long bookingInfoId;

    public enum SenderType {
        USER, PHOTOGRAPHER
    }

    public enum MessageType {
        TEXT, PAYMENT_REQUEST, IMAGE
    }


}
