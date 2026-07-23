package org.example.moneylog.domain.transaction.entitiy;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.moneylog.domain.category.entitiy.Category;
import org.example.moneylog.domain.category.entitiy.CategoryType;
import org.example.moneylog.domain.user.entitiy.User;
import org.example.moneylog.global.entity.BaseTimeEntity;

import java.time.LocalDate;

@Getter
@Entity
@Table(name = "transactions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Transaction extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  //거래내역 고유 ID

    // 소유자 (본인 데이터 필터의 기준)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  //사용자 ID

    // 카테고리
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;  //카테고리 ID

    @Enumerated(EnumType.STRING)      // ORDINAL 금지! 문자열로 저장
    @Column(nullable = false, length = 10)
    private CategoryType type;        // INCOME / EXPENSE

    @Column(nullable = false)
    private Long amount;              // 금액(원). 항상 양수

    @Column(length = 255)
    private String description;  //메모 / 설명

    @Column(nullable = false)
    private LocalDate transactionDate;  // 거래 발생일

    @Builder
    public Transaction(User user, Category category, CategoryType type,
                       Long amount, String description, LocalDate transactionDate) {
        this.user = user;
        this.category = category;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.transactionDate = transactionDate;
    }

    // 수정은 Setter가 아닌 의도가 드러나는 메서드로
    public void update(Category category, CategoryType type, Long amount,
                       String description, LocalDate transactionDate) {
        this.category = category;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.transactionDate = transactionDate;
    }

}
