package com.example.PersonalFinanceManager.service;

import com.example.PersonalFinanceManager.model.Transaction;
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

    @Override
    @Transactional
    public Transaction createTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @Override
    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    @Override
    public List<Transaction> getAllTransactions() {
        // 🔹 chỉ lấy những giao dịch chưa bị xóa (isDeleted = false)
        return transactionRepository.findByIsDeletedFalse();
    }

    @Override
    @Transactional
    public Transaction updateTransaction(Long id, Transaction transaction) {
        return transactionRepository.findById(id).map(e -> {
            e.setAmount(transaction.getAmount());
            e.setTransactionType(transaction.getTransactionType());
            e.setDescription(transaction.getDescription());
            e.setTransactionDate(transaction.getTransactionDate());
            e.setStatus(transaction.getStatus());
            e.setIsDeleted(transaction.getIsDeleted());
            e.setCategory(transaction.getCategory());
            e.setUser(transaction.getUser());
            return transactionRepository.save(e);
        }).orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    @Override
    @Transactional
    public void deleteTransaction(Long id) {
        transactionRepository.findById(id).ifPresent(e -> {
            e.setIsDeleted(true); // 🔹 Soft delete
            transactionRepository.save(e);
        });
    }

    // 🔹 Thêm tiện ích lấy theo user ID
    public List<Transaction> getTransactionsByUserId(Long userId) {
        return transactionRepository.findByUser_IdAndIsDeletedFalse(userId);
    }

    @Transactional
    public void softDeleteTransaction(Long id) {
        Transaction t = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        t.setIsDeleted(true);
        transactionRepository.save(t);
    }

    @Transactional
    public void restoreTransaction(Long id) {
        Transaction t = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        t.setIsDeleted(false);
        transactionRepository.save(t);
    }

    /**
     * 🔴 Xóa vĩnh viễn (hard delete)
     * Dùng cẩn thận — không thể khôi phục
     */
    @Transactional
    public void deleteById(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new RuntimeException("Transaction not found with ID: " + id);
        }
        transactionRepository.deleteById(id);
    }
}
