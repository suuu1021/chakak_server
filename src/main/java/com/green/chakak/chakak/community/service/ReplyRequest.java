package com.green.chakak.chakak.community.service;

import com.green.chakak.chakak._global.utils.NoSensitiveInfo;
import com.green.chakak.chakak.account.domain.User;
import com.green.chakak.chakak.community.domain.Post;
import com.green.chakak.chakak.community.domain.Reply;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ReplyRequest {


    @Data
    @NoArgsConstructor
    public static class CreateDTO {

        @NoSensitiveInfo
        @NotBlank(message = "댓글 내용은 필수 입력사항입니다.")
        @Size(max = 500, message = "댓글은 300자를 초과할 수 없습니다.")
        private String content;


        public Reply toEntity(Post post, User user) {
            return Reply.builder()
                    .post(post)
                    .user(user)
                    .content(this.content)
                    .status(Reply.ReplyStatus.ACTIVE)
                    .build();
        }
    }


    @Data
    @NoArgsConstructor
    public static class UpdateDTO {

        @NotBlank(message = "댓글 내용은 필수 입력사항입니다.")
        @Size(max = 300, message = "댓글은 300자를 초과할 수 없습니다.")
        private String content;
    }


    @Data
    @NoArgsConstructor
    public static class ListDTO {


        private Integer page = 0;


        private Integer size = 20;

        private String sortBy = "OLDEST";
    }
}