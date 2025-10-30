package com.example.PersonalFinanceManager.service;

import com.example.PersonalFinanceManager.dto.CategoryDTO;
import com.example.PersonalFinanceManager.model.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryServiceImpl {

    // 🔹 CRUD cơ bản
    Category createCategory(Category category);
    Optional<Category> getCategoryById(Long id);
    List<Category> getAllCategories();
    Category updateCategory(Long id, Category category);
    void deleteCategory(Long id);

    // 🔹 Lấy danh mục entity

    List<Category> getIncomeCategories();
    List<Category> getExpenseCategories();

    // 🔹 🆕 Thêm phiên bản DTO (dành cho controller dùng để render view nhanh, tránh vòng lặp)
    List<CategoryDTO> getCategoriesByUserIdDTO(Long userId);
    List<CategoryDTO> getIncomeCategoriesDTO();
    List<CategoryDTO> getExpenseCategoriesDTO();
}
