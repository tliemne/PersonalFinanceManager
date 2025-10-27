package com.example.PersonalFinanceManager.controller;

import com.example.PersonalFinanceManager.dto.AccountDTO;
import com.example.PersonalFinanceManager.model.Account;
import com.example.PersonalFinanceManager.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "*")
public class AccountController {

    @Autowired
    private AccountService accountService;

    // 🟢 Lấy tất cả tài khoản
    @GetMapping
    public ResponseEntity<List<AccountDTO>> getAllAccounts() {
        List<AccountDTO> dtos = accountService.getAllAccounts()
                .stream()
                .map(AccountDTO::new) // map Account → AccountDTO (chứa UserDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // 🟢 Lấy account theo ID
    @GetMapping("/{id}")
    public ResponseEntity<AccountDTO> getAccountById(@PathVariable Long id) {
        Optional<Account> account = accountService.getAccountById(id);
        return account.map(a -> ResponseEntity.ok(new AccountDTO(a)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 🟢 Tạo account mới
    @PostMapping
    public ResponseEntity<AccountDTO> createAccount(@RequestBody Account account) {
        try {
            Account created = accountService.createAccount(account);
            return ResponseEntity.ok(new AccountDTO(created));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 🟢 Cập nhật account
    @PutMapping("/{id}")
    public ResponseEntity<AccountDTO> updateAccount(@PathVariable Long id, @RequestBody Account account) {
        try {
            Account updated = accountService.updateAccount(id, account);
            return ResponseEntity.ok(new AccountDTO(updated));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 🟢 Xóa account
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        try {
            accountService.deleteAccount(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
