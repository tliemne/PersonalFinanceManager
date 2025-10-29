package com.example.PersonalFinanceManager.service;

import com.example.PersonalFinanceManager.model.Goal;
import com.example.PersonalFinanceManager.repository.GoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GoalService implements GoalServiceImpl {

    @Autowired
    private GoalRepository goalRepository;

    @Override
    public Goal createGoal(Goal goal) {
        return goalRepository.save(goal);
    }

    @Override
    public Optional<Goal> getGoalById(Long id) {
        return goalRepository.findById(id);
    }

    @Override
    public List<Goal> getAllGoals() {
        return goalRepository.findAll();
    }

    @Override
    public Goal updateGoal(Long id, Goal goal) {
        return goalRepository.findById(id).map(e -> {
            e.setName(goal.getName());
            e.setTargetAmount(goal.getTargetAmount());
            e.setCurrentAmount(goal.getCurrentAmount());
            e.setDeadline(goal.getDeadline());
            e.setPriority(goal.getPriority());
            e.setIsDeleted(goal.getIsDeleted());
            return goalRepository.save(e);
        }).orElseThrow(() -> new RuntimeException("Goal not found"));
    }

    @Override
    public void deleteGoal(Long id) {
        goalRepository.findById(id).ifPresent(e -> {
            e.setIsDeleted(true);
            goalRepository.save(e);
        });
    }

    // 🆕 Thêm hàm này để DashboardController hoạt động đúng
    public List<Goal> getGoalsByUserId(Long userId) {
        return goalRepository.findByUser_Id(userId);
    }
}
