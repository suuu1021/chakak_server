package com.green.chakak.chakak.community.service;

import com.green.chakak.chakak.community.domain.Reply;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

public class ReplyResponse {


    @Data
    public static class CreateDTO {
        private Long replyId;
        private Long postId;
        private String content;
        private String authorNickname;
        private String authorType;
        private Timestamp createdAt;

        public CreateDTO(Reply reply) {
            this.replyId = reply.getReplyId();
            this.postId = reply.getPost().getPostId();
            this.content = reply.getContent();
            this.authorNickname = getAuthorNickname(reply);
            this.authorType = reply.getUser().getUserType().getTypeCode();
            this.createdAt = reply.getCreatedAt();
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
    public static class UpdateDTO {
        private Long replyId;
        private String content;
        private Timestamp updatedAt;

        public UpdateDTO(Reply reply) {
            this.replyId = reply.getReplyId();
            this.content = reply.getContent();
            this.updatedAt = reply.getUpdatedAt();
        }
    }


}