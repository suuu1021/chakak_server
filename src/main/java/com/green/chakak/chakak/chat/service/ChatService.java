package com.green.chakak.chakak.chat.service;

import com.green.chakak.chakak._global.errors.exception.Exception400;
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
        log.info("Processing message in service for room {}", messageDto.getChatRoomId());
        ChatRoom chatRoom = chatRoomRepository.findById(messageDto.getChatRoomId())
                .orElseThrow(() -> new Exception404("채팅방을 찾을 수 없습니다: " + messageDto.getChatRoomId()));

        ChatMessage chatMessage = messageDto.toEntity(chatRoom);
        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);

        ChatMessageResponseDto responseDto = ChatMessageResponseDto.from(savedMessage);
        log.info("Broadcasting message to /topic/chat/room/{}", responseDto.getChatRoomId());
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
