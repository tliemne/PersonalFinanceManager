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
        if (goal.getIsDeleted() == null) goal.setIsDeleted(false);
        if (goal.getCurrentAmount() == null) goal.setCurrentAmount(0.0);
        return goalRepository.save(goal);
    }

    @Override
    public Optional<Goal> getGoalById(Long id) {
        return goalRepository.findById(id)
                .filter(g -> !Boolean.TRUE.equals(g.getIsDeleted()));
    }

    @Override
    public List<Goal> getAllGoals() {
        return goalRepository.findByIsDeletedFalse();
    }

    @Override
    public Goal updateGoal(Long id, Goal goal) {
        return goalRepository.findById(id).map(existing -> {
            existing.setName(goal.getName());
            existing.setTargetAmount(goal.getTargetAmount());
            existing.setCurrentAmount(goal.getCurrentAmount() != null ? goal.getCurrentAmount() : 0.0);
            existing.setDeadline(goal.getDeadline());
            existing.setPriority(goal.getPriority());
            return goalRepository.save(existing);
        }).orElseThrow(() -> new IllegalArgumentException("Goal not found with id: " + id));
    }

    @Override
    public void deleteGoal(Long id) {
        goalRepository.findById(id).ifPresent(goal -> {
            goal.setIsDeleted(true);
            goalRepository.save(goal);
        });
    }

    @Override
    public void restoreGoal(Long id) {
        goalRepository.findById(id).ifPresent(goal -> {
            goal.setIsDeleted(false);
            goalRepository.save(goal);
        });
    }

    @Override
    public void permanentDeleteGoal(Long id) {
        goalRepository.deleteById(id);
    }

    @Override
    public List<Goal> getGoalsByUserId(Long userId) {
        return goalRepository.findByUser_IdAndIsDeletedFalse(userId);
    }

    @Override
    public List<Goal> getDeletedGoalsByUserId(Long userId) {
        return goalRepository.findByUser_IdAndIsDeletedTrue(userId);
    }

    @Override
    public Optional<Goal> getGoalIncludeDeleted(Long id) {
        return goalRepository.findById(id);
    }
}
