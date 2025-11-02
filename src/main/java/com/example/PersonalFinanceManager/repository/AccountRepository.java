package com.example.PersonalFinanceManager.repository;

import com.example.PersonalFinanceManager.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByUserId(Long userId);
    List<Account> findByUser_Id(Long userId);
    long countByUserId(Long userId);
}
