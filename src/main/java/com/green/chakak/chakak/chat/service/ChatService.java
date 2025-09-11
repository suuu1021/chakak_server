package com.green.chakak.chakak.chat.service;

import com.green.chakak.chakak._global.errors.exception.Exception403;
import com.green.chakak.chakak._global.errors.exception.Exception404;
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
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public ChatRoom findOrCreateRoom(Long userProfileId, Long photographerProfileId) {
        UserProfile userProfile = userProfileJpaRepository.findById(userProfileId)
                .orElseThrow(() -> new Exception404("해당 유저 프로필을 찾을 수 없습니다: " + userProfileId));

        PhotographerProfile photographerProfile = photographerRepository.findById(photographerProfileId)
                .orElseThrow(() -> new Exception404("해당 사진작가 프로필을 찾을 수 없습니다: " + photographerProfileId));

        //UserProfile을 직접 사용하여 채팅방 조회
        return chatRoomRepository.findByUserProfileAndPhotographerProfile(userProfile, photographerProfile)
                .orElseGet(() -> {
                    // UserProfile을 사용하여 채팅방 생성
                    ChatRoom newChatRoom = ChatRoom.builder()
                            .userProfile(userProfile)
                            .photographerProfile(photographerProfile)
                            .build();
                    return chatRoomRepository.save(newChatRoom);
                });
    }

    public void processMessage(ChatMessageDto messageDto) {
        ChatRoom chatRoom = chatRoomRepository.findById(messageDto.getChatRoomId())
                .orElseThrow(() -> new Exception404("채팅방을 찾을 수 없습니다: " + messageDto.getChatRoomId()));

        ChatMessage chatMessage = messageDto.toEntity(chatRoom);
        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);

        ChatMessageResponseDto responseDto = ChatMessageResponseDto.from(savedMessage);
        messagingTemplate.convertAndSend("/topic/chat/room/" + responseDto.getChatRoomId(), responseDto);
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
            // UserProfile의 User를 기준으로 상대방 식별
            if (chatRoom.getUserProfile().getUser().equals(user)) {
                PhotographerProfile opponent = chatRoom.getPhotographerProfile();
                opponentNickname = opponent.getBusinessName();
            } else {
                //User가 아닌 UserProfile에서 닉네임 조회
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
        // UserProfile을 통해 User ID에 접근
        boolean isParticipant = chatRoom.getUserProfile().getUser().getUserId().equals(loginUserId) ||
                chatRoom.getPhotographerProfile().getUser().getUserId().equals(loginUserId);

        if (!isParticipant) {
            throw new Exception403("해당 채팅방에 접근할 권한이 없습니다.");
        }
    }
}
