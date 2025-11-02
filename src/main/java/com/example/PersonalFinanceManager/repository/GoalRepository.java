package com.example.PersonalFinanceManager.repository;

import com.example.PersonalFinanceManager.model.Goal;
import com.example.PersonalFinanceManager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {

    // 🔹 Lấy tất cả mục tiêu của 1 user
    List<Goal> findByUser_Id(Long userId);

    // 🔹 Lấy mục tiêu chưa bị xóa mềm của user
    List<Goal> findByUser_IdAndIsDeletedFalse(Long userId);

    // 🔹 Lấy mục tiêu đã bị xóa mềm của user
    List<Goal> findByUser_IdAndIsDeletedTrue(Long userId);

    // 🔹 Lấy tất cả mục tiêu chưa bị xóa (mọi user)
    List<Goal> findByIsDeletedFalse();

    // 🔹 Lấy tất cả mục tiêu đã bị xóa (mọi user)
    List<Goal> findByIsDeletedTrue();
    List<Goal> findByUserAndIsDeletedFalse(User user);
}
