package com.example.PersonalFinanceManager.service;

import com.example.PersonalFinanceManager.model.Budget;
import com.example.PersonalFinanceManager.model.Transaction;
import com.example.PersonalFinanceManager.repository.BudgetRepository;
import com.example.PersonalFinanceManager.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class    TransactionService implements TransactionServiceImpl {

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private BudgetService budgetService;
    @Autowired
    private BudgetRepository budgetRepository;
    @Override
    @Transactional
    public Transaction createTransaction(Transaction transaction) {
        Transaction saved = transactionRepository.save(transaction);

        // ⚡ Sau khi lưu giao dịch, cập nhật ngân sách nếu là chi tiêu
        if (saved.getTransactionType() == Transaction.TransactionType.EXPENSE
                && saved.getCategory() != null) {

            // Tìm ngân sách theo category và user
            List<Budget> budgets = budgetRepository.findByCategory_IdAndUser_IdAndIsDeletedFalse(
                    saved.getCategory().getId(),
                    saved.getUser().getId()
            );

            if (!budgets.isEmpty()) {
                Budget budget = budgets.get(0); // lấy ngân sách đầu tiên (1 category thường 1 budget)
                double used = budget.getUsedAmount() != null ? budget.getUsedAmount() : 0.0;
                double newUsed = used + saved.getAmount();

                // 🚫 Nếu vượt quá giới hạn, chỉ set tối đa = giới hạn
                if (budget.getAmountLimit() != null && newUsed > budget.getAmountLimit()) {
                    System.out.println("⚠️ Giao dịch vượt ngân sách cho danh mục: " + budget.getCategory().getName());
                }

                budget.setUsedAmount(newUsed);
                budgetRepository.save(budget);
            }
        }

        return saved;
    }

    @Override
    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    @Override
    public List<Transaction> getAllTransactions() {
        // 🔹 Lấy tất cả giao dịch chưa bị xóa
        return transactionRepository.findByIsDeletedFalse();
    }

    // 🔹 Lấy các giao dịch đã xóa (thùng rác)
    public List<Transaction> getDeletedTransactions() {
        return transactionRepository.findByIsDeletedTrue();
    }

    @Override
    @Transactional
    public Transaction updateTransaction(Long id, Transaction transaction) {
        return transactionRepository.findById(id).map(existing -> {

            boolean wasExpense = existing.getTransactionType() == Transaction.TransactionType.EXPENSE;
            boolean isExpense = transaction.getTransactionType() == Transaction.TransactionType.EXPENSE;

            Long oldCategoryId = existing.getCategory() != null ? existing.getCategory().getId() : null;
            Long newCategoryId = transaction.getCategory() != null ? transaction.getCategory().getId() : null;
            Long userId = existing.getUser() != null ? existing.getUser().getId() : null;

            Double oldAmount = existing.getAmount() != null ? existing.getAmount() : 0.0;
            Double newAmount = transaction.getAmount() != null ? transaction.getAmount() : 0.0;

            // 🔹 Nếu là chi tiêu cũ => trừ ngân sách cũ
            if (wasExpense && userId != null && oldCategoryId != null) {
                List<Budget> oldBudgets = budgetRepository.findByCategory_IdAndUser_IdAndIsDeletedFalse(oldCategoryId, userId);
                if (!oldBudgets.isEmpty()) {
                    Budget oldBudget = oldBudgets.get(0);
                    double used = oldBudget.getUsedAmount() != null ? oldBudget.getUsedAmount() : 0.0;
                    double newUsed = used - oldAmount;
                    if (newUsed < 0) newUsed = 0.0;
                    oldBudget.setUsedAmount(newUsed);
                    budgetRepository.save(oldBudget);
                }
            }

            // 🔹 Cập nhật thông tin giao dịch
            existing.setAmount(transaction.getAmount());
            existing.setTransactionType(transaction.getTransactionType());
            existing.setDescription(transaction.getDescription());
            existing.setTransactionDate(transaction.getTransactionDate());
            existing.setStatus(transaction.getStatus());
            existing.setIsDeleted(transaction.getIsDeleted());
            existing.setCategory(transaction.getCategory());
            existing.setUser(transaction.getUser());
            existing.setAccount(transaction.getAccount());

            Transaction updated = transactionRepository.save(existing);

            // 🔹 Nếu là chi tiêu mới => cộng vào ngân sách mới
            if (isExpense && userId != null && newCategoryId != null) {
                List<Budget> newBudgets = budgetRepository.findByCategory_IdAndUser_IdAndIsDeletedFalse(newCategoryId, userId);
                if (!newBudgets.isEmpty()) {
                    Budget newBudget = newBudgets.get(0);
                    double used = newBudget.getUsedAmount() != null ? newBudget.getUsedAmount() : 0.0;
                    double newUsed = used + newAmount;

                    if (newBudget.getAmountLimit() != null && newUsed > newBudget.getAmountLimit()) {
                        newUsed = newBudget.getAmountLimit();
                    }

                    newBudget.setUsedAmount(newUsed);
                    budgetRepository.save(newBudget);
                }
            }

            return updated;
        }).orElseThrow(() -> new RuntimeException("Transaction not found"));
    }


    @Override
    @Transactional
    public void deleteTransaction(Long id) {
        transactionRepository.findById(id).ifPresent(t -> {
            if (Boolean.TRUE.equals(t.getIsDeleted())) return; // đã xóa rồi thì bỏ qua
            t.setIsDeleted(true);
            transactionRepository.save(t);

            // ⚡ Nếu là chi tiêu => trừ ngân sách đã dùng
            if (t.getTransactionType() == Transaction.TransactionType.EXPENSE
                    && t.getCategory() != null && t.getUser() != null) {

                List<Budget> budgets = budgetRepository.findByCategory_IdAndUser_IdAndIsDeletedFalse(
                        t.getCategory().getId(),
                        t.getUser().getId()
                );

                if (!budgets.isEmpty()) {
                    Budget budget = budgets.get(0);
                    double used = budget.getUsedAmount() != null ? budget.getUsedAmount() : 0.0;
                    double newUsed = used - (t.getAmount() != null ? t.getAmount() : 0.0);
                    if (newUsed < 0) newUsed = 0.0; // không để âm
                    budget.setUsedAmount(newUsed);
                    budgetRepository.save(budget);
                }
            }
        });
    }

    @Override
    public List<Transaction> getTransactionsByUserId(Long userId) {
        return transactionRepository.findByUser_IdAndIsDeletedFalse(userId);
    }

    @Override
    @Transactional
    public void restoreTransaction(Long id) {
        Transaction t = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (Boolean.FALSE.equals(t.getIsDeleted())) return; // chưa bị xóa thì bỏ qua
        t.setIsDeleted(false);
        transactionRepository.save(t);

        // ⚡ Nếu là chi tiêu => cộng lại ngân sách đã dùng
        if (t.getTransactionType() == Transaction.TransactionType.EXPENSE
                && t.getCategory() != null && t.getUser() != null) {

            List<Budget> budgets = budgetRepository.findByCategory_IdAndUser_IdAndIsDeletedFalse(
                    t.getCategory().getId(),
                    t.getUser().getId()
            );

            if (!budgets.isEmpty()) {
                Budget budget = budgets.get(0);
                double used = budget.getUsedAmount() != null ? budget.getUsedAmount() : 0.0;
                double newUsed = used + (t.getAmount() != null ? t.getAmount() : 0.0);

                // 🚫 Nếu vượt giới hạn thì cắt về limit (không để UI hiển thị >100%)
                if (budget.getAmountLimit() != null && newUsed > budget.getAmountLimit()) {
                    newUsed = budget.getAmountLimit();
                    System.out.println("⚠️ Khôi phục giao dịch vượt ngân sách: "
                            + budget.getCategory().getName());
                }

                budget.setUsedAmount(newUsed);
                budgetRepository.save(budget);
            }
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new RuntimeException("Transaction not found with ID: " + id);
        }
        transactionRepository.deleteById(id);
    }
    private void updateBudgetUsedAmount(Long userId, Long categoryId, Double amount) {
        budgetService.getAllBudgets().stream()
                .filter(b -> b.getUser() != null && b.getUser().getId().equals(userId))
                .filter(b -> b.getCategory() != null && b.getCategory().getId().equals(categoryId))
                .findFirst()
                .ifPresent(budget -> {
                    Double used = budget.getUsedAmount() != null ? budget.getUsedAmount() : 0.0;
                    budget.setUsedAmount(used + amount);
                    budgetService.updateBudget(budget.getId(), budgetService.toDTO(budget)); // nếu bạn có DTO
                });
    }
}
