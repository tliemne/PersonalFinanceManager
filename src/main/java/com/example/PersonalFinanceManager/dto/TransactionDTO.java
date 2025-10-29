package com.example.PersonalFinanceManager.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TransactionDTO {

    private Long id;
    private Long userId;
    private Long accountId;
    private Long categoryId;

    private String categoryName; // ✅ Thêm nhẹ: hiển thị Dashboard đẹp, không bắt buộc
    private Double amount;
    private String transactionType;
    private String status;
    private Boolean isDeleted;
    private String description;
    private LocalDate transactionDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TransactionDTO() {}

    public TransactionDTO(
            Long id,
            Long userId,
            Long accountId,
            Long categoryId,
            String categoryName, // ✅ thêm tham số mới
            Double amount,
            String transactionType,
            String status,
            Boolean isDeleted,
            String description,
            LocalDate transactionDate,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.userId = userId;
        this.accountId = accountId;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.amount = amount;
        this.transactionType = transactionType;
        this.status = status;
        this.isDeleted = isDeleted;
        this.description = description;
        this.transactionDate = transactionDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ✅ Constructor không có categoryName (cho service cũ vẫn chạy)
    public TransactionDTO(
            Long id,
            Long userId,
            Long accountId,
            Long categoryId,
            Double amount,
            String transactionType,
            String status,
            Boolean isDeleted,
            String description,
            LocalDate transactionDate,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this(id, userId, accountId, categoryId, null, amount, transactionType, status,
                isDeleted, description, transactionDate, createdAt, updatedAt);
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDate transactionDate) { this.transactionDate = transactionDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
