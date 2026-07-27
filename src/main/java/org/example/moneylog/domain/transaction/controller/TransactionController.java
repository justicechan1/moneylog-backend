package org.example.moneylog.domain.transaction.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.moneylog.common.response.ApiResponse;
import org.example.moneylog.common.response.PageMeta;
import org.example.moneylog.domain.category.entitiy.CategoryType;
import org.example.moneylog.domain.transaction.dto.TransactionRequest;
import org.example.moneylog.domain.transaction.dto.TransactionResponse;
import org.example.moneylog.domain.transaction.dto.TransactionSearchCond;
import org.example.moneylog.domain.transaction.dto.TransactionSearchRequest;
import org.example.moneylog.domain.transaction.service.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Tag(name = "거래내역", description = "거래내역 등록/조회/검색/수정/삭제/CSV 내보내기 API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private static final DateTimeFormatter FILENAME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");

    private final TransactionService transactionService;

    @Operation(summary = "거래내역 등록", description = "수입 또는 지출 거래내역을 새로 등록합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<TransactionResponse>> create(
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId,
            @Valid @RequestBody TransactionRequest req) {
        TransactionResponse created = transactionService.create(userId, req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("거래내역 등록에 성공했습니다.", created));
    }

    @Operation(summary = "거래내역 목록 조회", description = "지정한 연월의 거래내역을 타입/카테고리로 필터링하여 페이지 단위로 조회합니다.")
    @GetMapping
    public ApiResponse<Map<String, List<TransactionResponse>>> getList(
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId,
            @Parameter(description = "조회할 연월", example = "2026-07") @RequestParam String yearMonth,
            @Parameter(description = "거래 타입(수입/지출)") @RequestParam(required = false) CategoryType type,
            @Parameter(description = "카테고리 ID") @RequestParam(required = false) Long categoryId,
            @PageableDefault(size = 20) Pageable pageable) {
        var cond = new TransactionSearchCond(yearMonth, type, categoryId);
        Page<TransactionResponse> page = transactionService.getList(userId, cond, pageable);
        // data.transactions 형태 + meta.pagination
        var data = Map.of("transactions", page.getContent());
        return ApiResponse.success("거래내역 목록 조회에 성공했습니다.", data, PageMeta.from(page));
    }

    // 복합 검색 — 키워드(description) + 기간(startDate~endDate) + 금액범위(minAmount~maxAmount) + 타입/카테고리, 전부 선택 조합
    @Operation(summary = "거래내역 복합 검색", description = "키워드/기간/금액범위/타입/카테고리를 자유롭게 조합하여 거래내역을 검색합니다.")
    @GetMapping("/search")
    public ApiResponse<Map<String, List<TransactionResponse>>> search(
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId,
            @Parameter(description = "설명(description)에 포함된 키워드") @RequestParam(required = false) String keyword,
            @Parameter(description = "조회 시작일") @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "조회 종료일") @RequestParam(required = false) LocalDate endDate,
            @Parameter(description = "최소 금액") @RequestParam(required = false) Long minAmount,
            @Parameter(description = "최대 금액") @RequestParam(required = false) Long maxAmount,
            @Parameter(description = "거래 타입(수입/지출)") @RequestParam(required = false) CategoryType type,
            @Parameter(description = "카테고리 ID") @RequestParam(required = false) Long categoryId,
            @PageableDefault(size = 20) Pageable pageable) {
        var cond = new TransactionSearchRequest(keyword, startDate, endDate, minAmount, maxAmount, type, categoryId);
        Page<TransactionResponse> page = transactionService.search(userId, cond, pageable);
        var data = Map.of("transactions", page.getContent());
        return ApiResponse.success("거래내역 검색에 성공했습니다.", data, PageMeta.from(page));
    }

    // CSV 내보내기 — /search와 동일한 필터 조건, 결과 전체를 스트리밍으로 다운로드
    @Operation(summary = "거래내역 CSV 내보내기", description = "검색 조건에 맞는 거래내역 전체를 CSV 파일로 스트리밍 다운로드합니다.")
    @GetMapping("/export")
    public ResponseEntity<StreamingResponseBody> export(
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId,
            @Parameter(description = "설명(description)에 포함된 키워드") @RequestParam(required = false) String keyword,
            @Parameter(description = "조회 시작일") @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "조회 종료일") @RequestParam(required = false) LocalDate endDate,
            @Parameter(description = "최소 금액") @RequestParam(required = false) Long minAmount,
            @Parameter(description = "최대 금액") @RequestParam(required = false) Long maxAmount,
            @Parameter(description = "거래 타입(수입/지출)") @RequestParam(required = false) CategoryType type,
            @Parameter(description = "카테고리 ID") @RequestParam(required = false) Long categoryId) {
        var cond = new TransactionSearchRequest(keyword, startDate, endDate, minAmount, maxAmount, type, categoryId);
        List<TransactionResponse> rows = transactionService.searchAll(userId, cond);

        String filename = "transactions_" + (startDate != null ? startDate : LocalDate.now()).format(FILENAME_FORMAT) + ".csv";
        StreamingResponseBody body = out -> {
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8))) {
                writer.write("\uFEFF");   // 엑셀에서 UTF-8 한글이 깨지지 않도록 BOM 추가
                writer.write("id,type,amount,categoryName,description,transactionDate,createdAt");
                writer.newLine();
                for (TransactionResponse t : rows) {
                    writer.write(String.join(",",
                            String.valueOf(t.id()),
                            t.type().name(),
                            String.valueOf(t.amount()),
                            csvEscape(t.categoryName()),
                            csvEscape(t.description()),
                            String.valueOf(t.transactionDate()),
                            String.valueOf(t.createdAt())));
                    writer.newLine();
                }
                writer.flush();
            }
        };

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(filename, StandardCharsets.UTF_8).build().toString())
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(body);
    }

    @Operation(summary = "거래내역 단건 조회", description = "ID로 거래내역 상세를 조회합니다.")
    @GetMapping("/{id}")
    public ApiResponse<TransactionResponse> get(
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId,
            @Parameter(description = "거래내역 ID") @PathVariable Long id) {
        return ApiResponse.success("거래내역 조회에 성공했습니다.",
                transactionService.get(userId, id));
    }

    @Operation(summary = "거래내역 수정", description = "ID로 지정한 거래내역의 내용을 수정합니다.")
    @PutMapping("/{id}")
    public ApiResponse<TransactionResponse> update(
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId,
            @Parameter(description = "거래내역 ID") @PathVariable Long id,
            @Valid @RequestBody TransactionRequest req) {
        return ApiResponse.success("거래내역 수정에 성공했습니다.",
                transactionService.update(userId, id, req));
    }

    @Operation(summary = "거래내역 삭제", description = "ID로 지정한 거래내역을 삭제합니다.")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId,
            @Parameter(description = "거래내역 ID") @PathVariable Long id) {
        transactionService.delete(userId, id);
        return ApiResponse.success("거래내역 삭제에 성공했습니다.", null);
    }

    // CSV 필드에 콤마/큰따옴표/줄바꿈이 있으면 큰따옴표로 감싸고 내부 큰따옴표는 이스케이프
    private static String csvEscape(String value) {
        if (value == null) return "";
        boolean needsQuote = value.contains(",") || value.contains("\"") || value.contains("\n");
        String escaped = value.replace("\"", "\"\"");
        return needsQuote ? "\"" + escaped + "\"" : escaped;
    }
}
