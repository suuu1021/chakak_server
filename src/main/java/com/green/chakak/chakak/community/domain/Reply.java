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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

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
        ACTIVE,
        DELETED
    }

    @Builder
    public Reply(Post post, User user, String content, ReplyStatus status) {
        this.post = post;
        this.user = user;
        this.content = content;
        this.status = status != null ? status : ReplyStatus.ACTIVE;
    }


    public void updateContent(String content) {
        if (content != null && !content.trim().isEmpty()) {
            this.content = content;
        }
    }


    public void deleteReply() {
        this.status = ReplyStatus.DELETED;
    }


    public void activateReply() {
        this.status = ReplyStatus.ACTIVE;
    }


    public boolean isOwner(Long userId) {
        return this.user.getUserId().equals(userId);
    }

    public boolean isActive() {
        return this.status == ReplyStatus.ACTIVE;
    }
}