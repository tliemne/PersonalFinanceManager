package com.example.PersonalFinanceManager.service;

import com.example.PersonalFinanceManager.model.Account;

import java.util.List;
import java.util.Optional;

public interface AccountServiceImpl {
    Account createAccount(Account account);
    Optional<Account> getAccountById(Long id);
    List<Account> getAllAccounts();
    Account updateAccount(Long id, Account account);
    void deleteAccount(Long id);
}
