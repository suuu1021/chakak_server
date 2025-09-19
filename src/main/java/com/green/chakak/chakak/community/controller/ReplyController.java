package com.green.chakak.chakak.community.controller;

import com.green.chakak.chakak._global.utils.ApiUtil;
import com.green.chakak.chakak.account.domain.LoginUser;
import com.green.chakak.chakak.community.service.PostResponse;
import com.green.chakak.chakak.community.service.ReplyRequest;
import com.green.chakak.chakak.community.service.ReplyResponse;
import com.green.chakak.chakak.community.service.ReplyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService replyService;

    /**
     * 댓글 작성
     * POST /api/posts/{postId}/replies
     */
    @PostMapping("/{postId}/replies")
    public ResponseEntity<?> createReply(
            @PathVariable Long postId,
            @Valid @RequestBody ReplyRequest.CreateDTO request,
            BindingResult bindingResult,
            @RequestAttribute LoginUser loginUser) {

        if (bindingResult.hasErrors()) {
            // 검증 실패 → 에러 메시지 확인
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        ReplyResponse.CreateDTO response = replyService.createReply(postId, request, loginUser);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    /**
     * 특정 게시글의 댓글 목록 조회 (로그인 필요)
     * GET /api/posts/{postId}/replies
     */
    @GetMapping("/{postId}/replies")
    public ResponseEntity<?> getRepliesByPost(
            @PathVariable Long postId,
            @RequestAttribute LoginUser loginUser) {

        List<PostResponse.ReplyDTO> response = replyService.getRepliesByPost(postId, loginUser);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    /**
     * 댓글 수정 (작성자만 가능)
     * PUT /api/replies/{replyId}
     */
    @PutMapping("/replies/{replyId}")
    public ResponseEntity<?> updateReply(
            @PathVariable Long replyId,
            @Valid @RequestBody ReplyRequest.UpdateDTO request,
            BindingResult bindingResult,
            @RequestAttribute LoginUser loginUser) {

        if (bindingResult.hasErrors()) {
            // 검증 실패 → 에러 메시지 확인
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        ReplyResponse.UpdateDTO response = replyService.updateReply(replyId, request, loginUser);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    /**
     * 댓글 삭제 (작성자만 가능)
     * DELETE /api/replies/{replyId}
     */
    @DeleteMapping("/replies/{replyId}")
    public ResponseEntity<?> deleteReply(
            @PathVariable Long replyId,
            @RequestAttribute LoginUser loginUser) {

        replyService.deleteReply(replyId, loginUser);
        return ResponseEntity.ok(new ApiUtil<>("댓글이 삭제되었습니다."));
    }

    /**
     * 특정 사용자의 댓글 목록 조회
     * GET /api/replies/user/{userId}
     */
    @GetMapping("/replies/user/{userId}")
    public ResponseEntity<?> getUserReplies(
            @PathVariable Long userId,
            @RequestAttribute(required = false) LoginUser loginUser) {

        List<PostResponse.ReplyDTO> response = replyService.getUserReplies(userId, loginUser);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    /**
     * 특정 사용자의 모든댓글 목록 조회
     * GET /api/replies/user/{userId}
     */
    @GetMapping("/replies/user/{userId}/all")
    public ResponseEntity<?> getUserAllReplies(
            @PathVariable Long userId,
            @RequestAttribute(required = false) LoginUser loginUser) {

        List<PostResponse.ReplyDTO> response = replyService.getUserALLReplies(userId, loginUser);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }





    /**
     * 내가 작성한 댓글 목록 조회 (로그인 필요)
     * GET /api/replies/my-replies
     */
    @GetMapping("/replies/my-replies")
    public ResponseEntity<?> getMyReplies(@RequestAttribute LoginUser loginUser) {

        List<PostResponse.ReplyDTO> response = replyService.getMyReplies(loginUser);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }
}