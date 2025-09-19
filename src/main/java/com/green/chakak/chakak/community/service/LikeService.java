package com.green.chakak.chakak.community.service;

import com.green.chakak.chakak._global.errors.exception.Exception403;
import com.green.chakak.chakak._global.errors.exception.Exception404;
import com.green.chakak.chakak.account.domain.LoginUser;
import com.green.chakak.chakak.account.domain.User;
import com.green.chakak.chakak.account.service.repository.UserJpaRepository;
import com.green.chakak.chakak.community.domain.Like;
import com.green.chakak.chakak.community.domain.Post;
import com.green.chakak.chakak.community.repository.LikeJpaRepository;
import com.green.chakak.chakak.community.repository.PostJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {

    private final LikeJpaRepository likeRepository;
    private final PostJpaRepository postRepository;
    private final UserJpaRepository userJpaRepository;

    /**
     * 좋아요 토글 (중복 방지)
     * - 이미 좋아요를 눌렀으면 취소, 안 눌렀으면 추가
     */
    @Transactional
    public LikeResponse.ToggleDTO toggleLike(Long postId, LoginUser loginUser) {
        // 1. 로그인 확인
        if (loginUser == null) {
            throw new Exception403("로그인 후 좋아요를 누를 수 있습니다.");
        }

        // 2. 게시글 존재 확인
        Post post = postRepository.findActiveByIdJoinUser(postId)
                .orElseThrow(() -> new Exception404("게시글을 찾을 수 없습니다."));

        // 3. 사용자 정보 조회
        User user = userJpaRepository.findById(loginUser.getId())
                .orElseThrow(() -> new Exception404("사용자 정보를 찾을 수 없습니다."));

        // 4. 기존 좋아요 확인
        Optional<Like> existingLike = likeRepository.findByPostAndUser(post, user);

        boolean isLiked;
        if (existingLike.isPresent()) {
            // 이미 좋아요를 눌렀다면 취소 (삭제)
            likeRepository.delete(existingLike.get());
            post.decreaseLikeCount(); // 좋아요 수 감소
            isLiked = false;
        } else {
            // 좋아요를 안 눌렀다면 추가
            Like newLike = Like.builder()
                    .post(post)
                    .user(user)
                    .build();
            likeRepository.save(newLike);
            post.increaseLikeCount(); // 좋아요 수 증가
            isLiked = true;
        }

        return new LikeResponse.ToggleDTO(postId, isLiked, post.getLikeCount());
    }

    /**
     * 좋아요 상태 확인
     * - 현재 사용자가 해당 게시글에 좋아요를 눌렀는지 확인
     */
    @Transactional(readOnly = true)
    public LikeResponse.StatusDTO getLikeStatus(Long postId, LoginUser loginUser) {
        // 1. 게시글 존재 확인
        Post post = postRepository.findActiveByIdJoinUser(postId)
                .orElseThrow(() -> new Exception404("게시글을 찾을 수 없습니다."));

        boolean isLiked = false;
        if (loginUser != null) {
            // 로그인 상태에서만 좋아요 상태 확인
            isLiked = likeRepository.existsByPostIdAndUserId(postId, loginUser.getId());
        }

        return new LikeResponse.StatusDTO(postId, isLiked, post.getLikeCount());
    }

    /**
     * 사용자가 좋아요 누른 게시글 목록 조회
     */
    @Transactional(readOnly = true)
    public List<LikeResponse.UserLikeDTO> getUserLikedPosts(Long userId) {
        // 1. 사용자 존재 확인
        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new Exception404("해당 사용자를 찾을 수 없습니다."));

        // 2. 사용자가 좋아요 누른 게시글 목록 조회
        List<Like> userLikes = likeRepository.findByUserIdJoinPost(userId);

        return userLikes.stream()
                .map(LikeResponse.UserLikeDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * 내가 좋아요 누른 게시글 목록 조회
     */
    @Transactional(readOnly = true)
    public List<LikeResponse.UserLikeDTO> getMyLikedPosts(LoginUser loginUser) {
        if (loginUser == null) {
            throw new Exception403("로그인이 필요합니다.");
        }

        return getUserLikedPosts(loginUser.getId());
    }

    /**
     * 특정 게시글의 좋아요 목록 조회 (누가 좋아요를 눌렀는지)
     */
    @Transactional(readOnly = true)
    public List<String> getPostLikers(Long postId) {
        // 1. 게시글 존재 확인
        postRepository.findActiveByIdJoinUser(postId)
                .orElseThrow(() -> new Exception404("게시글을 찾을 수 없습니다."));

        // 2. 좋아요 누른 사용자 목록 조회
        List<Like> likes = likeRepository.findByPostIdJoinUser(postId);

        return likes.stream()
                .map(like -> {
                    if (like.getUser().getUserProfile() != null &&
                            like.getUser().getUserProfile().getNickName() != null) {
                        return like.getUser().getUserProfile().getNickName();
                    }
                    String email = like.getUser().getEmail();
                    return email.substring(0, email.indexOf("@"));
                })
                .collect(Collectors.toList());
    }
}