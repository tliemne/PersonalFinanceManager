package com.example.PersonalFinanceManager.service;

import com.example.PersonalFinanceManager.model.Account;
import com.example.PersonalFinanceManager.model.Transaction;
import com.example.PersonalFinanceManager.repository.AccountRepository;
import com.example.PersonalFinanceManager.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService implements AccountServiceImpl {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public Account createAccount(Account account) {
        if (account.getBalance() == null) {
            account.setBalance(0.0);
        }
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
        return accountRepository.findById(id).map(existing -> {
            if (account.getName() != null && !account.getName().isBlank()) {
                existing.setName(account.getName());
            }
            if (account.getBalance() != null) {
                existing.setBalance(account.getBalance());
            }
            if (account.getCurrency() != null && !account.getCurrency().isBlank()) {
                existing.setCurrency(account.getCurrency());
            }
            return accountRepository.save(existing);
        }).orElseThrow(() -> new IllegalArgumentException("Account not found with ID: " + id));
    }

    @Override
    public void deleteAccount(Long id) {
        if (!accountRepository.existsById(id)) {
            throw new RuntimeException("Account not found");
        }
        accountRepository.deleteById(id);
    }

    @Override
    public List<Account> getAccountsByUserId(Long userId) {
        // ✅ Dùng đúng method trong repository
        return accountRepository.findByUserId(userId);
    }

    /**
     /**
     /**
     * 🔄 Tái tính số dư cho tất cả tài khoản của user dựa trên lịch sử giao dịch
     *  -> Dùng khi muốn đồng bộ số dư thực tế (phòng khi dữ liệu bị lệch)
     */
    public void recalculateAllBalances(Long userId) {
        List<Account> accounts = accountRepository.findByUserId(userId);

        for (Account account : accounts) {
            // Bắt đầu từ 0 (tính lại toàn bộ)
            double newBalance = 0.0;

            // Lấy tất cả giao dịch của tài khoản này (chưa bị xóa)
            List<Transaction> transactions = transactionRepository.findByAccount_IdAndIsDeletedFalse(account.getId());

            for (Transaction tx : transactions) {
                if (tx.getTransactionType() == Transaction.TransactionType.INCOME) {
                    newBalance += tx.getAmount();
                } else if (tx.getTransactionType() == Transaction.TransactionType.EXPENSE) {
                    newBalance -= tx.getAmount();
                }
            }

            account.setBalance(newBalance);
            accountRepository.save(account);
        }
    }


}
