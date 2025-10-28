package com.example.PersonalFinanceManager.controller;

import com.example.PersonalFinanceManager.dto.TransactionDTO;
import com.example.PersonalFinanceManager.model.*;
import com.example.PersonalFinanceManager.service.TransactionService;
import com.example.PersonalFinanceManager.repository.UserRepository;
import com.example.PersonalFinanceManager.repository.AccountRepository;
import com.example.PersonalFinanceManager.repository.CategoryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    // 🟢 Create Transaction
    @PostMapping
    public ResponseEntity<?> createTransaction(@RequestBody TransactionDTO dto) {
        Optional<User> user = userRepository.findById(dto.getUserId());
        Optional<Account> account = accountRepository.findById(dto.getAccountId());
        Optional<Category> category = categoryRepository.findById(dto.getCategoryId());

        if (user.isEmpty() || account.isEmpty() || category.isEmpty()) {
            return ResponseEntity.badRequest().body("❌ Invalid userId, accountId, or categoryId");
        }

        Transaction t = new Transaction();
        t.setUser(user.get());
        t.setAccount(account.get());
        t.setCategory(category.get());
        t.setAmount(dto.getAmount());
        t.setTransactionType(Transaction.TransactionType.valueOf(dto.getTransactionType()));
        t.setStatus(Transaction.TransactionStatus.valueOf(dto.getStatus()));
        t.setIsDeleted(dto.getIsDeleted());
        t.setDescription(dto.getDescription());
        t.setTransactionDate(dto.getTransactionDate());

        Transaction saved = transactionService.createTransaction(t);
        return ResponseEntity.ok(toDTO(saved));
    }

    // 🟡 Get All
    @GetMapping
    public ResponseEntity<List<TransactionDTO>> getAllTransactions() {
        List<TransactionDTO> list = transactionService.getAllTransactions()
                .stream().map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    // 🔵 Get by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getTransactionById(@PathVariable Long id) {
        Optional<Transaction> transaction = transactionService.getTransactionById(id);
        return transaction.map(value -> ResponseEntity.ok(toDTO(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 🟠 Update
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTransaction(@PathVariable Long id, @RequestBody TransactionDTO dto) {
        Optional<Transaction> transactionOpt = transactionService.getTransactionById(id);
        if (transactionOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Transaction t = transactionOpt.get();
        t.setAmount(dto.getAmount());
        t.setTransactionType(Transaction.TransactionType.valueOf(dto.getTransactionType()));
        t.setStatus(Transaction.TransactionStatus.valueOf(dto.getStatus()));
        t.setDescription(dto.getDescription());
        t.setTransactionDate(dto.getTransactionDate());
        t.setIsDeleted(dto.getIsDeleted());

        Transaction updated = transactionService.createTransaction(t); // save lại
        return ResponseEntity.ok(toDTO(updated));
    }

    // 🔴 Soft Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.ok("✅ Transaction moved to trash (isDeleted = true)");
    }

    // ✳️ Helper: Convert Entity -> DTO
    private TransactionDTO toDTO(Transaction t) {
        return new TransactionDTO(
                t.getId(),
                t.getUser().getId(),
                t.getAccount().getId(),
                t.getCategory().getId(),
                t.getAmount(),
                t.getTransactionType().name(),
                t.getStatus().name(),
                t.getIsDeleted(),
                t.getDescription(),
                t.getTransactionDate(),
                t.getCreatedAt(),
                t.getUpdatedAt()
        );
    }
}
