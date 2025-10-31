package com.example.PersonalFinanceManager.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class GoalDTO {
    private Long id;
    private Long userId;
    private String name;
    private Double targetAmount;
    private Double currentAmount;
    private String priority; // LOW, MEDIUM, HIGH
    private Boolean isDeleted;
    private LocalDate deadline;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 👉 Thêm hai trường mới để hiển thị trong bảng
    private Double progress; // % tiến độ
    private String status;   // Hoàn thành / Đang thực hiện / Quá hạn

    public GoalDTO() {}

    public GoalDTO(Long id, Long userId, String name, Double targetAmount, Double currentAmount,
                   String priority, Boolean isDeleted, LocalDate deadline,
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.priority = priority;
        this.isDeleted = isDeleted;
        this.deadline = deadline;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // 🧩 Thêm constructor đầy đủ (có progress + status)
    public GoalDTO(Long id, Long userId, String name, Double targetAmount, Double currentAmount,
                   String priority, Boolean isDeleted, LocalDate deadline,
                   LocalDateTime createdAt, LocalDateTime updatedAt,
                   Double progress, String status) {
        this(id, userId, name, targetAmount, currentAmount, priority, isDeleted, deadline, createdAt, updatedAt);
        this.progress = progress;
        this.status = status;
    }

    // --- Getters & Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getTargetAmount() { return targetAmount; }
    public void setTargetAmount(Double targetAmount) { this.targetAmount = targetAmount; }

    public Double getCurrentAmount() { return currentAmount; }
    public void setCurrentAmount(Double currentAmount) { this.currentAmount = currentAmount; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }

    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Double getProgress() { return progress; }
    public void setProgress(Double progress) { this.progress = progress; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
