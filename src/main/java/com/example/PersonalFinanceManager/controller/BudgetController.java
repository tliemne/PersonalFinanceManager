package com.example.PersonalFinanceManager.controller;

import com.example.PersonalFinanceManager.dto.BudgetDTO;
import com.example.PersonalFinanceManager.model.Budget;
import com.example.PersonalFinanceManager.model.Category;
import com.example.PersonalFinanceManager.model.User;
import com.example.PersonalFinanceManager.service.BudgetService;
import com.example.PersonalFinanceManager.repository.CategoryRepository;
import com.example.PersonalFinanceManager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    // ===================== CREATE =====================
    @PostMapping
    public ResponseEntity<BudgetDTO> createBudget(@RequestBody BudgetDTO budgetDTO) {
        Optional<User> userOpt = userRepository.findById(budgetDTO.getUserId());
        Optional<Category> catOpt = categoryRepository.findById(budgetDTO.getCategoryId());

        if (userOpt.isEmpty() || catOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Budget budget = new Budget();
        budget.setUser(userOpt.get());
        budget.setCategory(catOpt.get());
        budget.setAmountLimit(budgetDTO.getAmountLimit());
        budget.setUsedAmount(budgetDTO.getUsedAmount() != null ? budgetDTO.getUsedAmount() : 0.0);
        budget.setStartDate(budgetDTO.getStartDate());
        budget.setEndDate(budgetDTO.getEndDate());
        budget.setIsDeleted(budgetDTO.getIsDeleted() != null ? budgetDTO.getIsDeleted() : false);

        Budget saved = budgetService.createBudget(budget);
        return ResponseEntity.ok(convertToDTO(saved));
    }

    // ===================== READ (ALL) =====================
    @GetMapping
    public List<BudgetDTO> getAllBudgets() {
        return budgetService.getAllBudgets()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ===================== READ (ONE) =====================
    @GetMapping("/{id}")
    public ResponseEntity<BudgetDTO> getBudgetById(@PathVariable Long id) {
        Optional<Budget> opt = budgetService.getBudgetById(id);
        return opt.map(budget -> ResponseEntity.ok(convertToDTO(budget)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ===================== UPDATE =====================
    @PutMapping("/{id}")
    public ResponseEntity<BudgetDTO> updateBudget(@PathVariable Long id, @RequestBody BudgetDTO budgetDTO) {
        Optional<Budget> existingOpt = budgetService.getBudgetById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Budget existing = existingOpt.get();
        existing.setAmountLimit(budgetDTO.getAmountLimit());
        existing.setUsedAmount(budgetDTO.getUsedAmount());
        existing.setStartDate(budgetDTO.getStartDate());
        existing.setEndDate(budgetDTO.getEndDate());
        existing.setIsDeleted(budgetDTO.getIsDeleted());

        Budget updated = budgetService.updateBudget(id, budgetDTO);
        return ResponseEntity.ok(convertToDTO(updated));
    }

    // ===================== DELETE =====================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long id) {
        budgetService.deleteBudget(id);
        return ResponseEntity.noContent().build();
    }

    // ===================== DTO CONVERTER =====================
    private BudgetDTO convertToDTO(Budget b) {
        BudgetDTO dto = new BudgetDTO();
        dto.setId(b.getId());

        if (b.getUser() != null) {
            dto.setUserId(b.getUser().getId());
            dto.setUserName(b.getUser().getUsername());
        }

        if (b.getCategory() != null) {
            dto.setCategoryId(b.getCategory().getId());
            dto.setCategoryName(b.getCategory().getName());
        } else {
            dto.setCategoryName("Không xác định");
        }

        dto.setAmountLimit(b.getAmountLimit());
        dto.setUsedAmount(b.getUsedAmount());
        dto.setStartDate(b.getStartDate());
        dto.setEndDate(b.getEndDate());
        dto.setIsDeleted(b.getIsDeleted());

        double progress = (b.getAmountLimit() != null && b.getAmountLimit() > 0)
                ? (b.getUsedAmount() / b.getAmountLimit()) * 100 : 0;
        dto.setProgress(Math.min(progress, 100.0));

        String status = (b.getEndDate() != null && b.getEndDate().isBefore(LocalDate.now()))
                ? "Đã hết hạn" : "Còn hiệu lực";
        dto.setStatus(status);

        return dto;
    }
}
