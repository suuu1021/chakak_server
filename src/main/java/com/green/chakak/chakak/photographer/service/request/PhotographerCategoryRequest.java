package com.green.chakak.chakak.photographer.service.request;

import com.green.chakak.chakak.photographer.domain.PhotographerCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;


public class PhotographerCategoryRequest {

    @Data
    public static class SaveCategory {
        @NotBlank(message = "카테고리명은 필수입니다.")
        @Size(max = 50, message = "카테고리명은 50자를 초과할 수 없습니다.")
        private String categoryName;

        public PhotographerCategory toEntity() {
            return PhotographerCategory.builder()
                    .categoryName(this.categoryName)
                    .build();
        }
    }

    @Data
    public static class UpdateCategory {
        @NotBlank(message = "카테고리명은 필수입니다.")
        @Size(max = 50, message = "카테고리명은 50자를 초과할 수 없습니다.")
        private String categoryName;

    }

    @Data
    public static class AddCategoryToPhotographer {
        @NotNull(message = "카테고리 ID는 필수입니다.")
        private Long categoryId;
    }

}
