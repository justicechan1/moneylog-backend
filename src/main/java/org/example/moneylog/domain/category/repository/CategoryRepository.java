package org.example.moneylog.domain.category.repository;

import org.example.moneylog.domain.category.entitiy.Category;
import org.example.moneylog.domain.user.entitiy.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUser(User user);        // 내 카테고리 목록
    List<Category> findByUserId(Long userId);     // 내 카테고리 목록(ID 기준)
    Optional<Category> findByIdAndUser(Long id, User user);   // 소유자 검증용 단건 조회
    Optional<Category> findByIdAndUserId(Long id, Long userId);
    boolean existsByUserId(Long id);   //아이디 유무 조회
}
