package com.example.PersonalFinanceManager.repository;

import com.example.PersonalFinanceManager.model.Transaction;
import com.example.PersonalFinanceManager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // 🔹 Lấy toàn bộ giao dịch của user (kể cả đã xoá)
    List<Transaction> findByUser_Id(Long userId);

    // 🔹 Lấy toàn bộ giao dịch của user chưa bị xoá
    List<Transaction> findByUser_IdAndIsDeletedFalse(Long userId);

    // 🔹 Lấy giao dịch theo tài khoản (kể cả đã xoá)
    List<Transaction> findByAccount_Id(Long accountId);

    // 🔹 Lấy toàn bộ giao dịch chưa bị xoá / đã xoá
    List<Transaction> findByIsDeletedFalse();
    List<Transaction> findByIsDeletedTrue();

    // ⚠️ remove duplicate method names: use findByAccount_Id above
    // If you still want a short name, you can keep this, but it's redundant:
    // List<Transaction> findByAccountId(Long accountId);

    List<Transaction> findByAccount_IdAndIsDeletedFalse(Long accountId);

    // (Tùy chọn) Nếu cần lọc thêm theo user
    List<Transaction> findByAccount_UserId(Long userId);
    List<Transaction> findByUser_IdAndCategory_IdAndIsDeletedFalse(Long userId, Long categoryId);

    // ===== Fix chính: dùng tên field transactionDate =====
    // Lấy giao dịch của user trong khoảng ngày (dùng LocalDate)
    List<Transaction> findByUserAndTransactionDateBetween(User user, LocalDate start, LocalDate end);

    // Thường hữu dụng: chỉ lấy giao dịch chưa xóa trong khoảng
    List<Transaction> findByUserAndTransactionDateBetweenAndIsDeletedFalse(User user, LocalDate start, LocalDate end);
    List<Transaction> findByUserAndIsDeletedFalse(User user);
    long countByUserId(Long userId);
}
