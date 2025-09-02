package com.green.chakak.chakak.photo.domain;

import com.green.chakak.chakak.photo.service.request.PhotoCategoryRequest;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
@Table(name = "photo_service_category")
public class PhotoServiceCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @Column(nullable = false, length = 50)
    private String categoryName;

    @Column(nullable = false)
    @Lob
    private String categoryImageData;

    @Builder
    public PhotoServiceCategory(Long categoryId, String categoryName, String categoryImageData) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categoryImageData = categoryImageData;
    }

    public void updateFromDto(PhotoCategoryRequest.UpdateDTO dto) {
        if (dto.getCategoryName() != null && !dto.getCategoryName().trim().isEmpty()) {
            this.categoryName = dto.getCategoryName().trim();
        }
        if (dto.getCategoryImageData() != null && !dto.getCategoryImageData().trim().isEmpty()) {
            this.categoryImageData = dto.getCategoryImageData();
        }
    }
}