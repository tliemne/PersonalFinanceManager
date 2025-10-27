package com.example.PersonalFinanceManager.dto;

import java.time.LocalDate;

public class BudgetDTO {
    private Long id;
    private Long userId;       // liên kết với User
    private Long categoryId;   // liên kết với Category
    private Double amountLimit;
    private Double usedAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isDeleted;

    public BudgetDTO() {}

    // Getters và Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public Double getAmountLimit() { return amountLimit; }
    public void setAmountLimit(Double amountLimit) { this.amountLimit = amountLimit; }

    public Double getUsedAmount() { return usedAmount; }
    public void setUsedAmount(Double usedAmount) { this.usedAmount = usedAmount; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }
}
