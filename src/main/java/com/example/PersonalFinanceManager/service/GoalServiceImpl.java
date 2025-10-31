package com.example.PersonalFinanceManager.service;

import com.example.PersonalFinanceManager.model.Goal;

import java.util.List;
import java.util.Optional;

public interface GoalServiceImpl {

    Goal createGoal(Goal goal);

    Optional<Goal> getGoalById(Long id);

    List<Goal> getAllGoals();

    Goal updateGoal(Long id, Goal goal);

    void deleteGoal(Long id);

    void restoreGoal(Long id);

    void permanentDeleteGoal(Long id);

    List<Goal> getGoalsByUserId(Long userId);

    List<Goal> getDeletedGoalsByUserId(Long userId);

    Optional<Goal> getGoalIncludeDeleted(Long id);
}
