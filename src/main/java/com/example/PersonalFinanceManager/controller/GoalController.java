package com.example.PersonalFinanceManager.controller;

import com.example.PersonalFinanceManager.dto.GoalDTO;
import com.example.PersonalFinanceManager.model.Goal;
import com.example.PersonalFinanceManager.model.User;
import com.example.PersonalFinanceManager.service.GoalService;
import com.example.PersonalFinanceManager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    @Autowired
    private GoalService goalService;

    @Autowired
    private UserService userService;
    @GetMapping
    public ResponseEntity<List<GoalDTO>> getAllGoals() {
        List<GoalDTO> result = goalService.getAllGoals().stream()
                .filter(g -> !g.getIsDeleted()) // chỉ lấy goal chưa bị xóa
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
    @GetMapping("/{id}")
    public ResponseEntity<GoalDTO> getGoalById(@PathVariable Long id) {
        Goal goal = goalService.getGoalById(id)
                .orElseThrow(() -> new RuntimeException("Goal not found"));
        return ResponseEntity.ok(convertToDTO(goal));
    }
    @PostMapping
    public ResponseEntity<GoalDTO> createGoal(@RequestBody GoalDTO dto) {
        User user = userService.getUserById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Goal goal = new Goal();
        goal.setUser(user);
        goal.setName(dto.getName());
        goal.setTargetAmount(dto.getTargetAmount());
        goal.setCurrentAmount(dto.getCurrentAmount());
        goal.setDeadline(dto.getDeadline());
        goal.setPriority(Goal.Priority.valueOf(dto.getPriority()));
        goal.setIsDeleted(false);

        Goal saved = goalService.createGoal(goal);
        return ResponseEntity.ok(convertToDTO(saved));
    }
    @PutMapping("/{id}")
    public ResponseEntity<GoalDTO> updateGoal(@PathVariable Long id, @RequestBody GoalDTO dto) {
        Goal goal = new Goal();
        goal.setName(dto.getName());
        goal.setTargetAmount(dto.getTargetAmount());
        goal.setCurrentAmount(dto.getCurrentAmount());
        goal.setDeadline(dto.getDeadline());
        goal.setPriority(Goal.Priority.valueOf(dto.getPriority()));
        goal.setIsDeleted(dto.getIsDeleted());

        Goal updated = goalService.updateGoal(id, goal);
        return ResponseEntity.ok(convertToDTO(updated));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(@PathVariable Long id) {
        goalService.deleteGoal(id);
        return ResponseEntity.noContent().build();
    }
    private GoalDTO convertToDTO(Goal goal) {
        return new GoalDTO(
                goal.getId(),
                goal.getUser().getId(),
                goal.getName(),
                goal.getTargetAmount(),
                goal.getCurrentAmount(),
                goal.getPriority().name(),
                goal.getIsDeleted(),
                goal.getDeadline(),
                goal.getCreatedAt(),
                goal.getUpdatedAt()
        );
    }
}
