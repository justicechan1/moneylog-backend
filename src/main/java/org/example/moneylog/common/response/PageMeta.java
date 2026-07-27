package org.example.moneylog.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

// meta.pagination 형식: { "pagination": { page, size, totalItems, totalPages, hasNext, hasPrev } }
@Schema(description = "페이지네이션 메타 정보")
public record PageMeta(@Schema(description = "페이지네이션") Pagination pagination) {

    @Schema(description = "페이지네이션 상세")
    public record Pagination(
            @Schema(description = "현재 페이지(0-base)", example = "0") int page,
            @Schema(description = "페이지 크기", example = "20") int size,
            @Schema(description = "전체 항목 수", example = "42") long totalItems,
            @Schema(description = "전체 페이지 수", example = "3") int totalPages,
            @Schema(description = "다음 페이지 존재 여부") boolean hasNext,
            @Schema(description = "이전 페이지 존재 여부") boolean hasPrev
    ) {}

    // Page<T> → PageMeta 변환 정적 팩토리
    public static PageMeta from(Page<?> page) {
        return new PageMeta(new Pagination(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext(),
                page.hasPrevious()
        ));
    }
}
