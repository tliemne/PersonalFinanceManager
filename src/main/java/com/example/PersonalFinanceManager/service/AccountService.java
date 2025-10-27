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
        return accountRepository.save(account);
    }

    @Override
    public Optional<Account> getAccountById(Long id) {
        return accountRepository.findById(id);
    }

    @Override
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @Override
    public Account updateAccount(Long id, Account account) {
        return accountRepository.findById(id).map(existing->
        {
            existing.setId(account.getId());
            existing.setUser(account.getUser());
            existing.setName(account.getName());
            existing.setBalance(account.getBalance());
            existing.setCurrency(account.getCurrency());
            return accountRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("Account not found"));
    }

    @Override
    public void deleteAccount(Long id) {
        if(accountRepository.existsById(id))
        {
            throw new RuntimeException("Account not found");
        }
        accountRepository.deleteById(id);
    }
}

