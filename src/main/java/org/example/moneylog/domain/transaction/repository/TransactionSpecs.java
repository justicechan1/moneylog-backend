package org.example.moneylog.domain.transaction.repository;

import org.example.moneylog.domain.transaction.dto.TransactionSearchRequest;
import org.example.moneylog.domain.transaction.entitiy.Transaction;
import org.springframework.data.jpa.domain.Specification;

// TransactionSearchRequest → Specification 변환. 조건마다 (:param IS NULL OR ...) 가 늘어지는 것을 피하기 위해 도입
public class TransactionSpecs {

    private TransactionSpecs() {
    }

    public static Specification<Transaction> of(Long userId, TransactionSearchRequest cond) {
        return Specification
                .where(userIdEq(userId))
                .and(keywordLike(cond.keyword()))
                .and(dateFrom(cond.startDate()))
                .and(dateTo(cond.endDate()))
                .and(amountFrom(cond.minAmount()))
                .and(amountTo(cond.maxAmount()))
                .and(typeEq(cond.type()))
                .and(categoryIdEq(cond.categoryId()));
    }

    private static Specification<Transaction> userIdEq(Long userId) {
        return (root, query, cb) -> cb.equal(root.get("user").get("id"), userId);
    }

    private static Specification<Transaction> keywordLike(String keyword) {
        if (keyword == null || keyword.isBlank()) return null;
        return (root, query, cb) -> cb.like(root.get("description"), "%" + keyword + "%");
    }

    private static Specification<Transaction> dateFrom(java.time.LocalDate startDate) {
        if (startDate == null) return null;
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("transactionDate"), startDate);
    }

    private static Specification<Transaction> dateTo(java.time.LocalDate endDate) {
        if (endDate == null) return null;
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("transactionDate"), endDate);
    }

    private static Specification<Transaction> amountFrom(Long minAmount) {
        if (minAmount == null) return null;
        return (root, query, cb) -> cb.ge(root.get("amount"), minAmount);
    }

    private static Specification<Transaction> amountTo(Long maxAmount) {
        if (maxAmount == null) return null;
        return (root, query, cb) -> cb.le(root.get("amount"), maxAmount);
    }

    private static Specification<Transaction> typeEq(org.example.moneylog.domain.category.entitiy.CategoryType type) {
        if (type == null) return null;
        return (root, query, cb) -> cb.equal(root.get("type"), type);
    }

    private static Specification<Transaction> categoryIdEq(Long categoryId) {
        if (categoryId == null) return null;
        return (root, query, cb) -> cb.equal(root.get("category").get("id"), categoryId);
    }
}
