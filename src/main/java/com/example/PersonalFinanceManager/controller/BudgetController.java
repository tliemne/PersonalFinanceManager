package com.example.PersonalFinanceManager.controller;

import com.example.PersonalFinanceManager.dto.BudgetDTO;
import com.example.PersonalFinanceManager.model.Budget;
import com.example.PersonalFinanceManager.model.Category;
import com.example.PersonalFinanceManager.model.User;
import com.example.PersonalFinanceManager.repository.BudgetRepository;
import com.example.PersonalFinanceManager.repository.CategoryRepository;
import com.example.PersonalFinanceManager.repository.UserRepository;
import com.example.PersonalFinanceManager.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

        budgetDTO.setId(saved.getId());
        return ResponseEntity.ok(budgetDTO);
    }


    @GetMapping
    public List<BudgetDTO> getAllBudgets() {
        return budgetService.getAllBudgets().stream().map(b -> {
            BudgetDTO dto = new BudgetDTO();
            dto.setId(b.getId());
            dto.setUserId(b.getUser().getId());
            dto.setCategoryId(b.getCategory().getId());
            dto.setAmountLimit(b.getAmountLimit());
            dto.setUsedAmount(b.getUsedAmount());
            dto.setStartDate(b.getStartDate());
            dto.setEndDate(b.getEndDate());
            dto.setIsDeleted(b.getIsDeleted());
            return dto;
        }).collect(Collectors.toList());
    }


    @GetMapping("/{id}")
    public ResponseEntity<BudgetDTO> getBudgetById(@PathVariable Long id) {
        Optional<Budget> opt = budgetService.getBudgetById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        Budget b = opt.get();
        BudgetDTO dto = new BudgetDTO();
        dto.setId(b.getId());
        dto.setUserId(b.getUser().getId());
        dto.setCategoryId(b.getCategory().getId());
        dto.setAmountLimit(b.getAmountLimit());
        dto.setUsedAmount(b.getUsedAmount());
        dto.setStartDate(b.getStartDate());
        dto.setEndDate(b.getEndDate());
        dto.setIsDeleted(b.getIsDeleted());

        return ResponseEntity.ok(dto);
    }


    @PutMapping("/{id}")
    public ResponseEntity<BudgetDTO> updateBudget(@PathVariable Long id, @RequestBody BudgetDTO budgetDTO) {
        try {
            Budget budget = new Budget();
            budget.setAmountLimit(budgetDTO.getAmountLimit());
            budget.setUsedAmount(budgetDTO.getUsedAmount());
            budget.setStartDate(budgetDTO.getStartDate());
            budget.setEndDate(budgetDTO.getEndDate());
            budget.setIsDeleted(budgetDTO.getIsDeleted());

            Budget updated = budgetService.updateBudget(id, budget);

            BudgetDTO dto = new BudgetDTO();
            dto.setId(updated.getId());
            dto.setUserId(updated.getUser().getId());
            dto.setCategoryId(updated.getCategory().getId());
            dto.setAmountLimit(updated.getAmountLimit());
            dto.setUsedAmount(updated.getUsedAmount());
            dto.setStartDate(updated.getStartDate());
            dto.setEndDate(updated.getEndDate());
            dto.setIsDeleted(updated.getIsDeleted());

            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long id) {
        budgetService.deleteBudget(id);
        return ResponseEntity.noContent().build();
    }
}
