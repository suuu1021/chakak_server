package com.green.chakak.chakak.community.service;

import com.green.chakak.chakak._global.errors.exception.Exception403;
import com.green.chakak.chakak._global.errors.exception.Exception404;
import com.green.chakak.chakak.account.domain.LoginUser;
import com.green.chakak.chakak.account.domain.User;
import com.green.chakak.chakak.account.service.repository.UserJpaRepository;
import com.green.chakak.chakak.community.domain.Post;
import com.green.chakak.chakak.community.repository.PostJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostJpaRepository postRepository;
    private final UserJpaRepository userJpaRepository;

    /**
     * 커뮤니티 글 작성
     * - 로그인된 사용자만 작성 가능
     */
    @Transactional
    public PostResponse.CreateDTO createPost(PostRequest.CreateDTO request, LoginUser loginUser) {
        // 1. 로그인 확인
        if (loginUser == null) {
            throw new Exception403("로그인 후 글을 작성할 수 있습니다.");
        }

        // 2. 사용자 정보 조회
        User user = userJpaRepository.findById(loginUser.getId())
                .orElseThrow(() -> new Exception404("사용자 정보를 찾을 수 없습니다."));

        // 3. 게시글 생성 및 저장
        Post post = request.toEntity(user);
        Post savedPost = postRepository.save(post);

        return new PostResponse.CreateDTO(savedPost);
    }

    /**
     * 커뮤니티 글 목록 조회 (비회원도 가능)
     * - 페이징, 검색, 정렬 기능 포함
     */
    @Transactional(readOnly = true)
    public PostResponse.PageDTO getPostList(PostRequest.ListDTO request) {
        // 1. 페이징 및 정렬 설정
        Sort sort = createSort(request.getSortBy());
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        // 2. 조건에 따른 데이터 조회
        Page<Post> postPage = getPostsByCondition(request, pageable);

        // 3. DTO 변환
        List<PostResponse.ListDTO> content = postPage.getContent().stream()
                .map(PostResponse.ListDTO::new)
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

    /**
     * 커뮤니티 글 상세 조회
     * - 로그인된 사용자만 상세 내용 조회 가능
     * - 조회 시 조회수 증가
     */
    @Transactional
    public PostResponse.DetailDTO getPostDetail(Long postId, LoginUser loginUser) {
        // 1. 로그인 확인
        if (loginUser == null) {
            throw new Exception403("로그인 후 게시글을 확인할 수 있습니다.");
        }

        // 2. 게시글 조회 (조인 패치 적용)
        Post post = postRepository.findActiveByIdWithUser(postId)
                .orElseThrow(() -> new Exception404("게시글을 찾을 수 없습니다."));

        // 3. 조회수 증가 (작성자 본인은 제외)
        if (!post.isOwner(loginUser.getId())) {
            post.increaseViewCount();
        }

        return new PostResponse.DetailDTO(post, loginUser.getId());
    }

    /**
     * 커뮤니티 글 수정
     * - 작성자만 수정 가능
     */
    @Transactional
    public PostResponse.UpdateDTO updatePost(Long postId, PostRequest.UpdateDTO request, LoginUser loginUser) {
        // 1. 로그인 및 권한 확인
        Post post = checkOwnership(postId, loginUser);

        // 2. 게시글 수정
        post.updatePost(request.getTitle(), request.getContent());

        return new PostResponse.UpdateDTO(post);
    }

    /**
     * 커뮤니티 글 삭제 (소프트 삭제)
     * - 작성자만 삭제 가능
     */
    @Transactional
    public void deletePost(Long postId, LoginUser loginUser) {
        // 1. 로그인 및 권한 확인
        Post post = checkOwnership(postId, loginUser);

        // 2. 소프트 삭제 처리
        post.deletePost();
    }

    /**
     * 인기 게시글 조회 (조회수 기준)
     */
    @Transactional(readOnly = true)
    public List<PostResponse.ListDTO> getPopularPosts(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Post> popularPosts = postRepository.findTopActiveByViewCount(pageable);

        return popularPosts.stream()
                .map(PostResponse.ListDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * 사용자별 게시글 조회
     */
    @Transactional(readOnly = true)
    public List<PostResponse.ListDTO> getUserPosts(Long userId) {


        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new Exception404("해당 사용자를 찾을 수 없습니다."));

        List<Post> userPosts = postRepository.findActiveByUserIdWithUser(userId);

        return userPosts.stream()
                .map(PostResponse.ListDTO::new)
                .collect(Collectors.toList());
    }

    // === 내부 메서드들 ===

    /**
     * 정렬 조건 생성
     */
    private Sort createSort(String sortBy) {
        if ("VIEWS".equalsIgnoreCase(sortBy)) {
            return Sort.by(Sort.Direction.DESC, "viewCount")
                    .and(Sort.by(Sort.Direction.DESC, "createdAt"));
        }
        // 기본값: 최신순
        return Sort.by(Sort.Direction.DESC, "createdAt");
    }

    /**
     * 조건에 따른 게시글 조회
     */
    private Page<Post> getPostsByCondition(PostRequest.ListDTO request, Pageable pageable) {
        String keyword = request.getKeyword();
        String userType = request.getUserType();

        // 키워드와 사용자 타입 모두 있는 경우
        if (keyword != null && !keyword.trim().isEmpty() &&
                userType != null && !userType.trim().isEmpty()) {
            return postRepository.findActiveByKeywordAndUserTypeWithUser(keyword, userType, pageable);
        }

        // 키워드만 있는 경우
        if (keyword != null && !keyword.trim().isEmpty()) {
            return postRepository.findActiveByKeywordWithUser(keyword, pageable);
        }

        // 사용자 타입만 있는 경우
        if (userType != null && !userType.trim().isEmpty()) {
            return postRepository.findActiveByUserTypeWithUser(userType, pageable);
        }

        // 조건이 없는 경우
        return postRepository.findAllActiveWithUser(pageable);
    }

    /**
     * 게시글 소유권 확인
     */
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
