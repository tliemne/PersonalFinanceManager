package com.example.PersonalFinanceManager.service;

import com.example.PersonalFinanceManager.model.Category;
import com.example.PersonalFinanceManager.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService implements CategoryServiceImpl {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // 🔹 Lấy danh sách category theo userId
    @Override
    public List<Category> getCategoriesByUserId(Long userId) {
        return categoryRepository.findByUser_Id(userId);
    }

    @Override
    public Category updateCategory(Long id, Category category) {
        return categoryRepository.findById(id).map(existing -> {
            existing.setName(category.getName());
            existing.setType(category.getType());
            return categoryRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepository.findById(id).ifPresentOrElse(category -> {
            // 🔹 Vì Category không có isDeleted → xoá cứng luôn
            categoryRepository.delete(category);
        }, () -> {
            throw new RuntimeException("Category with id " + id + " not found");
        });
    }
    @Override
    public List<Category> getIncomeCategories() {
        return categoryRepository.findByType(Category.CategoryType.INCOME);
    }

    @Override
    public List<Category> getExpenseCategories() {
        return categoryRepository.findByType(Category.CategoryType.EXPENSE);
    }
}
