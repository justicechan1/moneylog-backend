package org.example.moneylog.domain.category.repository;

import org.example.moneylog.domain.category.entitiy.Category;
import org.example.moneylog.domain.user.entitiy.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUser(User user);        // 내 카테고리 목록
}
