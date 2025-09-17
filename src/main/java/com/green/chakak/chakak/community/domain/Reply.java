package com.green.chakak.chakak.community.domain;

import com.green.chakak.chakak.account.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Data
@NotNull
@Table(name = "reply")
@Entity
public class Reply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500) //기본값 255
    private String comment;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Post post;

    @CreationTimestamp // 서버 PC 시간 기준
    private Timestamp createdAt;

    @Builder
    public Reply(Long id, String comment, User user, Post post, Timestamp createdAt) {
        this.id = id;
        this.comment = comment;
        this.user = user;
        this.post = post;
        this.createdAt = createdAt;
    }

    @Transient
    private boolean isReplyOwner;

    public boolean isOwner(Long sessionId) {
        return this.user.getUserId().equals(sessionId);
    }


}
