package com.green.chakak.chakak.chat.controller;

import com.green.chakak.chakak.account.domain.LoginUser;
import com.green.chakak.chakak.chat.domain.ChatRoom;
import com.green.chakak.chakak.chat.service.ChatMessageResponseDto;
import com.green.chakak.chakak.chat.service.ChatRoomListResponseDto;
import com.green.chakak.chakak.chat.service.ChatRoomRequestDto;
import com.green.chakak.chakak.chat.service.ChatRoomResponseDto;
import com.green.chakak.chakak.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatRoomController {

    private final ChatService chatService;

    /**
     * 채팅방을 생성하거나 기존 채팅방을 조회합니다.
     * 모든 비즈니스 로직은 ChatService로 위임합니다.
     */
    @PostMapping("/rooms")
    public ResponseEntity<ChatRoomResponseDto> createOrFindRoom(@RequestBody ChatRoomRequestDto requestDto, LoginUser loginUser) {
        // 컨트롤러는 요청과 로그인 정보를 서비스로 전달하기만 합니다.
        ChatRoom chatRoom = chatService.findOrCreateRoom(requestDto, loginUser);
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
