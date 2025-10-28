package com.example.PersonalFinanceManager.dto;

import com.example.PersonalFinanceManager.model.RecurringTransaction.Frequency;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class RecurringTransactionDTO {
    private Long id;
    private Long userId;
    private Long categoryId;
    private Double amount;
    private Frequency frequency;
    private LocalDate nextDate;
    private String description;
    private LocalDateTime createdAt;

    public RecurringTransactionDTO() {}

    public RecurringTransactionDTO(
            Long id,
            Long userId,
            Long categoryId,
            Double amount,
            Frequency frequency,
            LocalDate nextDate,
            String description,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.userId = userId;
        this.categoryId = categoryId;
        this.amount = amount;
        this.frequency = frequency;
        this.nextDate = nextDate;
        this.description = description;
        this.createdAt = createdAt;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public Frequency getFrequency() { return frequency; }
    public void setFrequency(Frequency frequency) { this.frequency = frequency; }

    public LocalDate getNextDate() { return nextDate; }
    public void setNextDate(LocalDate nextDate) { this.nextDate = nextDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
