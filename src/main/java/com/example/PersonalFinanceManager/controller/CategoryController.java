package com.example.PersonalFinanceManager.controller;

import com.example.PersonalFinanceManager.dto.CategoryDTO;
import com.example.PersonalFinanceManager.model.Category;
import com.example.PersonalFinanceManager.model.User;
import com.example.PersonalFinanceManager.service.CategoryService;
import com.example.PersonalFinanceManager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService; // dùng để lấy User theo userId


    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        List<CategoryDTO> result = categoryService.getAllCategories()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return ResponseEntity.ok(convertToDTO(category));
    }
    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody CategoryDTO dto) {
        User user = userService.getUserById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Category category = new Category();
        category.setUser(user);
        category.setName(dto.getName());
        category.setType(dto.getType());

        Category saved = categoryService.createCategory(category);
        return ResponseEntity.ok(convertToDTO(saved));
    }
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long id, @RequestBody CategoryDTO dto) {
        Category category = new Category();
        category.setName(dto.getName());
        category.setType(dto.getType());

        Category updated = categoryService.updateCategory(id, category);
        return ResponseEntity.ok(convertToDTO(updated));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
    private CategoryDTO convertToDTO(Category category) {
        return new CategoryDTO(
                category.getId(),
                category.getUser().getId(),
                category.getName(),
                category.getType(),
                category.getCreatedAt()
        );
    }
}
