package org.example.moneylog.domain.transaction.service;

import lombok.RequiredArgsConstructor;
import org.example.moneylog.domain.category.entitiy.Category;
import org.example.moneylog.domain.category.repository.CategoryRepository;
import org.example.moneylog.domain.transaction.dto.TransactionRequest;
import org.example.moneylog.domain.transaction.dto.TransactionResponse;
import org.example.moneylog.domain.transaction.dto.TransactionSearchCond;
import org.example.moneylog.domain.transaction.dto.TransactionSearchRequest;
import org.example.moneylog.domain.transaction.entitiy.Transaction;
import org.example.moneylog.domain.transaction.repository.TransactionRepository;
import org.example.moneylog.domain.transaction.repository.TransactionSpecs;
import org.example.moneylog.domain.user.entitiy.User;
import org.example.moneylog.domain.user.repository.UserRepository;
import org.example.moneylog.common.exception.CustomException;
import org.example.moneylog.common.exception.ErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    // 등록 — userId는 1-8에서 로그인 사용자로 교체 예정
    @Transactional
    public TransactionResponse create(Long userId, TransactionRequest req) {
        // 카테고리 존재 + 소유자 검증 — SPEC 표준 에러 코드 CATEGORY_NOT_FOUND
        Category category = categoryRepository.findByIdAndUserId(req.categoryId(), userId)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        // 연관관계 저장을 위해서는 실제 User 엔티티가 필요 — id만으로는 매핑 불가
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Transaction tx = Transaction.builder()
                .user(user)
                .category(category)
                .type(req.type())
                .amount(req.amount())
                .description(req.description())
                .transactionDate(req.transactionDate())
                .build();

        return TransactionResponse.from(transactionRepository.save(tx));
    }

    // 목록 조회 — 월별 필터 + (선택)타입/카테고리 + 페이징
    @Transactional(readOnly = true)
    public Page<TransactionResponse> getList(Long userId, TransactionSearchCond cond, Pageable pageable) {
        YearMonth ym = YearMonth.parse(cond.yearMonth());   // "2026-07"
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        Page<Transaction> page = transactionRepository.search(
                userId, start, end, cond.type(), cond.categoryId(), pageable);

        return page.map(TransactionResponse::from);   // Page 내부 요소만 DTO로 변환
    }

    // 복합 검색 — 키워드/기간/금액범위/타입/카테고리 (전부 선택, 조합 가능) + 페이징
    @Transactional(readOnly = true)
    public Page<TransactionResponse> search(Long userId, TransactionSearchRequest cond, Pageable pageable) {
        return transactionRepository.findAll(TransactionSpecs.of(userId, cond), pageable)
                .map(TransactionResponse::from);
    }

    // CSV 내보내기용 — 검색과 동일한 조건으로 전체 결과를 최신순으로 조회(페이징 없음)
    @Transactional(readOnly = true)
    public List<TransactionResponse> searchAll(Long userId, TransactionSearchRequest cond) {
        Sort sort = Sort.by(Sort.Direction.DESC, "transactionDate").and(Sort.by(Sort.Direction.DESC, "id"));
        return transactionRepository.findAll(TransactionSpecs.of(userId, cond), sort).stream()
                .map(TransactionResponse::from)
                .toList();
    }

    // 단건 조회
    @Transactional(readOnly = true)
    public TransactionResponse get(Long userId, Long id) {
        return TransactionResponse.from(findOwned(userId, id));
    }

    // 수정
    @Transactional
    public TransactionResponse update(Long userId, Long id, TransactionRequest req) {
        Transaction tx = findOwned(userId, id);
        Category category = categoryRepository.findByIdAndUserId(req.categoryId(), userId)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
        // 엔티티의 도메인 메서드로 상태 변경(더티 체킹으로 자동 반영)
        tx.update( category, req.type(), req.amount(), req.description(), req.transactionDate());
        return TransactionResponse.from(tx);
    }

    // 삭제
    @Transactional
    public void delete(Long userId, Long id) {
        Transaction tx = findOwned(userId, id);
        transactionRepository.delete(tx);
    }

    // 본인 소유 거래만 조회하는 공통 헬퍼(인가의 핵심)
    // 존재 자체가 없으면 404, 존재하지만 남의 것이면 403으로 구분한다
    private Transaction findOwned(Long userId, Long id) {
        Transaction tx = transactionRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.TRANSACTION_NOT_FOUND));
        if (!tx.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
        return tx;
    }

}