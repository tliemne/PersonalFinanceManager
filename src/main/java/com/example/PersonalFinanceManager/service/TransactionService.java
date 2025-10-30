package com.example.PersonalFinanceManager.service;

import com.example.PersonalFinanceManager.model.Account;
import com.example.PersonalFinanceManager.model.Budget;
import com.example.PersonalFinanceManager.model.Transaction;
import com.example.PersonalFinanceManager.repository.AccountRepository;
import com.example.PersonalFinanceManager.repository.BudgetRepository;
import com.example.PersonalFinanceManager.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService implements TransactionServiceImpl {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private AccountRepository accountRepository;

    // ============================================================
    // ✅ HELPER – cập nhật & hoàn tác số dư ví (chỉ khi COMPLETED)
    // ============================================================
    private void applyTransactionToAccount(Transaction t) {
        if (t.getAccount() == null || t.getStatus() != Transaction.TransactionStatus.COMPLETED) return;

        double balance = t.getAccount().getBalance() != null ? t.getAccount().getBalance() : 0.0;

        if (t.getTransactionType() == Transaction.TransactionType.EXPENSE) {
            balance -= t.getAmount();
        } else if (t.getTransactionType() == Transaction.TransactionType.INCOME) {
            balance += t.getAmount();
        }

        t.getAccount().setBalance(balance);
        accountRepository.save(t.getAccount());
    }

    private void revertTransactionFromAccount(Transaction t) {
        if (t.getAccount() == null || t.getStatus() != Transaction.TransactionStatus.COMPLETED) return;

        double balance = t.getAccount().getBalance() != null ? t.getAccount().getBalance() : 0.0;

        if (t.getTransactionType() == Transaction.TransactionType.EXPENSE) {
            balance += t.getAmount();
        } else if (t.getTransactionType() == Transaction.TransactionType.INCOME) {
            balance -= t.getAmount();
        }

        t.getAccount().setBalance(balance);
        accountRepository.save(t.getAccount());
    }

    // ============================================================
    // ✅ HELPER – cập nhật ngân sách theo ngày giao dịch
    // ============================================================
    private void applyTransactionToBudget(Transaction t) {
        if (t.getTransactionType() != Transaction.TransactionType.EXPENSE ||
                t.getCategory() == null ||
                t.getUser() == null ||
                t.getStatus() != Transaction.TransactionStatus.COMPLETED) return;

        List<Budget> budgets = budgetRepository.findByCategory_IdAndUser_IdAndIsDeletedFalse(
                t.getCategory().getId(),
                t.getUser().getId()
        );

        Budget activeBudget = budgets.stream()
                .filter(b -> t.getTransactionDate() != null &&
                        b.getStartDate() != null &&
                        b.getEndDate() != null &&
                        !t.getTransactionDate().isBefore(b.getStartDate()) &&
                        !t.getTransactionDate().isAfter(b.getEndDate()))
                .findFirst()
                .orElse(null);

        if (activeBudget != null) {
            double used = activeBudget.getUsedAmount() != null ? activeBudget.getUsedAmount() : 0.0;
            double newUsed = used + t.getAmount();

            if (activeBudget.getAmountLimit() != null && newUsed > activeBudget.getAmountLimit()) {
                System.out.println("⚠️ Giao dịch vượt ngân sách cho danh mục: " + activeBudget.getCategory().getName());
            }

            activeBudget.setUsedAmount(newUsed);
            budgetRepository.save(activeBudget);
        }
    }

    private void revertTransactionFromBudget(Transaction t) {
        if (t.getTransactionType() != Transaction.TransactionType.EXPENSE ||
                t.getCategory() == null ||
                t.getUser() == null ||
                t.getStatus() != Transaction.TransactionStatus.COMPLETED) return;

        List<Budget> budgets = budgetRepository.findByCategory_IdAndUser_IdAndIsDeletedFalse(
                t.getCategory().getId(),
                t.getUser().getId()
        );

        Budget activeBudget = budgets.stream()
                .filter(b -> t.getTransactionDate() != null &&
                        b.getStartDate() != null &&
                        b.getEndDate() != null &&
                        !t.getTransactionDate().isBefore(b.getStartDate()) &&
                        !t.getTransactionDate().isAfter(b.getEndDate()))
                .findFirst()
                .orElse(null);

        if (activeBudget != null) {
            double used = activeBudget.getUsedAmount() != null ? activeBudget.getUsedAmount() : 0.0;
            activeBudget.setUsedAmount(Math.max(0.0, used - t.getAmount()));
            budgetRepository.save(activeBudget);
        }
    }

    // ============================================================
    // ✅ CREATE
    // ============================================================
    @Override
    @Transactional
    public Transaction createTransaction(Transaction transaction) {
        if (transaction.getStatus() == null) {
            transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
        }

        Transaction saved = transactionRepository.save(transaction);

        applyTransactionToAccount(saved);
        applyTransactionToBudget(saved);

        return saved;
    }

    // ============================================================
    // ✅ READ
    // ============================================================
    @Override
    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findByIsDeletedFalse();
    }

    public List<Transaction> getDeletedTransactions() {
        return transactionRepository.findByIsDeletedTrue();
    }

    // ============================================================
    // ✅ UPDATE
    // ============================================================
    @Override
    @Transactional
    public Transaction updateTransaction(Long id, Transaction transaction) {
        return transactionRepository.findById(id).map(existing -> {

            // Hoàn lại số dư và ngân sách cũ
            revertTransactionFromAccount(existing);
            revertTransactionFromBudget(existing);

            // Cập nhật dữ liệu
            existing.setAmount(transaction.getAmount());
            existing.setTransactionType(transaction.getTransactionType());
            existing.setDescription(transaction.getDescription());
            existing.setTransactionDate(transaction.getTransactionDate());
            existing.setIsDeleted(transaction.getIsDeleted());
            existing.setCategory(transaction.getCategory());
            existing.setUser(transaction.getUser());
            existing.setAccount(transaction.getAccount());
            existing.setStatus(transaction.getStatus());

            Transaction updated = transactionRepository.save(existing);

            // Áp dụng lại logic mới
            applyTransactionToAccount(updated);
            applyTransactionToBudget(updated);

            return updated;
        }).orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    // ============================================================
    // ✅ DELETE (soft delete)
    // ============================================================
    @Override
    @Transactional
    public void deleteTransaction(Long id) {
        transactionRepository.findById(id).ifPresent(t -> {
            if (Boolean.TRUE.equals(t.getIsDeleted())) return;

            t.setIsDeleted(true);
            transactionRepository.save(t);

            revertTransactionFromAccount(t);
            revertTransactionFromBudget(t);
        });
    }

    // ============================================================
    // ✅ RESTORE
    // ============================================================
    @Override
    @Transactional
    public void restoreTransaction(Long id) {
        Transaction t = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (Boolean.FALSE.equals(t.getIsDeleted())) return;

        t.setIsDeleted(false);
        transactionRepository.save(t);

        applyTransactionToAccount(t);
        applyTransactionToBudget(t);
    }

    // ============================================================
    // ✅ DELETE BY ID (hard delete)
    // ============================================================
    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new RuntimeException("Transaction not found with ID: " + id);
        }
        transactionRepository.deleteById(id);
    }

    // ============================================================
    // ✅ GET BY USER
    // ============================================================
    @Override
    public List<Transaction> getTransactionsByUserId(Long userId) {
        return transactionRepository.findByUser_IdAndIsDeletedFalse(userId);
    }
}
