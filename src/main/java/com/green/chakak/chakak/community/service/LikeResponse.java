package com.green.chakak.chakak.community.service;

import com.green.chakak.chakak.community.domain.Like;
import com.green.chakak.chakak.community.domain.Post;
import lombok.Data;

import java.sql.Timestamp;


public class LikeResponse {


    @Data
    public static class ToggleDTO {
        private Long postId;
        private boolean isLiked;
        private Integer likeCount;
        private String message;

        public ToggleDTO(Long postId, boolean isLiked, Integer likeCount) {
            this.postId = postId;
            this.isLiked = isLiked;
            this.likeCount = likeCount;
            this.message = isLiked ? "좋아요를 눌렀습니다." : "좋아요를 취소했습니다.";
        }
    }


    @Data
    public static class StatusDTO {
        private Long postId;
        private boolean isLiked;
        private Integer likeCount;

        public StatusDTO(Long postId, boolean isLiked, Integer likeCount) {
            this.postId = postId;
            this.isLiked = isLiked;
            this.likeCount = likeCount;
        }
    }


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