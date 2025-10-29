package com.example.PersonalFinanceManager.repository;

import com.example.PersonalFinanceManager.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // 🔹 Lấy toàn bộ giao dịch của user (kể cả đã xoá)
    List<Transaction> findByUser_Id(Long userId);

    // 🔹 Lấy toàn bộ giao dịch của user chưa bị xoá
    List<Transaction> findByUser_IdAndIsDeletedFalse(Long userId);

    // 🔹 Lấy giao dịch theo tài khoản (kể cả đã xoá)
    List<Transaction> findByAccount_Id(Long accountId);

    // 🔹 Lấy toàn bộ giao dịch chưa bị xoá
    List<Transaction> findByIsDeletedFalse();
    List<Transaction> findByIsDeletedTrue();
}
