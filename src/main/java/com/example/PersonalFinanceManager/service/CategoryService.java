package com.example.PersonalFinanceManager.service;

import com.example.PersonalFinanceManager.model.Category;
import com.example.PersonalFinanceManager.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

public class CategoryService implements CategoryServiceImpl {
    @Autowired
    private CategoryRepository repo;

    @Override
    public Category createCategory(Category category) {
        return null;
    }

    @Override
    public Optional<Category> getCategoryById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Category> getAllCategories() {
        return List.of();
    }

    @Override
    public Category updateCategory(Long id, Category category) {
        return null;
    }

    @Override
    public void deleteCategory(Long id) {

    }
}
