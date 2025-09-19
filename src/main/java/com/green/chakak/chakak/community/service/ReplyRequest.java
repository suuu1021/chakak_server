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

    // 댓글 작성 요청 DTO
    @Data
    @NoArgsConstructor
    public static class CreateDTO {

        @NoSensitiveInfo
        @NotBlank(message = "댓글 내용은 필수 입력사항입니다.")
        @Size(max = 500, message = "댓글은 300자를 초과할 수 없습니다.")
        private String content;

        // DTO를 Entity로 변환하는 메서드
        public Reply toEntity(Post post, User user) {
            return Reply.builder()
                    .post(post)
                    .user(user)
                    .content(this.content)
                    .status(Reply.ReplyStatus.ACTIVE)
                    .build();
        }
    }

    // 댓글 수정 요청 DTO
    @Data
    @NoArgsConstructor
    public static class UpdateDTO {

        @NotBlank(message = "댓글 내용은 필수 입력사항입니다.")
        @Size(max = 300, message = "댓글은 300자를 초과할 수 없습니다.")
        private String content;
    }

    // 댓글 목록 조회 요청 DTO
    @Data
    @NoArgsConstructor
    public static class ListDTO {

        // 페이지 번호 (기본값: 0)
        private Integer page = 0;

        // 페이지 크기 (기본값: 20)
        private Integer size = 20;

        // 정렬 기준 (LATEST: 최신순, OLDEST: 오래된순)
        private String sortBy = "OLDEST"; // 댓글은 보통 오래된 순으로 정렬
    }
}