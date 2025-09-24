package com.green.chakak.chakak.community.service;

import com.green.chakak.chakak._global.errors.exception.Exception403;
import com.green.chakak.chakak._global.errors.exception.Exception404;
import com.green.chakak.chakak._global.utils.Define;
import com.green.chakak.chakak._global.utils.FileUploadUtil;
import com.green.chakak.chakak.account.domain.LoginUser;
import com.green.chakak.chakak.account.domain.User;
import com.green.chakak.chakak.account.service.repository.UserJpaRepository;
import com.green.chakak.chakak.community.domain.Post;
import com.green.chakak.chakak.community.repository.LikeJpaRepository;
import com.green.chakak.chakak.community.repository.PostJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostJpaRepository postRepository;
    private final UserJpaRepository userJpaRepository;
    private final LikeJpaRepository likeJpaRepository;
    private final FileUploadUtil fileUploadUtil;


    @Transactional
    public PostResponse.CreateDTO createPost(PostRequest.CreateDTO createDTO, LoginUser loginUser) {

        if (loginUser == null) {
            throw new Exception403("로그인 후 글을 작성할 수 있습니다.");
        }

        User user = userJpaRepository.findById(loginUser.getId())
                .orElseThrow(() -> new Exception404("사용자 정보를 찾을 수 없습니다."));

        String imageUrl = null;

        if (createDTO.getImageData() != null && !createDTO.getImageData().isBlank()) {
            // Base64 이미지 저장
            imageUrl = fileUploadUtil.saveBase64ImageWithType(createDTO.getImageData(), "Image", Define.COMMUNITY);
            validateImageData(createDTO.getImageData());
        }


        Post post = createDTO.toEntity(user);
        if (imageUrl != null) {
            post.setImageUrl(imageUrl);
        }

        Post savedPost = postRepository.save(post);

        return new PostResponse.CreateDTO(savedPost);
    }

    @Transactional(readOnly = true)
    public PostResponse.PageDTO getPostList(PostRequest.ListDTO request, LoginUser loginUser) {

        Sort sort = createSort(request.getSortBy());
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        Page<Post> postPage = getPostsByCondition(request, pageable);

        Long currentUserId = loginUser != null ? loginUser.getId() : null;

        List<PostResponse.ListDTO> content = postPage.getContent().stream()
                .map(post -> new PostResponse.ListDTO(post, currentUserId, likeJpaRepository))
                .collect(Collectors.toList());

        return new PostResponse.PageDTO(
                content,
                postPage.getNumber(),
                postPage.getTotalPages(),
                postPage.getTotalElements(),
                postPage.getSize(),
                postPage.hasNext(),
                postPage.hasPrevious()
        );
    }

    @Transactional
    public PostResponse.DetailDTO getPostDetail(Long postId, LoginUser loginUser) {

        if (loginUser == null) {
            throw new Exception403("로그인 후 게시글을 확인할 수 있습니다.");
        }

        Post post = postRepository.findActiveByIdWithUser(postId)
                .orElseThrow(() -> new Exception404("게시글을 찾을 수 없습니다."));

        if (!post.isOwner(loginUser.getId())) {
            post.increaseViewCount();
        }

        Long currentUserId = loginUser != null ? loginUser.getId() : null;

        return new PostResponse.DetailDTO(post, currentUserId, likeJpaRepository);
    }

    @Transactional
    public PostResponse.UpdateDTO updatePost(Long postId, PostRequest.UpdateDTO request, LoginUser loginUser) {

        Post post = checkOwnership(postId, loginUser);

        // Base64 이미지 데이터 유효성 검사 (선택사항)

        validateImageData(request.getImageData());


        // 이미지 데이터 처리 로직
        String imageDataToUpdate = processImageDataForUpdate(request.getImageData(), post.getImageUrl());

        post.updatePost(request.getTitle(), request.getContent(), imageDataToUpdate);
        return new PostResponse.UpdateDTO(post);
    }

    @Transactional
    public void deletePost(Long postId, LoginUser loginUser) {

        Post post = checkOwnership(postId, loginUser);

        if (post.getImageUrl() != null) {
            boolean deleted = fileUploadUtil.deleteFile(post.getImageUrl());
            if (!deleted) {
                log.warn("게시글 이미지 삭제 실패: {}", post.getImageUrl());
            }
        }

        postRepository.delete(post);
    }

    @Transactional(readOnly = true)
    public List<PostResponse.ListDTO> getPopularPosts(int limit, LoginUser loginUser) {

        Long currentUserId = loginUser != null ? loginUser.getId() : null;
        Pageable pageable = PageRequest.of(0, limit);
        List<Post> popularPosts = postRepository.findTopActiveByViewCount(pageable);

        return popularPosts.stream()
                .map(post -> new PostResponse.ListDTO(post, currentUserId, likeJpaRepository))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PostResponse.ListDTO> getUserPosts(Long userId, LoginUser loginUser) {

        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new Exception404("해당 사용자를 찾을 수 없습니다."));

        Long currentUserId = loginUser != null ? loginUser.getId() : null;

        List<Post> userPosts = postRepository.findActiveByUserIdWithUser(userId);

        return userPosts.stream()
                .map(post -> new PostResponse.ListDTO(post, currentUserId, likeJpaRepository))
                .collect(Collectors.toList());
    }

    // Base64 이미지 데이터 유효성 검사
    private void validateImageData(String imageData) {
        if (imageData == null || imageData.trim().isEmpty()) {
            return; // 이미지가 없는 경우는 허용
        }

        // Base64 데이터 형식 확인
        if (!imageData.startsWith("data:image/")) {
            throw new IllegalArgumentException("올바른 이미지 데이터 형식이 아닙니다.");
        }

        // 이미지 크기 제한 (예: 5MB - Base64는 원본보다 약 33% 큼)
        // Base64 문자열 길이로 대략적인 크기 계산: (길이 * 3/4) / 1024 / 1024 MB
        double approximateSizeMB = (imageData.length() * 0.75) / (1024 * 1024);
        if (approximateSizeMB > 5) {
            throw new IllegalArgumentException("이미지 크기는 5MB 이하여야 합니다.");
        }

        // 허용되는 이미지 타입 확인
        String[] allowedTypes = {"data:image/jpeg", "data:image/jpg", "data:image/png", "data:image/gif", "data:image/webp"};
        boolean isValidType = false;
        for (String type : allowedTypes) {
            if (imageData.startsWith(type)) {
                isValidType = true;
                break;
            }
        }
        if (!isValidType) {
            throw new IllegalArgumentException("지원되지 않는 이미지 형식입니다. (jpeg, jpg, png, gif, webp만 허용)");
        }
    }

    // 이미지 데이터 업데이트 처리
    private String processImageDataForUpdate(String newImageData, String currentImageData) {
        // null인 경우: 기존 이미지 유지
        if (newImageData == null) {
            return currentImageData;
        }

        // 빈 문자열인 경우: 이미지 삭제
        if (newImageData.trim().isEmpty()) {
            return null;
        }

        // 새로운 이미지 데이터가 있는 경우: 교체
        return fileUploadUtil.saveBase64ImageWithType(newImageData, "Image", Define.COMMUNITY);
    }

    private Sort createSort(String sortBy) {
        if ("VIEWS".equalsIgnoreCase(sortBy)) {
            return Sort.by(Sort.Direction.DESC, "viewCount")
                    .and(Sort.by(Sort.Direction.DESC, "createdAt"));
        }
        // 기본값: 최신순
        return Sort.by(Sort.Direction.DESC, "createdAt");
    }

    private Page<Post> getPostsByCondition(PostRequest.ListDTO request, Pageable pageable) {
        String keyword = request.getKeyword();
        String userType = request.getUserType();

        if (keyword != null && !keyword.trim().isEmpty() &&
                userType != null && !userType.trim().isEmpty()) {
            return postRepository.findActiveByKeywordAndUserTypeWithUser(keyword, userType, pageable);
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            return postRepository.findActiveByKeywordWithUser(keyword, pageable);
        }

        if (userType != null && !userType.trim().isEmpty()) {
            return postRepository.findActiveByUserTypeWithUser(userType, pageable);
        }

        return postRepository.findAllActiveWithUser(pageable);
    }

    private Post checkOwnership(Long postId, LoginUser loginUser) {
        if (loginUser == null) {
            throw new Exception403("로그인이 필요합니다.");
        }

        Post post = postRepository.findActiveByIdWithUser(postId)
                .orElseThrow(() -> new Exception404("게시글을 찾을 수 없습니다."));

        if (!post.isOwner(loginUser.getId())) {
            throw new Exception403("게시글 작성자만 수정/삭제할 수 있습니다.");
        }

        return post;
    }
}