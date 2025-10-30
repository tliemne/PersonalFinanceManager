package com.example.PersonalFinanceManager.service;

import com.example.PersonalFinanceManager.dto.BudgetDTO;
import com.example.PersonalFinanceManager.model.*;
import com.example.PersonalFinanceManager.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BudgetService implements BudgetServiceImpl {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TransactionRepository transactionRepository; // ✅ thêm repository này

    // ===================== CREATE =====================
    @Override
    @Transactional
    public Budget createBudget(Budget budget) {
        // ✅ Kiểm tra user hợp lệ
        if (budget.getUser() == null || budget.getUser().getId() == null) {
            throw new IllegalArgumentException("❌ User không hợp lệ hoặc chưa được chọn");
        }

        User user = userRepository.findById(budget.getUser().getId())
                .orElseThrow(() -> new IllegalArgumentException("❌ Không tìm thấy User với ID: " + budget.getUser().getId()));
        budget.setUser(user);

        // ✅ Kiểm tra category hợp lệ
        if (budget.getCategory() == null || budget.getCategory().getId() == null) {
            throw new IllegalArgumentException("❌ Category không hợp lệ hoặc chưa được chọn");
        }

        Category category = categoryRepository.findById(budget.getCategory().getId())
                .orElseThrow(() -> new IllegalArgumentException("❌ Không tìm thấy Category với ID: " + budget.getCategory().getId()));
        budget.setCategory(category);

        // ✅ Gán giá trị mặc định
        if (budget.getIsDeleted() == null) budget.setIsDeleted(false);

        if (budget.getStartDate() == null) budget.setStartDate(LocalDate.now());
        if (budget.getEndDate() == null) budget.setEndDate(budget.getStartDate().plusMonths(1));

        if (budget.getAmountLimit() == null || budget.getAmountLimit() <= 0) {
            throw new IllegalArgumentException("❌ Hạn mức ngân sách phải lớn hơn 0");
        }

        // ✅ Tự động tính usedAmount từ transaction
        double used = calculateUsedAmountFromTransactions(
                budget.getUser().getId(),
                budget.getCategory().getId(),
                budget.getStartDate(),
                budget.getEndDate()
        );
        budget.setUsedAmount(used);

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
    public Budget updateBudget(Long id, BudgetDTO dto) {
        return budgetRepository.findById(id).map(existing -> {

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
            if (dto.getStartDate() != null)
                existing.setStartDate(dto.getStartDate());
            if (dto.getEndDate() != null)
                existing.setEndDate(dto.getEndDate());
            if (dto.getIsDeleted() != null)
                existing.setIsDeleted(dto.getIsDeleted());

            // ✅ Luôn tính lại usedAmount dựa theo giao dịch
            if (existing.getUser() != null && existing.getCategory() != null) {
                double used = calculateUsedAmountFromTransactions(
                        existing.getUser().getId(),
                        existing.getCategory().getId(),
                        existing.getStartDate(),
                        existing.getEndDate()
                );
                existing.setUsedAmount(used);
            }

            return budgetRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("❌ Budget not found"));
    }

    // ===================== DELETE =====================
    @Override
    public void deleteBudget(Long id) {
        budgetRepository.findById(id).ifPresent(b -> {
            b.setIsDeleted(true);
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

    // ===================== CHUYỂN BUDGET -> DTO =====================
    public BudgetDTO toDTO(Budget b) {
        BudgetDTO dto = new BudgetDTO();

        dto.setId(b.getId());
        dto.setUserId(b.getUser() != null ? b.getUser().getId() : null);
        dto.setUserName(b.getUser() != null ? b.getUser().getFullName() : "Không xác định");

        dto.setCategoryId(b.getCategory() != null ? b.getCategory().getId() : null);
        dto.setCategoryName(b.getCategory() != null ? b.getCategory().getName() : "Không xác định");

        double used = (b.getUsedAmount() != null) ? b.getUsedAmount() : 0.0;
        double limit = (b.getAmountLimit() != null && b.getAmountLimit() > 0) ? b.getAmountLimit() : 0.0;

        dto.setUsedAmount(used);
        dto.setAmountLimit(limit);
        dto.setStartDate(b.getStartDate());
        dto.setEndDate(b.getEndDate());
        dto.setIsDeleted(b.getIsDeleted());

        double actualProgress = 0.0;
        if (limit > 0.0) {
            actualProgress = (used / limit) * 100.0;
        }
        if (Double.isNaN(actualProgress) || Double.isInfinite(actualProgress) || actualProgress < 0)
            actualProgress = 0.0;

        double progress = Math.min(actualProgress, 100.0);

        dto.setProgress(progress);
        dto.setActualProgress(actualProgress);

        String status = (b.getEndDate() != null && b.getEndDate().isBefore(LocalDate.now()))
                ? "Đã hết hạn"
                : "Còn hiệu lực";
        dto.setStatus(status);

        return dto;
    }

    // ===================== HÀM TÍNH USED AMOUNT =====================
    private double calculateUsedAmountFromTransactions(Long userId, Long categoryId, LocalDate start, LocalDate end) {
        if (userId == null || categoryId == null) return 0.0;

        List<Transaction> transactions = transactionRepository
                .findByUser_IdAndCategory_IdAndIsDeletedFalse(userId, categoryId);

        return transactions.stream()
                .filter(tx -> tx.getTransactionDate() != null)
                .filter(tx -> {
                    if (start == null && end == null) return true;
                    if (start == null) return !tx.getTransactionDate().isAfter(end);
                    if (end == null) return !tx.getTransactionDate().isBefore(start);
                    return !tx.getTransactionDate().isBefore(start) && !tx.getTransactionDate().isAfter(end);
                })
                .filter(tx -> tx.getTransactionType() == Transaction.TransactionType.EXPENSE)
                .mapToDouble(tx -> Optional.ofNullable(tx.getAmount()).orElse(0.0))
                .sum();
    }

    // ===================== (TÙY CHỌN) CẬP NHẬT BUDGET SAU KHI GIAO DỊCH THAY ĐỔI =====================
    public void recomputeBudgetsForTransaction(Transaction t) {
        if (t == null || t.getUser() == null || t.getCategory() == null || t.getTransactionDate() == null) return;

        List<Budget> budgets = budgetRepository.findByCategory_IdAndUser_IdAndIsDeletedFalse(
                t.getCategory().getId(), t.getUser().getId()
        );

        for (Budget b : budgets) {
            boolean inRange = !t.getTransactionDate().isBefore(b.getStartDate())
                    && !t.getTransactionDate().isAfter(b.getEndDate());

            if (inRange) {
                double used = calculateUsedAmountFromTransactions(
                        b.getUser().getId(),
                        b.getCategory().getId(),
                        b.getStartDate(),
                        b.getEndDate()
                );
                b.setUsedAmount(used);
                budgetRepository.save(b);
            }
        }
    }
}
