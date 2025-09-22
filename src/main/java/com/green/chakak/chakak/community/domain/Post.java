package com.green.chakak.chakak.community.domain;

import com.green.chakak.chakak.account.domain.User;
import com.green.chakak.chakak.account.domain.UserType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "post")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Lob
    @Column(name = "image_url", columnDefinition = "LONGTEXT")
    private String imageUrl;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0;

    @Column(name = "like_count", nullable = false)
    private Integer likeCount = 0;

    @Column(name = "reply_count", nullable = false)
    private Integer replyCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PostStatus status = PostStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt;

    public enum PostStatus {
        ACTIVE,
        INACTIVE,
        DELETED
    }

    public enum PostCategory {
        COMMUNITY,
        NOTICE
    }

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Reply> replies = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();

    @Builder
    public Post(User user, String title, String content, String imageUrl, PostStatus status) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.status = status != null ? status : PostStatus.ACTIVE;
        this.viewCount = 0;
        this.likeCount = 0;
        this.replyCount = 0;
        this.replies = new ArrayList<>();
        this.likes = new ArrayList<>();
    }

    public PostCategory getCategory() {
        return this.user.getUserType().getTypeCode().equals("admin")
                ? PostCategory.NOTICE
                : PostCategory.COMMUNITY;
    }

    public void increaseViewCount() {
        this.viewCount++;
    }

    public void updatePost(String title, String content, String imageData) {
        if (title != null && !title.trim().isEmpty()) {
            this.title = title;
        }
        if (content != null && !content.trim().isEmpty()) {
            this.content = content;
        }
        // 이미지 데이터도 업데이트 (null이면 기존 이미지 유지)
        if (imageUrl != null) {
            this.imageUrl = imageData;
        }
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    public void increaseReplyCount() {
        this.replyCount++;
    }

    public void decreaseReplyCount() {
        if (this.replyCount > 0) {
            this.replyCount--;
        }
    }

    // 게시글 삭제 (소프트 삭제)
    public void deletePost() {
        this.status = PostStatus.DELETED;
    }

    // 게시글 활성화
    public void activatePost() {
        this.status = PostStatus.ACTIVE;
    }

    // 작성자인지 확인
    public boolean isOwner(Long userId) {
        return this.user.getUserId().equals(userId);
    }

    // 활성 상태인지 확인
    public boolean isActive() {
        return this.status == PostStatus.ACTIVE;
    }

    // 관리자 표시 여부
    public boolean isAdminPost() {
        return "admin".equals(this.user.getUserType().getTypeCode());
    }

    // 이미지가 있는지 확인
    public boolean hasImage() {
        return this.imageUrl != null && !this.imageUrl.trim().isEmpty();
    }
}