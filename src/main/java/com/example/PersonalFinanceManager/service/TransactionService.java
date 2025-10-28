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
        return transactionRepository.save(transaction);
    }

    @Override
    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @Override
    public Transaction updateTransaction(Long id, Transaction transaction) {
        return transactionRepository.findById(id).map(e->{
            e.setAmount(e.getAmount());
            e.setTransactionType(e.getTransactionType());
            e.setDescription(e.getDescription());
            e.setTransactionDate(e.getTransactionDate());
            e.setStatus(e.getStatus());
            e.setIsDeleted(e.getIsDeleted());
            return  transactionRepository.save(e);
        }).orElseThrow(()-> new RuntimeException("not found"));
    }
    @Override
    public void deleteTransaction(Long id) {
        transactionRepository.findById(id).ifPresent(e->{
            e.setIsDeleted(true);
            transactionRepository.save(e);
        });
    }
}
