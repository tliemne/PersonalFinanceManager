package com.example.PersonalFinanceManager.repository;

import com.example.PersonalFinanceManager.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUser_Id(Long userId);
    List<Category> findByType(Category.CategoryType type);
}
