package com.example.PersonalFinanceManager.service;

import com.example.PersonalFinanceManager.dto.BudgetDTO;
import com.example.PersonalFinanceManager.model.Budget;
import com.example.PersonalFinanceManager.model.Category;
import com.example.PersonalFinanceManager.model.User;
import com.example.PersonalFinanceManager.repository.BudgetRepository;
import com.example.PersonalFinanceManager.repository.CategoryRepository;
import com.example.PersonalFinanceManager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BudgetService implements BudgetServiceImpl {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    // ===================== CREATE =====================
    @Override
    public Budget createBudget(Budget budget) {
        return budgetRepository.save(budget);
    }

    // ===================== READ =====================
    @Override
    public Optional<Budget> getBudgetById(Long id) {
        return budgetRepository.findById(id)
                .filter(b -> b.getIsDeleted() == null || !b.getIsDeleted());
    }

    @Override
    public List<Budget> getAllBudgets() {
        return budgetRepository.findAll().stream()
                .filter(b -> b.getIsDeleted() == null || !b.getIsDeleted())
                .collect(Collectors.toList());
    }

    // ===================== UPDATE =====================
    // ⚙️ Bổ sung xử lý từ BudgetDTO
    public Budget updateBudget(Long id, BudgetDTO dto) {
        return budgetRepository.findById(id).map(existing -> {

            // Nếu DTO có user/category mới thì cập nhật
            if (dto.getUserId() != null) {
                userRepository.findById(dto.getUserId())
                        .ifPresent(existing::setUser);
            }
            if (dto.getCategoryId() != null) {
                categoryRepository.findById(dto.getCategoryId())
                        .ifPresent(existing::setCategory);
            }

            if (dto.getAmountLimit() != null)
                existing.setAmountLimit(dto.getAmountLimit());
            if (dto.getUsedAmount() != null)
                existing.setUsedAmount(dto.getUsedAmount());
            if (dto.getStartDate() != null)
                existing.setStartDate(dto.getStartDate());
            if (dto.getEndDate() != null)
                existing.setEndDate(dto.getEndDate());
            if (dto.getIsDeleted() != null)
                existing.setIsDeleted(dto.getIsDeleted());

            return budgetRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("❌ Budget not found"));
    }

    // ===================== DELETE =====================
    @Override
    public void deleteBudget(Long id) {
        budgetRepository.findById(id).ifPresent(b -> {
            b.setIsDeleted(true); // soft delete
            budgetRepository.save(b);
        });
    }

    // ===================== LẤY NGÂN SÁCH THEO USER =====================
    public List<Budget> getBudgetsByUserId(Long userId) {
        return budgetRepository.findAll().stream()
                .filter(b -> b.getUser() != null
                        && b.getUser().getId().equals(userId)
                        && (b.getIsDeleted() == null || !b.getIsDeleted()))
                .collect(Collectors.toList());
    }
}
