package com.example.PersonalFinanceManager.service;

import com.example.PersonalFinanceManager.dto.UserProfileDTO;
import com.example.PersonalFinanceManager.model.User;
import com.example.PersonalFinanceManager.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@Service
public class UserProfileService {

    @Autowired private UserRepository userRepository;
    @Autowired private AccountRepository accountRepository;
    @Autowired private TransactionRepository transactionRepository;
    @Autowired private GoalRepository goalRepository;

    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/avatars/";

    // 🔹 Lấy thông tin hồ sơ người dùng
    public UserProfileDTO getUserProfile(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();

        UserProfileDTO dto = new UserProfileDTO();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setCreatedAt(user.getCreatedAt());

        // Thống kê tổng
        dto.setTotalAccounts(accountRepository.countByUserId(userId));
        dto.setTotalTransactions(transactionRepository.countByUserId(userId));
        dto.setTotalGoals(goalRepository.countByUserId(userId));

        return dto;
    }

    public User updateProfile(Long userId, String fullName, String email, MultipartFile avatarFile) throws IOException {
        User user = userRepository.findById(userId).orElseThrow();

        user.setFullName(fullName);
        user.setEmail(email);

        // 🖼 Nếu có ảnh mới
        if (avatarFile != null && !avatarFile.isEmpty()) {
            String fileName = "avatar_" + userId + "_" + System.currentTimeMillis() + ".png";
            Path uploadPath = Paths.get(UPLOAD_DIR);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Files.copy(avatarFile.getInputStream(), uploadPath.resolve(fileName),
                    StandardCopyOption.REPLACE_EXISTING);

            user.setAvatarUrl("/uploads/avatars/" + fileName);
        }

        return userRepository.save(user); // 🟢 TRẢ VỀ USER MỚI
    }

}
