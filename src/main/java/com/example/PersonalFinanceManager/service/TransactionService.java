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
            existing.setAmount(transaction.getAmount());
            existing.setTransactionType(transaction.getTransactionType());
            existing.setDescription(transaction.getDescription());
            existing.setTransactionDate(transaction.getTransactionDate());
            existing.setStatus(transaction.getStatus());
            existing.setIsDeleted(transaction.getIsDeleted());
            existing.setCategory(transaction.getCategory());
            existing.setUser(transaction.getUser());
            existing.setAccount(transaction.getAccount());
            return transactionRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    @Override
    @Transactional
    public void deleteTransaction(Long id) {
        transactionRepository.findById(id).ifPresent(t -> {
            t.setIsDeleted(true); // 🔹 Soft delete
            transactionRepository.save(t);
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
        t.setIsDeleted(false);
        transactionRepository.save(t);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new RuntimeException("Transaction not found with ID: " + id);
        }
        transactionRepository.deleteById(id);
    }
}
