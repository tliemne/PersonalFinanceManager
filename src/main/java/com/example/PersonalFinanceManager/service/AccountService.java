package com.example.PersonalFinanceManager.service;

import com.example.PersonalFinanceManager.model.Account;
import com.example.PersonalFinanceManager.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService implements AccountServiceImpl {
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public Account createAccount(Account account) {
        return null;
    }

    @Override
    public Optional<Account> getAccountById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Account> getAllAccounts() {
        return List.of();
    }

    @Override
    public Account updateAccount(Long id, Account account) {
        return null;
    }

    @Override
    public void deleteAccount(Long id) {

    }
}

