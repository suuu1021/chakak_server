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


    @Transactional
    public LikeResponse.ToggleDTO toggleLike(Long postId, LoginUser loginUser) {

        if (loginUser == null) {
            throw new Exception403("로그인 후 좋아요를 누를 수 있습니다.");
        }


        Post post = postRepository.findActiveByIdJoinUser(postId)
                .orElseThrow(() -> new Exception404("게시글을 찾을 수 없습니다."));


        User user = userJpaRepository.findById(loginUser.getId())
                .orElseThrow(() -> new Exception404("사용자 정보를 찾을 수 없습니다."));


        Optional<Like> existingLike = likeRepository.findByPostAndUser(post, user);

        boolean isLiked;
        if (existingLike.isPresent()) {

            likeRepository.delete(existingLike.get());
            post.decreaseLikeCount();
            isLiked = false;
        } else {

            Like newLike = Like.builder()
                    .post(post)
                    .user(user)
                    .build();
            likeRepository.save(newLike);
            post.increaseLikeCount();
            isLiked = true;
        }

        return new LikeResponse.ToggleDTO(postId, isLiked, post.getLikeCount());
    }


    @Transactional(readOnly = true)
    public LikeResponse.StatusDTO getLikeStatus(Long postId, LoginUser loginUser) {

        Post post = postRepository.findActiveByIdJoinUser(postId)
                .orElseThrow(() -> new Exception404("게시글을 찾을 수 없습니다."));

        boolean isLiked = false;
        if (loginUser != null) {

            isLiked = likeRepository.existsByPostIdAndUserId(postId, loginUser.getId());
        }

        return new LikeResponse.StatusDTO(postId, isLiked, post.getLikeCount());
    }

    @Transactional(readOnly = true)
    public List<LikeResponse.UserLikeDTO> getUserLikedPosts(Long userId) {

        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new Exception404("해당 사용자를 찾을 수 없습니다."));


        List<Like> userLikes = likeRepository.findByUserIdJoinPost(userId);

        return userLikes.stream()
                .map(LikeResponse.UserLikeDTO::new)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<LikeResponse.UserLikeDTO> getMyLikedPosts(LoginUser loginUser) {
        if (loginUser == null) {
            throw new Exception403("로그인이 필요합니다.");
        }

        return getUserLikedPosts(loginUser.getId());
    }


    @Transactional(readOnly = true)
    public List<String> getPostLikers(Long postId) {

        postRepository.findActiveByIdJoinUser(postId)
                .orElseThrow(() -> new Exception404("게시글을 찾을 수 없습니다."));


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