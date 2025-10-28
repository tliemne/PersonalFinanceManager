package com.example.PersonalFinanceManager.controller;

import com.example.PersonalFinanceManager.dto.RecurringTransactionDTO;
import com.example.PersonalFinanceManager.model.Category;
import com.example.PersonalFinanceManager.model.RecurringTransaction;
import com.example.PersonalFinanceManager.model.User;
import com.example.PersonalFinanceManager.service.CategoryService;
import com.example.PersonalFinanceManager.service.RecurringTransactionService;
import com.example.PersonalFinanceManager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recurring-transactions")
public class RecurringTransactionController {

    @Autowired
    private RecurringTransactionService recurringTransactionService;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    // ✅ Lấy tất cả recurring transactions
    @GetMapping
    public ResponseEntity<List<RecurringTransactionDTO>> getAllRecurringTransactions() {
        List<RecurringTransactionDTO> result = recurringTransactionService.getAllRecurringTransactions()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    // ✅ Lấy theo ID
    @GetMapping("/{id}")
    public ResponseEntity<RecurringTransactionDTO> getRecurringTransactionById(@PathVariable Long id) {
        RecurringTransaction rt = recurringTransactionService.getRecurringTransactionById(id)
                .orElseThrow(() -> new RuntimeException("Recurring Transaction not found"));
        return ResponseEntity.ok(convertToDTO(rt));
    }

    // ✅ Tạo mới
    @PostMapping
    public ResponseEntity<RecurringTransactionDTO> createRecurringTransaction(@RequestBody RecurringTransactionDTO dto) {
        User user = userService.getUserById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Category category = categoryService.getCategoryById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        RecurringTransaction rt = new RecurringTransaction();
        rt.setUser(user);
        rt.setCategory(category);
        rt.setAmount(dto.getAmount());
        rt.setFrequency(dto.getFrequency());
        rt.setNextDate(dto.getNextDate());
        rt.setDescription(dto.getDescription());

        RecurringTransaction saved = recurringTransactionService.createRecurringTransaction(rt);
        return ResponseEntity.ok(convertToDTO(saved));
    }

    // ✅ Cập nhật
    @PutMapping("/{id}")
    public ResponseEntity<RecurringTransactionDTO> updateRecurringTransaction(@PathVariable Long id, @RequestBody RecurringTransactionDTO dto) {
        RecurringTransaction rt = new RecurringTransaction();
        rt.setAmount(dto.getAmount());
        rt.setFrequency(dto.getFrequency());
        rt.setNextDate(dto.getNextDate());
        rt.setDescription(dto.getDescription());

        RecurringTransaction updated = recurringTransactionService.updateRecurringTransaction(id, rt);
        return ResponseEntity.ok(convertToDTO(updated));
    }

    // ✅ Xóa (DELETE thật)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecurringTransaction(@PathVariable Long id) {
        recurringTransactionService.deleteRecurringTransaction(id);
        return ResponseEntity.noContent().build();
    }

    // 🔄 Convert Entity → DTO
    private RecurringTransactionDTO convertToDTO(RecurringTransaction rt) {
        return new RecurringTransactionDTO(
                rt.getId(),
                rt.getUser().getId(),
                rt.getCategory().getId(),
                rt.getAmount(),
                rt.getFrequency(),
                rt.getNextDate(),
                rt.getDescription(),
                rt.getCreatedAt()
        );
    }
}
