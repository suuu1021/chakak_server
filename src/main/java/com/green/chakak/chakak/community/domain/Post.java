package com.green.chakak.chakak.community.domain;

import com.green.chakak.chakak.account.domain.User;
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

    // 작성자 (User와 다대일 관계 - 한 명의 유저가 여러 글을 작성할 수 있음)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    // 조회수
    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0;

    // 좋아요 수 (성능을 위한 비정규화)
    @Column(name = "like_count", nullable = false)
    private Integer likeCount = 0;

    // 댓글 수 (성능을 위한 비정규화)
    @Column(name = "reply_count", nullable = false)
    private Integer replyCount = 0;


    // 게시글 상태 (ACTIVE: 활성, INACTIVE: 비활성, DELETED: 삭제)
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
        ACTIVE,     // 활성
        INACTIVE,   // 비활성
        DELETED     // 삭제
    }

    // 댓글 목록 (일대다 관계)
    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Reply> replies = new ArrayList<>();

    // 좋아요 목록 (일대다 관계)
    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();

    @Builder
    public Post(User user, String title, String content, PostStatus status) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.status = status != null ? status : PostStatus.ACTIVE;
        this.viewCount = 0;
        this.likeCount = 0;
        this.replies = new ArrayList<>();
        this.likes = new ArrayList<>();
    }

    // 비즈니스 메서드들

    // 조회수 증가
    public void increaseViewCount() {
        this.viewCount++;
    }

    // 게시글 수정
    public void updatePost(String title, String content) {
        if (title != null && !title.trim().isEmpty()) {
            this.title = title;
        }
        if (content != null && !content.trim().isEmpty()) {
            this.content = content;
        }
    }

    // 좋아요 수 증가
    public void increaseLikeCount() {
        this.likeCount++;
    }

    // 좋아요 수 감소
    public void decreaseLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    // 댓글 수 증가
    public void increaseReplyCount() {
        this.replyCount++;
    }

    // 댓글 수 감소
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
}