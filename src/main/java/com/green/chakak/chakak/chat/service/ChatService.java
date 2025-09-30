package com.green.chakak.chakak.chat.service;

import com.green.chakak.chakak._global.errors.exception.Exception400;
import com.green.chakak.chakak._global.errors.exception.Exception403;
import com.green.chakak.chakak._global.errors.exception.Exception404;
import com.green.chakak.chakak._global.utils.ChatFileUploadUtil;
import com.green.chakak.chakak.account.domain.LoginUser;
import com.green.chakak.chakak.account.domain.User;
import com.green.chakak.chakak.account.domain.UserProfile;
import com.green.chakak.chakak.account.service.repository.UserJpaRepository;
import com.green.chakak.chakak.account.service.repository.UserProfileJpaRepository;
import com.green.chakak.chakak.chat.domain.ChatMessage;
import com.green.chakak.chakak.chat.domain.ChatRoom;
import com.green.chakak.chakak.chat.repository.ChatMessageRepository;
import com.green.chakak.chakak.chat.repository.ChatRoomRepository;
import com.green.chakak.chakak.photographer.domain.PhotographerProfile;
import com.green.chakak.chakak.photographer.service.repository.PhotographerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserJpaRepository userJpaRepository;
    private final UserProfileJpaRepository userProfileJpaRepository;
    private final PhotographerRepository photographerRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatFileUploadUtil chatFileUploadUtil;

    public ChatRoom findOrCreateRoom(ChatRoomRequestDto requestDto, LoginUser loginUser) {
        UserProfile userProfile;
        PhotographerProfile photographerProfile;

        String role = loginUser.getUserTypeName();

        if ("user".equalsIgnoreCase(role)) {
            userProfile = userProfileJpaRepository.findByUserId(loginUser.getId())
                    .orElseThrow(() -> new Exception404("로그인한 유저의 프로필을 찾을 수 없습니다. ID: " + loginUser.getId()));

            if (requestDto.getPhotographerProfileId() == null) {
                throw new Exception400("상대방(사진작가)의 프로필 ID가 필요합니다.");
            }
            photographerProfile = photographerRepository.findById(requestDto.getPhotographerProfileId())
                    .orElseThrow(() -> new Exception404("해당 사진작가 프로필을 찾을 수 없습니다: " + requestDto.getPhotographerProfileId()));

        } else if ("photographer".equalsIgnoreCase(role)) {
            photographerProfile = photographerRepository.findByUser_UserId(loginUser.getId())
                    .orElseThrow(() -> new Exception404("로그인한 작가의 프로필을 찾을 수 없습니다. ID: " + loginUser.getId()));

            if (requestDto.getUserProfileId() == null) {
                throw new Exception400("상대방(유저)의 프로필 ID가 필요합니다.");
            }
            userProfile = userProfileJpaRepository.findById(requestDto.getUserProfileId())
                    .orElseThrow(() -> new Exception404("해당 유저 프로필을 찾을 수 없습니다: " + requestDto.getUserProfileId()));
        } else {
            throw new Exception403("채팅방을 생성할 수 없는 사용자 유형입니다: " + role);
        }

        return chatRoomRepository.findByUserProfileAndPhotographerProfile(userProfile, photographerProfile)
                .orElseGet(() -> {
                    log.info("Creating new chat room between user {} and photographer {}", userProfile.getUserProfileId(), photographerProfile.getPhotographerProfileId());
                    ChatRoom newChatRoom = ChatRoom.builder()
                            .userProfile(userProfile)
                            .photographerProfile(photographerProfile)
                            .build();
                    return chatRoomRepository.save(newChatRoom);
                });
    }

    public void processMessage(ChatMessageDto messageDto) {
        log.info("--- 3. ChatService: processMessage 시작 (Room ID: {}) ---", messageDto.getChatRoomId());
        ChatRoom chatRoom = chatRoomRepository.findById(messageDto.getChatRoomId())
                .orElseThrow(() -> new Exception404("채팅방을 찾을 수 없습니다: " + messageDto.getChatRoomId()));

        ChatMessage chatMessage;

        // 1. 이미지 메시지 처리
        if (messageDto.getMessageType() == ChatMessage.MessageType.IMAGE &&
                messageDto.getImageBase64() != null && !messageDto.getImageBase64().trim().isEmpty()) {

            try {
                log.info("--- 4. ChatService: 이미지 메시지로 판단. 파일 저장 로직 호출 시작 ---");
                String imageUrl = chatFileUploadUtil.saveChatImage(
                        messageDto.getImageBase64(),
                        messageDto.getImageOriginalName()
                );
                log.info("--- 5. ChatService: 파일 저장 완료. 반환된 이미지 URL: {} ---", imageUrl);

                chatMessage = ChatMessage.builder()
                        .chatRoom(chatRoom)
                        .senderType(messageDto.getSenderType())
                        .senderId(messageDto.getSenderId())
                        .messageType(ChatMessage.MessageType.IMAGE)
                        .message(messageDto.getMessage())
                        .imageUrl(imageUrl)
                        .imageOriginalName(messageDto.getImageOriginalName())
                        .isRead(false)
                        .build();
                log.info("--- 6. ChatService: 이미지 URL이 포함된 ChatMessage 엔티티 생성 완료 ---");

            } catch (Exception e) {
                log.error("!!! ChatService: 이미지 처리 중 오류 발생 !!!", e);
                chatMessage = ChatMessage.builder()
                        .chatRoom(chatRoom)
                        .senderType(messageDto.getSenderType())
                        .senderId(messageDto.getSenderId())
                        .messageType(ChatMessage.MessageType.TEXT)
                        .message("이미지 전송에 실패했습니다: " + e.getMessage())
                        .isRead(false)
                        .build();
                log.warn("--- 6. ChatService: 이미지 처리 실패로, 실패 안내 메시지 엔티티 생성 ---");
            }

            // 2. 결제 요청 메시지 처리
        } else if (messageDto.getMessageType() == ChatMessage.MessageType.PAYMENT_REQUEST) {
            log.info("--- 4. ChatService: 결제 요청 메시지로 판단 ---");

            if (messageDto.getBookingInfoId() == null) {
                throw new Exception400("PAYMENT_REQUEST 메시지에는 bookingInfoId가 반드시 필요합니다.");
            }

            chatMessage = ChatMessage.builder()
                    .chatRoom(chatRoom)
                    .senderType(messageDto.getSenderType())
                    .senderId(messageDto.getSenderId())
                    .messageType(ChatMessage.MessageType.PAYMENT_REQUEST)
                    .message(messageDto.getMessage())
                    .paymentAmount(messageDto.getPaymentAmount())
                    .paymentDescription(messageDto.getPaymentDescription())
                    .photoServiceInfoId(messageDto.getPhotoServiceInfoId())
                    .priceInfoId(messageDto.getPriceInfoId())
                    .bookingInfoId(messageDto.getBookingInfoId())
                    .isRead(false)
                    .build();
            log.info("--- 6. ChatService: 결제 요청 ChatMessage 엔티티 생성 완료 ---");

            // 3. 텍스트 메시지 처리
        } else {
            log.info("--- 4. ChatService: 텍스트 메시지로 판단 ---");
            chatMessage = messageDto.toEntity(chatRoom);
            log.info("--- 6. ChatService: ChatMessage 엔티티 생성 완료 ---");
        }

        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);
        log.info("--- 7. ChatService: DB에 메시지 저장 완료 (Message ID: {}) ---", savedMessage.getChatMessageId());

        ChatMessageResponseDto responseDto = ChatMessageResponseDto.from(savedMessage);
        log.info("--- 8. ChatService: 브로드캐스팅 시작 (Destination: /topic/chat/room/{}) ---", responseDto.getChatRoomId());
        messagingTemplate.convertAndSend("/topic/chat/room/" + responseDto.getChatRoomId(), responseDto);
        log.info("--- 9. ChatService: processMessage 종료 ---");
    }

    @Transactional(readOnly = true)
    public List<ChatMessageResponseDto> getMessagesByRoomId(Long chatRoomId, LoginUser loginUser) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new Exception404("채팅방을 찾을 수 없습니다: " + chatRoomId));

        verifyChatRoomAccess(chatRoom, loginUser);

        List<ChatMessage> messages = chatMessageRepository.findAllByChatRoom_ChatRoomId(chatRoomId);
        return messages.stream()
                .map(ChatMessageResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ChatRoomListResponseDto> getMyChatRooms(LoginUser loginUser) {
        User user = userJpaRepository.findById(loginUser.getId())
                .orElseThrow(() -> new Exception404("로그인 정보를 찾을 수 없습니다."));

        List<ChatRoom> chatRooms = chatRoomRepository.findAllByUser(user);

        return chatRooms.stream().map(chatRoom -> {
            String opponentNickname;
            String opponentProfileImageUrl = "";
            if (chatRoom.getUserProfile().getUser().equals(user)) {
                PhotographerProfile opponent = chatRoom.getPhotographerProfile();
                opponentNickname = opponent.getBusinessName();
            } else {
                UserProfile opponent = chatRoom.getUserProfile();
                opponentNickname = opponent.getNickName();
            }

            Optional<ChatMessage> lastMessageOpt = chatMessageRepository.findFirstByChatRoom_ChatRoomIdOrderByCreatedAtDesc(chatRoom.getChatRoomId());
            String lastMessageContent = lastMessageOpt.map(ChatMessage::getMessage).orElse("아직 대화 내용이 없습니다.");
            String lastMessageTime = lastMessageOpt.map(msg -> msg.getCreatedAt().toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))).orElse("");

            long unreadCount = chatMessageRepository.countByChatRoom_ChatRoomIdAndSenderIdNotAndIsReadFalse(chatRoom.getChatRoomId(), loginUser.getId());

            return ChatRoomListResponseDto.builder()
                    .chatRoomId(chatRoom.getChatRoomId())
                    .opponentNickname(opponentNickname)
                    .opponentProfileImageUrl(opponentProfileImageUrl)
                    .lastMessage(lastMessageContent)
                    .lastMessageCreatedAt(lastMessageTime)
                    .unreadMessageCount(unreadCount)
                    .build();
        }).collect(Collectors.toList());
    }

    public void markMessagesAsRead(Long chatRoomId, LoginUser loginUser) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new Exception404("채팅방을 찾을 수 없습니다: " + chatRoomId));

        verifyChatRoomAccess(chatRoom, loginUser);

        chatMessageRepository.markAllAsRead(chatRoomId, loginUser.getId());
    }

    private void verifyChatRoomAccess(ChatRoom chatRoom, LoginUser loginUser) {
        Long loginUserId = loginUser.getId();
        boolean isParticipant = chatRoom.getUserProfile().getUser().getUserId().equals(loginUserId) ||
                chatRoom.getPhotographerProfile().getUser().getUserId().equals(loginUserId);

        if (!isParticipant) {
            throw new Exception403("해당 채팅방에 접근할 권한이 없습니다.");
        }
    }
}
