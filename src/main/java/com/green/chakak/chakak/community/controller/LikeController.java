package com.green.chakak.chakak.community.controller;

import com.green.chakak.chakak._global.utils.ApiUtil;
import com.green.chakak.chakak.account.domain.LoginUser;
import com.green.chakak.chakak.community.service.LikeResponse;
import com.green.chakak.chakak.community.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    /**
     * 좋아요 토글 (중복 방지)
     * POST /api/posts/{postId}/like
     * - 이미 좋아요를 눌렀으면 취소, 안 눌렀으면 추가
     */
    @PostMapping("/{postId}/like")
    public ResponseEntity<?> toggleLike(
            @PathVariable Long postId,
            @RequestAttribute LoginUser loginUser) {

        LikeResponse.ToggleDTO response = likeService.toggleLike(postId, loginUser);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    /**
     * 좋아요 상태 확인
     * GET /api/posts/{postId}/like/status
     * - 현재 사용자가 해당 게시글에 좋아요를 눌렀는지 확인
     */
    @GetMapping("/{postId}/like/status")
    public ResponseEntity<?> getLikeStatus(
            @PathVariable Long postId,
            @RequestAttribute(required = false) LoginUser loginUser) {

        LikeResponse.StatusDTO response = likeService.getLikeStatus(postId, loginUser);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    /**
     * 특정 사용자가 좋아요 누른 게시글 목록 조회
     * GET /api/likes/user/{userId}
     */
    @GetMapping("/likes/user/{userId}")
    public ResponseEntity<?> getUserLikedPosts(@PathVariable Long userId) {

        List<LikeResponse.UserLikeDTO> response = likeService.getUserLikedPosts(userId);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    /**
     * 내가 좋아요 누른 게시글 목록 조회 (로그인 필요)
     * GET /api/likes/my-likes
     */
    @GetMapping("/likes/my-likes")
    public ResponseEntity<?> getMyLikedPosts(@RequestAttribute LoginUser loginUser) {

        List<LikeResponse.UserLikeDTO> response = likeService.getMyLikedPosts(loginUser);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    /**
     * 특정 게시글의 좋아요 누른 사용자 목록 조회
     * GET /api/posts/{postId}/like/likers
     * - 누가 좋아요를 눌렀는지 확인 (닉네임 목록)
     */
    @GetMapping("/{postId}/like/likers")
    public ResponseEntity<?> getPostLikers(@PathVariable Long postId) {

        List<String> response = likeService.getPostLikers(postId);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }
}