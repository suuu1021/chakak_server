package com.green.chakak.chakak.community.service;

import com.green.chakak.chakak.account.domain.User;
import com.green.chakak.chakak.community.domain.Post;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

public class PostRequest {

    // 커뮤니티 글 작성 요청 DTO
    @Data
    @NoArgsConstructor
    public static class CreateDTO {

        @NotBlank(message = "제목은 필수 입력사항입니다.")
        @Size(max = 50, message = "제목은 50자를 초과할 수 없습니다.")
        private String title;

        @NotBlank(message = "내용은 필수 입력사항입니다.")
        @Size(max = 1500, message = "내용은 1500자를 초과할 수 없습니다.")
        private String content;

        // DTO를 Entity로 변환하는 메서드
        public Post toEntity(User user) {
            return Post.builder()
                    .user(user)
                    .title(this.title)
                    .content(this.content)
                    .status(Post.PostStatus.ACTIVE)
                    .build();
        }
    }

    // 커뮤니티 글 수정 요청 DTO
    @Data
    @NoArgsConstructor
    public static class UpdateDTO {

        @Size(max = 50, message = "제목은 50자를 초과할 수 없습니다.")
        private String title;

        @Size(max = 1500, message = "내용은 1500자를 초과할 수 없습니다.")
        private String content;
    }

    // 커뮤니티 글 목록 조회 요청 DTO (페이징, 검색 등)
    @Data
    @NoArgsConstructor
    public static class ListDTO {

        // 페이지 번호 (기본값: 0)
        private Integer page = 0;

        // 페이지 크기 (기본값: 10)
        private Integer size = 10;

        // 검색 키워드 (제목 + 내용 검색)
        private String keyword;

        // 작성자 타입으로 필터링 (USER, PHOTOGRAPHER)
        private String userType;

        // 정렬 기준 (LATEST: 최신순, VIEWS: 조회수순)
        private String sortBy = "LATEST";
    }
}