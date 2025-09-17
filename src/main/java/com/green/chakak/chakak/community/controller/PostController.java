package com.green.chakak.chakak.community.controller;

import com.green.chakak.chakak._global.utils.ApiUtil;
import com.green.chakak.chakak.account.domain.LoginUser;
import com.green.chakak.chakak.community.service.PostRequest;
import com.green.chakak.chakak.community.service.PostResponse;
import com.green.chakak.chakak.community.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /**
     * 커뮤니티 글 작성
     * POST /api/post
     */
    @PostMapping
    public ResponseEntity<?> createPost(
            @Valid @RequestBody PostRequest.CreateDTO request,
            @RequestAttribute LoginUser loginUser) {

        PostResponse.CreateDTO response = postService.createPost(request, loginUser);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    /**
     * 커뮤니티 글 목록 조회 (비회원도 접근 가능)
     * GET /api/post
     */
    @GetMapping
    public ResponseEntity<?> getPostList(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String userType,
            @RequestParam(defaultValue = "LATEST") String sortBy) {

        PostRequest.ListDTO request = new PostRequest.ListDTO();
        request.setPage(page);
        request.setSize(size);
        request.setKeyword(keyword);
        request.setUserType(userType);
        request.setSortBy(sortBy);

        PostResponse.PageDTO response = postService.getPostList(request);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    /**
     * 커뮤니티 글 상세 조회 (로그인 필요)
     * GET /api/post/{postId}
     */
    @GetMapping("/{postId}")
    public ResponseEntity<?> getPostDetail(
            @PathVariable Long postId,
            @RequestAttribute LoginUser loginUser) {

        PostResponse.DetailDTO response = postService.getPostDetail(postId, loginUser);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    /**
     * 커뮤니티 글 수정 (작성자만 가능)
     * PUT /api/post/{postId}
     */
    @PutMapping("/{postId}")
    public ResponseEntity<?> updatePost(
            @PathVariable Long postId,
            @Valid @RequestBody PostRequest.UpdateDTO request,
            @RequestAttribute LoginUser loginUser) {

        PostResponse.UpdateDTO response = postService.updatePost(postId, request, loginUser);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    /**
     * 커뮤니티 글 삭제 (작성자만 가능)
     * DELETE /api/post/{postId}
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(
            @PathVariable Long postId,
            @RequestAttribute LoginUser loginUser) {

        postService.deletePost(postId, loginUser);
        return ResponseEntity.ok(new ApiUtil<>("삭제 처리되었습니다."));
    }

    /**
     * 인기 게시글 조회 (조회수 기준 상위 N개)
     * GET /api/post/popular
     */
    @GetMapping("/popular")
    public ResponseEntity<?> getPopularPosts(
            @RequestParam(defaultValue = "5") int limit) {

        List<PostResponse.ListDTO> response = postService.getPopularPosts(limit);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    /**
     * 특정 사용자의 게시글 목록 조회
     * GET /api/post/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserPosts(@PathVariable Long userId) {

        List<PostResponse.ListDTO> response = postService.getUserPosts(userId);
        return ResponseEntity.ok(new ApiUtil<>(response));
    }

    /**
     * 내가 작성한 게시글 목록 조회 (로그인 필요)
     * GET /api/post/my-posts
     */
    @GetMapping("/my-posts")
    public ResponseEntity<?> getMyPosts(@RequestAttribute LoginUser loginUser) {


        List<PostResponse.ListDTO> response = postService.getUserPosts(loginUser.getId());
        return ResponseEntity.ok(new ApiUtil<>(response));
    }
}