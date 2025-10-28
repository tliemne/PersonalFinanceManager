package com.example.PersonalFinanceManager.dto;

import com.example.PersonalFinanceManager.model.Category.CategoryType;

import java.time.LocalDateTime;

public class CategoryDTO {
    private Long id;
    private Long userId;
    private String name;
    private CategoryType type;
    private LocalDateTime createdAt;

    public CategoryDTO() {}

    public CategoryDTO(Long id, Long userId, String name, CategoryType type, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.type = type;
        this.createdAt = createdAt;
    }

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CategoryType getType() {
        return type;
    }

    public void setType(CategoryType type) {
        this.type = type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

