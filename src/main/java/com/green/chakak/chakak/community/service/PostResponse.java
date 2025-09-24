package com.green.chakak.chakak.community.service;

import com.green.chakak.chakak.community.domain.Post;
import com.green.chakak.chakak.community.domain.Reply;
import com.green.chakak.chakak.community.repository.LikeJpaRepository;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PostResponse {

    // 커뮤니티 글 작성 응답 DTO
    @Data
    public static class CreateDTO {
        private Long postId;
        private String title;
        private String content;
        private String imageData; // Base64 이미지 데이터
        private String authorNickname;
        private String authorType;
        private Timestamp createdAt;

        public CreateDTO(Post post) {
            this.postId = post.getPostId();
            this.title = post.getTitle();
            this.content = post.getContent();
            this.imageData = post.getImageUrl();
            this.authorNickname = getAuthorNickname(post);
            this.authorType = post.getUser().getUserType().getTypeCode();
            this.createdAt = post.getCreatedAt();
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

    @Data
    public static class DetailDTO {
        private Long postId;
        private String title;
        private String content;
        private String imageData; // Base64 이미지 데이터
        private String authorNickname;
        private String authorType;
        private Integer viewCount;
        private Integer likeCount;
        private String status;
        private List<ReplyDTO> replies;
        private Timestamp createdAt;
        private Timestamp updatedAt;
        private boolean isLiked;
        private boolean isOwner;
        private boolean hasImage; // 이미지 유무

        @Builder
        public DetailDTO(Post post, Long currentUserId, LikeJpaRepository likeJpaRepository) {
            this.postId = post.getPostId();
            this.title = post.getTitle();
            this.content = post.getContent();
            this.imageData = post.getImageUrl();
            this.hasImage = post.hasImage();
            this.authorNickname = getAuthorNickname(post);
            this.authorType = post.getUser().getUserType().getTypeCode();
            this.viewCount = post.getViewCount();
            this.likeCount = post.getLikeCount();
            this.status = post.getStatus().name();
            this.createdAt = post.getCreatedAt();
            this.updatedAt = post.getUpdatedAt();
            this.isOwner = currentUserId != null && post.isOwner(currentUserId);

            this.isLiked = currentUserId != null &&
                    likeJpaRepository.existsByPostIdAndUserId(post.getPostId(), currentUserId);

            this.replies = new ArrayList<>();
            for (Reply reply : post.getReplies()) {
                this.replies.add(new ReplyDTO(reply, currentUserId));
            }
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

    @Data
    public static class ReplyDTO {
        private Long replyId;
        private String content;
        private String authorNickname;
        private String authorType;
        private Timestamp createdAt;
        private boolean isOwner;

        @Builder
        public ReplyDTO(Reply reply, Long currentUserId) {
            this.replyId = reply.getReplyId();
            this.content = reply.getContent();
            this.authorNickname = getAuthorNickname(reply);
            this.authorType = reply.getUser().getUserType().getTypeCode();
            this.createdAt = reply.getCreatedAt();
            this.isOwner = currentUserId != null && reply.isOwner(currentUserId);
        }

        private String getAuthorNickname(Reply reply) {
            if (reply.getUser().getUserProfile() != null &&
                    reply.getUser().getUserProfile().getNickName() != null) {
                return reply.getUser().getUserProfile().getNickName();
            }
            String email = reply.getUser().getEmail();
            return email.substring(0, email.indexOf("@"));
        }
    }

    @Data
    public static class ListDTO {
        private Long postId;
        private String title;
        private String content;
        private String authorNickname;
        private Long authorId;
        private String authorType;
        private Integer viewCount;
        private Integer likeCount;
        private Integer replyCount;
        private boolean hasImage; // 이미지 유무 (썸네일 표시용)
        private String thumbnailData; // 썸네일용 Base64 데이터 (필요시)
        private Timestamp createdAt;
        private boolean isLiked;

        public ListDTO(Post post, Long currentUserId, LikeJpaRepository likeRepository) {
            this.postId = post.getPostId();
            this.title = post.getTitle();
            this.content = post.getContent();
            this.authorNickname = getAuthorNickname(post);
            this.authorId = post.getUser().getUserId();
            this.authorType = post.getUser().getUserType().getTypeCode();
            this.viewCount = post.getViewCount();
            this.likeCount = post.getLikeCount();
            this.replyCount = post.getReplyCount();
            this.hasImage = post.hasImage();
            // 목록에서는 전체 이미지 데이터는 너무 크므로 hasImage만 표시
            // 필요하면 thumbnailData에 작은 크기의 썸네일 데이터를 넣을 수 있음
            this.createdAt = post.getCreatedAt();

            this.isLiked = currentUserId != null &&
                    likeRepository.existsByPostIdAndUserId(post.getPostId(), currentUserId);


            if (this.hasImage && post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
                this.thumbnailData = post.getImageUrl(); // 그대로 할당
            } else {
                this.thumbnailData = null;
            }
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

    @Data
    public static class UpdateDTO {
        private Long postId;
        private String title;
        private String content;
        private String imageData; // Base64 이미지 데이터
        private boolean hasImage; // 이미지 유무
        private Timestamp updatedAt;

        public UpdateDTO(Post post) {
            this.postId = post.getPostId();
            this.title = post.getTitle();
            this.content = post.getContent();
            this.imageData = post.getImageUrl();
            this.hasImage = post.hasImage();
            this.updatedAt = post.getUpdatedAt();
        }
    }

    @Data
    public static class PageDTO {
        private List<ListDTO> content;
        private int currentPage;
        private int totalPages;
        private long totalElements;
        private int size;
        private boolean hasNext;
        private boolean hasPrevious;

        @Builder
        public PageDTO(List<ListDTO> content, int currentPage, int totalPages,
                       long totalElements, int size, boolean hasNext, boolean hasPrevious) {
            this.content = content;
            this.currentPage = currentPage;
            this.totalPages = totalPages;
            this.totalElements = totalElements;
            this.size = size;
            this.hasNext = hasNext;
            this.hasPrevious = hasPrevious;
        }
    }
}