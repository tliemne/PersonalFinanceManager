package com.example.PersonalFinanceManager.repository;

import com.example.PersonalFinanceManager.model.Budget;
import com.example.PersonalFinanceManager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    List<Budget> findByUserId(Long userId);
    List<Budget> findByCategory_IdAndUser_IdAndIsDeletedFalse(Long categoryId, Long userId);
    List<Budget> findByUserAndIsDeletedFalse(User user);
}
