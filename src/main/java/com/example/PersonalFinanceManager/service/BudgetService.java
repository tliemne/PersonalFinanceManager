package com.example.PersonalFinanceManager.service;

import com.example.PersonalFinanceManager.model.Budget;
import com.example.PersonalFinanceManager.repository.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class BudgetService implements BudgetServiceImpl {
    @Autowired
    private BudgetRepository budgetRepository;
    @Override
    public Budget createBudget(Budget budget) {
        return budgetRepository.save(budget);
    }

    @Override
    public Optional<Budget> getBudgetById(Long id) {
        return budgetRepository.findById(id);
    }

    @Override
    public List<Budget> getAllBudgets() {
        return budgetRepository.findAll();
    }

    @Override
    public Budget updateBudget(Long id, Budget budget) {
        return budgetRepository.findById(id).map(e->{
            e.setAmountLimit(budget.getAmountLimit());
            e.setUsedAmount(budget.getUsedAmount());
            e.setStartDate(budget.getStartDate());
            e.setEndDate(budget.getEndDate());
            e.setIsDeleted(budget.getIsDeleted());
            return budgetRepository.save(e);
        }).orElseThrow(() -> new RuntimeException("Budget not found"));
    }
    @Override
    public void deleteBudget(Long id) {
        budgetRepository.findById(id).ifPresent(user -> {
            user.setIsDeleted(true); // Soft delete
            budgetRepository.save(user);
        });

    }
}
