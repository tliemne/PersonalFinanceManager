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

    @Override
    public Category updateCategory(Long id, Category category) {
        return categoryRepository.findById(id).map(e-> {
            e.setName(category.getName());
            e.setType(category.getType());
            return categoryRepository.save(e);
        }).orElseThrow(() -> new RuntimeException("Category not found"));
    }

    @Override
    public void deleteCategory(Long id) {
        System.out.println(">>> Deleting category id = " + id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category id " + id + " not found"));
        System.out.println(">>> Found category: " + category.getName());
        categoryRepository.delete(category);
    }
}
