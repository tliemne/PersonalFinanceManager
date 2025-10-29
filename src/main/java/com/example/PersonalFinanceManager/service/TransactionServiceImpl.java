package com.example.PersonalFinanceManager.service;

import com.example.PersonalFinanceManager.model.Transaction;
import java.util.List;
import java.util.Optional;

public interface TransactionServiceImpl {
    Transaction createTransaction(Transaction transaction);
    Optional<Transaction> getTransactionById(Long id);
    List<Transaction> getAllTransactions();
    Transaction updateTransaction(Long id, Transaction transaction);
    void deleteTransaction(Long id);
    List<Transaction> getTransactionsByUserId(Long userId);
    void softDeleteTransaction(Long id);
    void deleteById(Long id);
    void restoreTransaction(Long id);

}
