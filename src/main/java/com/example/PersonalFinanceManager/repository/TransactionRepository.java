package com.example.PersonalFinanceManager.repository;

import com.example.PersonalFinanceManager.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long> {
    List<Transaction> findByUserId(Long userId);
    List<Transaction> findByAccountId(Long accountId);

}
