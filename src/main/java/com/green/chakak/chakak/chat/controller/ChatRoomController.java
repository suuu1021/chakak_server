package com.green.chakak.chakak.chat.controller;

import com.green.chakak.chakak.account.domain.LoginUser;
import com.green.chakak.chakak.account.domain.UserProfile;
import com.green.chakak.chakak.account.service.repository.UserProfileJpaRepository;
import com.green.chakak.chakak.chat.domain.ChatRoom;
import com.green.chakak.chakak.chat.service.ChatMessageResponseDto;
import com.green.chakak.chakak.chat.service.ChatRoomListResponseDto;
import com.green.chakak.chakak.chat.service.ChatRoomRequestDto;
import com.green.chakak.chakak.chat.service.ChatRoomResponseDto;
import com.green.chakak.chakak.chat.service.ChatService;
import com.green.chakak.chakak._global.errors.exception.Exception404;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatRoomController {

    private final ChatService chatService;
    private final UserProfileJpaRepository userProfileJpaRepository;

    @PostMapping("/rooms")
    public ResponseEntity<ChatRoomResponseDto> createOrFindRoom(@RequestBody ChatRoomRequestDto requestDto, LoginUser loginUser) {
        UserProfile userProfile = userProfileJpaRepository.findByUserId(loginUser.getId())
                .orElseThrow(() -> new Exception404("로그인한 유저의 프로필을 찾을 수 없습니다."));

        ChatRoom chatRoom = chatService.findOrCreateRoom(userProfile.getUserProfileId(), requestDto.getPhotographerProfileId());

        return ResponseEntity.ok(ChatRoomResponseDto.from(chatRoom));
    }

    @GetMapping("/rooms/{chatRoomId}/messages")
    public ResponseEntity<List<ChatMessageResponseDto>> getMessages(@PathVariable Long chatRoomId, LoginUser loginUser) {
        List<ChatMessageResponseDto> messages = chatService.getMessagesByRoomId(chatRoomId, loginUser);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/my/rooms")
    public ResponseEntity<List<ChatRoomListResponseDto>> getMyChatRooms(LoginUser loginUser) {
        List<ChatRoomListResponseDto> myChatRooms = chatService.getMyChatRooms(loginUser);
        return ResponseEntity.ok(myChatRooms);
    }


    // 유저가 채팅방 입장하면 호출되는 읽음 처리 기능
    @PostMapping("/rooms/{chatRoomId}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long chatRoomId, LoginUser loginUser) {
        chatService.markMessagesAsRead(chatRoomId, loginUser);
        return ResponseEntity.ok().build();
    }
}
