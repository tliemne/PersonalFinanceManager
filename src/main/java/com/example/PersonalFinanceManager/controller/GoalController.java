package com.example.PersonalFinanceManager.controller;

import com.example.PersonalFinanceManager.dto.GoalDTO;
import com.example.PersonalFinanceManager.model.Goal;
import com.example.PersonalFinanceManager.model.User;
import com.example.PersonalFinanceManager.service.GoalService;
import com.example.PersonalFinanceManager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/dashboard")
public class GoalController {
    @ModelAttribute
    public void addUserToModel(Model model) {
        userService.getUserById(userId).ifPresent(user -> model.addAttribute("user", user));
    }
    @Autowired
    private GoalService goalService;

    @Autowired
    private UserService userService;

    private final Long userId = 1L; // user demo

    @GetMapping("/goals")
    public String listGoals(@RequestParam(value = "edit", required = false) Long editId, Model model) {
        List<GoalDTO> goals = goalService.getGoalsByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        List<GoalDTO> deletedGoals = goalService.getDeletedGoalsByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        GoalDTO goalForm = (editId != null)
                ? convertToDTO(goalService.getGoalIncludeDeleted(editId).orElse(new Goal()))
                : new GoalDTO();

        model.addAttribute("goal", goalForm);
        model.addAttribute("goals", goals);
        model.addAttribute("deletedGoals", deletedGoals);
        model.addAttribute("title", "Quản lý mục tiêu");
        model.addAttribute("content", "dashboard/goals");

        // ✅ Quan trọng: để sidebar mở đúng nhóm và highlight đúng link
        model.addAttribute("activePage", "goals");

        return "layout/base";
    }

    @PostMapping("/goals/save")
    public String saveGoal(@ModelAttribute("goal") GoalDTO dto) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Goal goal = new Goal();
        goal.setUser(user);
        goal.setName(dto.getName());
        goal.setTargetAmount(dto.getTargetAmount());
        goal.setCurrentAmount(dto.getCurrentAmount() != null ? dto.getCurrentAmount() : 0.0);
        goal.setDeadline(dto.getDeadline());
        goal.setPriority(Goal.Priority.valueOf(dto.getPriority() != null ? dto.getPriority() : "MEDIUM"));
        goal.setIsDeleted(false);

        if (dto.getId() != null && dto.getId() > 0) {
            goalService.updateGoal(dto.getId(), goal);
        } else {
            goalService.createGoal(goal);
        }

        return "redirect:/dashboard/goals";
    }

    @GetMapping("/goals/delete/{id}")
    public String deleteGoal(@PathVariable Long id) {
        goalService.deleteGoal(id);
        return "redirect:/dashboard/goals";
    }

    @GetMapping("/goals/restore/{id}")
    public String restoreGoal(@PathVariable Long id) {
        goalService.restoreGoal(id);
        return "redirect:/dashboard/goals";
    }

    @GetMapping("/goals/permanent-delete/{id}")
    public String permanentDeleteGoal(@PathVariable Long id) {
        goalService.permanentDeleteGoal(id);
        return "redirect:/dashboard/goals";
    }

    @GetMapping("/goals/get/{id}")
    @ResponseBody
    public GoalDTO getGoalData(@PathVariable Long id) {
        return goalService.getGoalIncludeDeleted(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Goal not found"));
    }

    private GoalDTO convertToDTO(Goal goal) {
        if (goal == null) return new GoalDTO();

        double progress = 0.0;
        if (goal.getTargetAmount() != null && goal.getTargetAmount() > 0) {
            progress = (goal.getCurrentAmount() / goal.getTargetAmount()) * 100.0;
        }

        String status;
        if (progress >= 100) {
            status = "Hoàn thành";
        } else if (goal.getDeadline() != null && goal.getDeadline().isBefore(LocalDate.now())) {
            status = "Quá hạn";
        } else {
            status = "Đang thực hiện";
        }

        return new GoalDTO(
                goal.getId(),
                goal.getUser() != null ? goal.getUser().getId() : null,
                goal.getName(),
                goal.getTargetAmount(),
                goal.getCurrentAmount(),
                goal.getPriority() != null ? goal.getPriority().name() : "MEDIUM",
                goal.getIsDeleted(),
                goal.getDeadline(),
                goal.getCreatedAt(),
                goal.getUpdatedAt(),
                progress,
                status
        );
    }
}
