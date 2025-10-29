package com.example.PersonalFinanceManager.dto;

import java.time.LocalDate;

public class BudgetDTO {
    private Long id;
    private Long userId;           // Liên kết với User
    private String userName;       // Hiển thị tên người dùng (tùy chọn)
    private Long categoryId;       // Liên kết với Category
    private String categoryName;   // Hiển thị tên danh mục
    private Double amountLimit;    // Giới hạn ngân sách
    private Double usedAmount;     // Số tiền đã dùng
    private LocalDate startDate;   // Ngày bắt đầu
    private LocalDate endDate;     // Ngày kết thúc
    private Boolean isDeleted;     // Đã xóa hay chưa

    // ✅ Thông tin mở rộng (tính toán thêm)
    private Double progress;       // Tiến độ sử dụng (%) = usedAmount / amountLimit * 100
    private String status;         // Trạng thái: "Còn hiệu lực" / "Đã hết hạn"

    public BudgetDTO() {}

    // --- Getters và Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

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

    public Double getProgress() { return progress; }
    public void setProgress(Double progress) { this.progress = progress; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
