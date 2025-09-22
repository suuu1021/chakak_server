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

    @PostMapping("/{postId}/like")
    public ResponseEntity<?> toggleLike(
            @PathVariable Long postId,
            @RequestAttribute LoginUser loginUser) {

        LikeResponse.ToggleDTO response = likeService.toggleLike(postId, loginUser);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }


    @GetMapping("/{postId}/like/status")
    public ResponseEntity<?> getLikeStatus(
            @PathVariable Long postId,
            @RequestAttribute(required = false) LoginUser loginUser) {

        LikeResponse.StatusDTO response = likeService.getLikeStatus(postId, loginUser);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }


    @GetMapping("/likes/user/{userId}")
    public ResponseEntity<?> getUserLikedPosts(@PathVariable Long userId) {

        List<LikeResponse.UserLikeDTO> response = likeService.getUserLikedPosts(userId);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }


    @GetMapping("/likes/my-likes")
    public ResponseEntity<?> getMyLikedPosts(@RequestAttribute LoginUser loginUser) {

        List<LikeResponse.UserLikeDTO> response = likeService.getMyLikedPosts(loginUser);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }


    @GetMapping("/{postId}/like/likers")
    public ResponseEntity<?> getPostLikers(@PathVariable Long postId) {

        List<String> response = likeService.getPostLikers(postId);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }
}