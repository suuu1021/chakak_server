package com.green.chakak.chakak.payment.repository.response;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
public class PageResponse<T> {
    private List<T> content;            // 실제 데이터 리스트
    private int page;                   // 현재 페이지 번호 (0부터 시작)
    private int size;                   // 페이지 크기
    private long totalElements;         // 전체 데이터 개수
    private int totalPages;             // 전체 페이지 수
    private boolean first;              // 첫 번째 페이지 여부
    private boolean last;               // 마지막 페이지 여부
    private boolean hasNext;            // 다음 페이지 존재 여부
    private boolean hasPrevious;        // 이전 페이지 존재 여부

    // Spring Data Page 객체로부터 PageResponse 생성하는 정적 메서드
    public static <T> PageResponse<T> of(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }

    // 데이터 변환용 정적 메서드 (Entity -> DTO 변환 시 사용)
    public static <T, R> PageResponse<R> of(Page<T> page, List<R> convertedContent) {
        return PageResponse.<R>builder()
                .content(convertedContent)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }
}
