package org.example.moneylog.global.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.moneylog.domain.category.entitiy.Category;
import org.example.moneylog.domain.category.entitiy.CategoryType;
import org.example.moneylog.domain.category.repository.CategoryRepository;
import org.example.moneylog.domain.transaction.entitiy.Transaction;
import org.example.moneylog.domain.transaction.repository.TransactionRepository;
import org.example.moneylog.domain.user.entitiy.User;
import org.example.moneylog.domain.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Profile("local")
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        // 1) 저장
        User user = userRepository.save(User.builder()
                .email("test@likelion.com")
                .password(passwordEncoder.encode("password1234"))   // 로그인 테스트용 원문: password1234
                .nickname("머니로그유저")
                .build());

        Category food = categoryRepository.save(Category.builder()
                .user(user).name("식비").type(CategoryType.EXPENSE).build());

        transactionRepository.save(Transaction.builder()
                .user(user).category(food).type(CategoryType.EXPENSE)
                .amount(12000L).description("점심 김치찌개")
                .transactionDate(LocalDate.of(2026, 7, 8))
                .build());

        // 2) 조회 확인
        long count = transactionRepository.count();
        log.info("저장된 거래 수 = {}", count);
        transactionRepository.findAll()
                .forEach(t -> log.info("거래: {}원 / {} / {}",
                        t.getAmount(), t.getCategory().getName(), t.getTransactionDate()));
    }
}
