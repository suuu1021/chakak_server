package com.green.chakak.chakak.community.service;

import com.green.chakak.chakak.community.domain.Like;
import com.green.chakak.chakak.community.domain.Post;
import lombok.Data;

import java.sql.Timestamp;


public class LikeResponse {

    // 좋아요 토글 응답 DTO
    @Data
    public static class ToggleDTO {
        private Long postId;
        private boolean isLiked; // true: 좋아요 추가됨, false: 좋아요 취소됨
        private Integer likeCount; // 해당 게시글의 총 좋아요 수
        private String message; // 사용자에게 보여줄 메시지

        public ToggleDTO(Long postId, boolean isLiked, Integer likeCount) {
            this.postId = postId;
            this.isLiked = isLiked;
            this.likeCount = likeCount;
            this.message = isLiked ? "좋아요를 눌렀습니다." : "좋아요를 취소했습니다.";
        }
    }

    // 좋아요 상태 확인 응답 DTO
    @Data
    public static class StatusDTO {
        private Long postId;
        private boolean isLiked; // 현재 사용자가 좋아요를 눌렀는지
        private Integer likeCount; // 해당 게시글의 총 좋아요 수

        public StatusDTO(Long postId, boolean isLiked, Integer likeCount) {
            this.postId = postId;
            this.isLiked = isLiked;
            this.likeCount = likeCount;
        }
    }

    // 사용자가 좋아요 누른 게시글 목록 DTO
    @Data
    public static class UserLikeDTO {
        private Long likeId;
        private Long postId;
        private String postTitle;
        private String postAuthor;
        private Timestamp likedAt;

        public UserLikeDTO(Like like) {
            this.likeId = like.getLikeId();
            this.postId = like.getPost().getPostId();
            this.postTitle = like.getPost().getTitle();
            this.postAuthor = getAuthorNickname(like.getPost());
            this.likedAt = like.getCreatedAt();
        }

        private String getAuthorNickname(Post post) {
            if (post.getUser().getUserProfile() != null &&
                    post.getUser().getUserProfile().getNickName() != null) {
                return post.getUser().getUserProfile().getNickName();
            }
            String email = post.getUser().getEmail();
            return email.substring(0, email.indexOf("@"));
        }
    }
}