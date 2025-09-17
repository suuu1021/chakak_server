package com.green.chakak.chakak.community.service;

import com.green.chakak.chakak.community.domain.Post;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

public class PostResponse {

    // 커뮤니티 글 작성 응답 DTO
    @Data
    public static class CreateDTO {
        private Long postId;
        private String title;
        private String content;
        private String authorNickname;
        private String authorType;
        private Timestamp createdAt;

        public CreateDTO(Post post) {
            this.postId = post.getPostId();
            this.title = post.getTitle();
            this.content = post.getContent();
            // UserProfile이 있으면 닉네임, 없으면 이메일의 @ 앞부분 사용
            this.authorNickname = getAuthorNickname(post);
            this.authorType = post.getUser().getUserType().getTypeCode();
            this.createdAt = post.getCreatedAt();
        }

        private String getAuthorNickname(Post post) {
            if (post.getUser().getUserProfile() != null &&
                    post.getUser().getUserProfile().getNickName() != null) {
                return post.getUser().getUserProfile().getNickName();
            }
            // 프로필이 없으면 이메일의 @ 앞부분을 닉네임으로 사용
            String email = post.getUser().getEmail();
            return email.substring(0, email.indexOf("@"));
        }
    }

    // 커뮤니티 글 상세 조회 응답 DTO
    @Data
    public static class DetailDTO {
        private Long postId;
        private String title;
        private String content;
        private String authorNickname;
        private String authorType;
        private Integer viewCount;
        private String status;
        private Timestamp createdAt;
        private Timestamp updatedAt;
        private boolean isOwner; // 현재 로그인 사용자가 작성자인지

        public DetailDTO(Post post, Long currentUserId) {
            this.postId = post.getPostId();
            this.title = post.getTitle();
            this.content = post.getContent();
            this.authorNickname = getAuthorNickname(post);
            this.authorType = post.getUser().getUserType().getTypeCode();
            this.viewCount = post.getViewCount();
            this.status = post.getStatus().name();
            this.createdAt = post.getCreatedAt();
            this.updatedAt = post.getUpdatedAt();
            this.isOwner = currentUserId != null && post.isOwner(currentUserId);
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

    // 커뮤니티 글 목록 조회 응답 DTO
    @Data
    public static class ListDTO {
        private Long postId;
        private String title;
        private String authorNickname;
        private String authorType;
        private Integer viewCount;
        private Timestamp createdAt;

        public ListDTO(Post post) {
            this.postId = post.getPostId();
            this.title = post.getTitle();
            this.authorNickname = getAuthorNickname(post);
            this.authorType = post.getUser().getUserType().getTypeCode();
            this.viewCount = post.getViewCount();
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

    // 커뮤니티 글 수정 응답 DTO
    @Data
    public static class UpdateDTO {
        private Long postId;
        private String title;
        private String content;
        private Timestamp updatedAt;

        public UpdateDTO(Post post) {
            this.postId = post.getPostId();
            this.title = post.getTitle();
            this.content = post.getContent();
            this.updatedAt = post.getUpdatedAt();
        }
    }

    // 페이지네이션을 포함한 목록 응답 DTO
    @Data
    public static class PageDTO {
        private List<ListDTO> content;
        private int currentPage;
        private int totalPages;
        private long totalElements;
        private int size;
        private boolean hasNext;
        private boolean hasPrevious;

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