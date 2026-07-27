package org.example.moneylog.domain.category.service;

import lombok.RequiredArgsConstructor;
import org.example.moneylog.domain.category.entitiy.Category;
import org.example.moneylog.domain.category.entitiy.CategoryType;
import org.example.moneylog.domain.category.repository.CategoryRepository;
import org.example.moneylog.domain.user.entitiy.User;
import org.example.moneylog.domain.user.repository.UserRepository;
import org.example.moneylog.common.exception.CustomException;
import org.example.moneylog.common.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriesService {
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    // 내 카테고리 목록 조회
    @Transactional(readOnly = true)
    public List<Category> getMyCategories(Long userId) {
        return categoryRepository.findByUserId(userId);
    }

    //기본 카테고리 시드
    @Transactional
    public void seedDefaultCategories(Long userId) {
        if (categoryRepository.existsByUserId(userId)) return;   // 중복 시드 방지

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<Category> defaults = List.of(
                Category.of(user, "식비", CategoryType.EXPENSE),
                Category.of(user, "교통", CategoryType.EXPENSE),
                Category.of(user, "주거", CategoryType.EXPENSE),
                Category.of(user, "문화", CategoryType.EXPENSE),
                Category.of(user, "급여", CategoryType.INCOME),
                Category.of(user, "용돈", CategoryType.INCOME)
        );
        categoryRepository.saveAll(defaults);
    }
}
