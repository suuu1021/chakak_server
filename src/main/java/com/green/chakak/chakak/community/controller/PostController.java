package com.green.chakak.chakak.community.controller;

import com.green.chakak.chakak._global.utils.ApiUtil;
import com.green.chakak.chakak.account.domain.LoginUser;
import com.green.chakak.chakak.community.service.PostRequest;
import com.green.chakak.chakak.community.service.PostResponse;
import com.green.chakak.chakak.community.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 게시글 작성 (Base64 이미지 포함)
    @PostMapping
    public ResponseEntity<?> createPost(
            @Valid @RequestBody PostRequest.CreateDTO request,
            BindingResult bindingResult,
            @RequestAttribute LoginUser loginUser) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        PostResponse.CreateDTO response = postService.createPost(request, loginUser);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    @GetMapping("/list")
    public ResponseEntity<?> getPostList(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String userType,
            @RequestParam(defaultValue = "LATEST") String sortBy,
            @RequestAttribute(required = false) LoginUser loginUser) {

        PostRequest.ListDTO request = new PostRequest.ListDTO();
        request.setPage(page);
        request.setSize(size);
        request.setKeyword(keyword);
        request.setUserType(userType);
        request.setSortBy(sortBy);

        PostResponse.PageDTO response = postService.getPostList(request, loginUser);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<?> getPostDetail(
            @PathVariable Long postId,
            @RequestAttribute LoginUser loginUser) {

        PostResponse.DetailDTO response = postService.getPostDetail(postId, loginUser);
        log.error(String.valueOf(response));
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    // 게시글 수정 (Base64 이미지 포함)
    @PutMapping("/{postId}")
    public ResponseEntity<?> updatePost(
            @PathVariable Long postId,
            @Valid @RequestBody PostRequest.UpdateDTO request,
            BindingResult bindingResult,
            LoginUser loginUser) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        PostResponse.UpdateDTO response = postService.updatePost(postId, request, loginUser);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(
            @PathVariable Long postId,
            @RequestAttribute LoginUser loginUser) {

        postService.deletePost(postId, loginUser);
        return ResponseEntity.ok(new ApiUtil<>("삭제 처리되었습니다."));
    }

    @GetMapping("/popular")
    public ResponseEntity<?> getPopularPosts(
            @RequestParam(defaultValue = "5") int limit,
            LoginUser loginUser) {

        List<PostResponse.ListDTO> response = postService.getPopularPosts(5, loginUser);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserPosts(@PathVariable Long userId, LoginUser loginUser) {

        List<PostResponse.ListDTO> response = postService.getUserPosts(userId, loginUser);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    @GetMapping("/my-posts")
    public ResponseEntity<?> getMyPosts(@RequestAttribute LoginUser loginUser) {

        Long userId = loginUser.getId();
        List<PostResponse.ListDTO> response = postService.getUserPosts(userId, loginUser);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }
}