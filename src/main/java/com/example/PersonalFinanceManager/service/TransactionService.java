package com.example.PersonalFinanceManager.service;

import com.example.PersonalFinanceManager.model.Transaction;
import com.example.PersonalFinanceManager.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService implements TransactionServiceImpl {
    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public Transaction createTransaction(Transaction transaction) {
        return null;
    }

    @Override
    public Optional<Transaction> getTransactionById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return List.of();
    }

    @Override
    public Transaction updateTransaction(Long id, Transaction transaction) {
        return null;
    }

    @Override
    public void deleteTransaction(Long id) {

    }
}
