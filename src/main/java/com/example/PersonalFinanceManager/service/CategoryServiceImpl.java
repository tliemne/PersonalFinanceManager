package com.example.PersonalFinanceManager.service;

import com.example.PersonalFinanceManager.model.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryServiceImpl {
    Category createCategory(Category category);
    Optional<Category> getCategoryById(Long id);
    List<Category> getAllCategories();
    Category updateCategory(Long id, Category category);
    void deleteCategory(Long id);

    // 🆕 Thêm dòng này
    List<Category> getCategoriesByUserId(Long userId);
    List<Category> getIncomeCategories();
    List<Category> getExpenseCategories();
}
