package org.example.moneylog.domain.category.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.moneylog.common.response.ApiResponse;
import org.example.moneylog.domain.category.dto.CategoryResponse;
import org.example.moneylog.domain.category.service.CategoriesService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "카테고리", description = "카테고리 조회 API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoriesService categoriesService;

    @Operation(summary = "카테고리 목록 조회", description = "로그인한 사용자가 사용 가능한 전체 카테고리 목록을 조회합니다.")
    @GetMapping
    public ApiResponse<List<CategoryResponse>> getList(@Parameter(hidden = true) @AuthenticationPrincipal Long userId) {
        List<CategoryResponse> data = categoriesService.getMyCategories(userId).stream()
                .map(CategoryResponse::from)
                .toList();
        return ApiResponse.success("카테고리 목록 조회에 성공했습니다.", data);
    }
}
