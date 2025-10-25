package com.example.PersonalFinanceManager.service;

import com.example.PersonalFinanceManager.model.Budget;

import java.util.List;
import java.util.Optional;

public interface BudgetServiceImpl {
    Budget createBudget(Budget budget);
    Optional<Budget> getBudgetById(Long id);
    List<Budget> getAllBudgets();
    Budget updateBudget(Long id, Budget budget);
    void deleteBudget(Long id);
}
