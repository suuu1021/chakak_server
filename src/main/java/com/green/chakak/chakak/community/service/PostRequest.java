package com.green.chakak.chakak.community.service;

import com.green.chakak.chakak._global.utils.NoSensitiveInfo;
import com.green.chakak.chakak.account.domain.User;
import com.green.chakak.chakak.community.domain.Post;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

public class PostRequest {

    @Data
    @NoArgsConstructor
    public static class CreateDTO {

        @NoSensitiveInfo
        @NotBlank(message = "제목은 필수 입력사항입니다")
        @Size(max = 50, message = "제목은 50자를 초과할 수 없습니다")
        private String title;

        @NoSensitiveInfo
        @NotBlank(message = "내용은 필수 입력사항입니다")
        @Size(max = 1500, message = "내용은 1500자를 초과할 수 없습니다")
        private String content;

        // Base64 인코딩된 이미지 데이터 (선택사항)
        private String imageData;

        public Post toEntity(User user) {
            return Post.builder()
                    .user(user)
                    .title(this.title)
                    .content(this.content)
                    .imageUrl(this.imageData)
                    .status(Post.PostStatus.ACTIVE)
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    public static class UpdateDTO {

        @NoSensitiveInfo
        @NotBlank(message = "제목은 필수 입력사항입니다")
        @Size(max = 50, message = "제목은 50자를 초과할 수 없습니다")
        private String title;

        @NoSensitiveInfo
        @NotBlank(message = "내용은 필수 입력사항입니다")
        @Size(min = 10, max = 1500, message = "내용은 최소 10자, 최대 1500자 작성가능합니다")
        private String content;

        // Base64 인코딩된 이미지 데이터 (선택사항)
        // null이면 기존 이미지 유지, 빈 문자열이면 이미지 삭제
        private String imageData;
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