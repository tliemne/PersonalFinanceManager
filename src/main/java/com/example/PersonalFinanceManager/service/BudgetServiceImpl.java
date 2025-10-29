package com.example.PersonalFinanceManager.service;

import com.example.PersonalFinanceManager.dto.BudgetDTO;
import com.example.PersonalFinanceManager.model.Budget;

import java.util.List;
import java.util.Optional;

public interface BudgetServiceImpl {
    // Tạo mới ngân sách
    Budget createBudget(Budget budget);

    // Lấy ngân sách theo ID
    Optional<Budget> getBudgetById(Long id);

    // Lấy tất cả ngân sách (lọc soft delete)
    List<Budget> getAllBudgets();

    // Cập nhật ngân sách (sử dụng DTO để linh hoạt hơn)
    Budget updateBudget(Long id, BudgetDTO budgetDTO);

    // Xóa mềm ngân sách
    void deleteBudget(Long id);

    // Lấy danh sách ngân sách theo userId
    List<Budget> getBudgetsByUserId(Long userId);
}
