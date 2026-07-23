package org.example.moneylog.domain.category.entitiy;

import jakarta.persistence.*;
import lombok.*;
import org.example.moneylog.domain.user.entitiy.User;
import org.example.moneylog.global.entity.BaseTimeEntity;

@Getter
@Entity
@Table(name = "categories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //카테고리 고유 ID

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  //사용자 ID

    @Column(nullable = false, length = 50)
    private String name; //카테고리명

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private CategoryType type;     // INCOME(수입) / EXPENSE(지출)

    @Builder
    public Category(User user, String name, CategoryType type) {
        this.user = user;
        this.name = name;
        this.type = type;
    }

    // 수정은 Setter가 아닌 의도가 드러나는 메서드로
    public void update(String name, CategoryType type) {
        this.name = name;
        this.type = type;
    }
}
