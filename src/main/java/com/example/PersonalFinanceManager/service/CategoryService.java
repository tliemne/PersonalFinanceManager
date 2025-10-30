package com.example.PersonalFinanceManager.service;

import com.example.PersonalFinanceManager.dto.CategoryDTO;
import com.example.PersonalFinanceManager.model.Category;
import com.example.PersonalFinanceManager.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService implements CategoryServiceImpl {

    @Autowired
    private CategoryRepository categoryRepository;

    // 🔹 Hàm helper để map sang DTO
    private CategoryDTO toDTO(Category category) {
        return new CategoryDTO(
                category.getId(),
                category.getUser() != null ? category.getUser().getId() : null,
                category.getName(),
                category.getType(),
                category.getCreatedAt()
        );
    }

    // 🔹 Tạo mới
    @Override
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    // 🔹 Lấy theo ID
    @Override
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    // 🔹 Lấy tất cả (entity)
    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // 🔹 Lấy danh mục theo User ID (→ DTO)
    public List<CategoryDTO> getCategoriesByUserIdDTO(Long userId) {
        return categoryRepository.findByUser_Id(userId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // 🔹 Lấy danh mục thu nhập (→ DTO)
    public List<CategoryDTO> getIncomeCategoriesDTO() {
        return categoryRepository.findByType(Category.CategoryType.INCOME)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // 🔹 Lấy danh mục chi tiêu (→ DTO)
    public List<CategoryDTO> getExpenseCategoriesDTO() {
        return categoryRepository.findByType(Category.CategoryType.EXPENSE)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // 🔹 Cập nhật danh mục
    @Override
    public Category updateCategory(Long id, Category category) {
        return categoryRepository.findById(id)
                .map(existing -> {
                    existing.setName(category.getName());
                    existing.setType(category.getType());
                    return categoryRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
    }

    // 🔹 Xóa danh mục
    @Override
    public void deleteCategory(Long id) {
        categoryRepository.findById(id).ifPresentOrElse(
                categoryRepository::delete,
                () -> { throw new RuntimeException("Category with id " + id + " not found"); }
        );
    }

    // Các hàm cũ vẫn giữ để dùng nội bộ
    @Override
    public List<Category> getIncomeCategories() {
        return categoryRepository.findByType(Category.CategoryType.INCOME);
    }

    @Override
    public List<Category> getExpenseCategories() {
        return categoryRepository.findByType(Category.CategoryType.EXPENSE);
    }
}
