package com.green.chakak.chakak._global.utils;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;
import java.util.List;

public class PageUtil {
    @Getter
    public static class PageResponse<T> {
        private final List<T> content;
        private final int page;
        private final int size;
        private final long totalElements;
        private final int totalPages;
        private final boolean last;

        @Builder
        public PageResponse(List<T> content, int page, int size, long totalElements, int totalPages, boolean last) {
            this.content = content;
            this.page = page;
            this.size = size;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
            this.last = last;
        }

        public static <T> PageResponse<T> from(Page<T> page) {
            return PageResponse.<T>builder()
                    .content(page.getContent())
                    .page(page.getNumber())
                    .size(page.getSize())
                    .totalElements(page.getTotalElements())
                    .totalPages(page.getTotalPages())
                    .last(page.isLast())
                    .build();
        }
    }
}

