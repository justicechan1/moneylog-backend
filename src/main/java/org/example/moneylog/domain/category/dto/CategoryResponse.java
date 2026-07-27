package org.example.moneylog.domain.category.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.moneylog.domain.category.entitiy.Category;
import org.example.moneylog.domain.category.entitiy.CategoryType;

@Schema(description = "카테고리 응답")
public record CategoryResponse(
        @Schema(description = "카테고리 ID", example = "1") Long id,
        @Schema(description = "카테고리명", example = "식비") String name,
        @Schema(description = "카테고리 타입(수입/지출)") CategoryType type) {
    public static CategoryResponse from(Category category) {
        return new CategoryResponse(category.getId(), category.getName(), category.getType());
    }
}
