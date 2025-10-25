package com.example.PersonalFinanceManager.service;

import com.example.PersonalFinanceManager.model.RecurringTransaction;

import java.util.List;
import java.util.Optional;

public interface RecurringTransactionServiceImpl {
    RecurringTransaction createRecurringTransaction(RecurringTransaction rt);
    Optional<RecurringTransaction> getRecurringTransactionById(Long id);
    List<RecurringTransaction> getAllRecurringTransactions();
    RecurringTransaction updateRecurringTransaction(Long id, RecurringTransaction rt);
    void deleteRecurringTransaction(Long id);
}
