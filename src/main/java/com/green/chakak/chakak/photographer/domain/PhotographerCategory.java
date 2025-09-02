package com.green.chakak.chakak.photographer.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Table(name = "photographer_category")
@Entity
public class PhotographerCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @Column(nullable = false)
    private String categoryName;

    @Builder
    public PhotographerCategory(Long categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public void update(@NotBlank(message = "카테고리명은 필수입니다.")
                       @Size(max = 50, message = "카테고리명은 50자를 초과할 수 없습니다.")
                       String categoryName) {
        this.categoryName = categoryName;
    }
}
