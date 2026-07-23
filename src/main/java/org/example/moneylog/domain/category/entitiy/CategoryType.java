package org.example.moneylog.domain.category.entitiy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
// 2. final이 붙은 필드를 인자로 받는 생성자를 자동으로 만들어줌
@RequiredArgsConstructor
public enum CategoryType {

    // --- Enum 상수 정의 ---
    INCOME, //수입
    EXPENSE //지출
}