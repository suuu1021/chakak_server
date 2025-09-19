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

@Entity
@Table(name = "reply")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reply_id")
    private Long replyId;

    // 어떤 게시글의 댓글인지 (Post와 다대일 관계)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    // 댓글 작성자 (User와 다대일 관계)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    // 댓글 상태 (ACTIVE: 활성, DELETED: 삭제)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReplyStatus status = ReplyStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt;

    public enum ReplyStatus {
        ACTIVE,   // 활성
        DELETED   // 삭제
    }

    @Builder
    public Reply(Post post, User user, String content, ReplyStatus status) {
        this.post = post;
        this.user = user;
        this.content = content;
        this.status = status != null ? status : ReplyStatus.ACTIVE;
    }

    // 비즈니스 메서드들

    // 댓글 수정
    public void updateContent(String content) {
        if (content != null && !content.trim().isEmpty()) {
            this.content = content;
        }
    }

    // 댓글 삭제 (소프트 삭제)
    public void deleteReply() {
        this.status = ReplyStatus.DELETED;
    }

    // 댓글 활성화
    public void activateReply() {
        this.status = ReplyStatus.ACTIVE;
    }

    // 작성자인지 확인
    public boolean isOwner(Long userId) {
        return this.user.getUserId().equals(userId);
    }

    // 활성 상태인지 확인
    public boolean isActive() {
        return this.status == ReplyStatus.ACTIVE;
    }
}