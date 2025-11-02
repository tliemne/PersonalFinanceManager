package com.example.PersonalFinanceManager.controller;

import com.example.PersonalFinanceManager.dto.UserProfileDTO;
import com.example.PersonalFinanceManager.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/dashboard")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    private final Long userId = 1L; // ⚠️ Tạm thời fix user ID

    // 🔹 Hiển thị hồ sơ cá nhân
    @GetMapping("/profile")
    public String showProfile(Model model) {
        UserProfileDTO user = userProfileService.getUserProfile(userId);

        model.addAttribute("user", user);
        model.addAttribute("activePage", "profile");
        model.addAttribute("content", "dashboard/profile");
        model.addAttribute("title", "Hồ sơ cá nhân");

        return "layout/base";
    }

    // 🔹 Cập nhật thông tin cá nhân
    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam String fullName,
                                @RequestParam String email,
                                @RequestParam(required = false) MultipartFile avatarFile,
                                Model model) {
        try {
            // ✅ Cập nhật hồ sơ người dùng
            userProfileService.updateProfile(userId, fullName, email, avatarFile);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi cập nhật hồ sơ: " + e.getMessage());
        }

        // ✅ Tải lại thông tin mới để hiển thị ngay, không cần redirect
        UserProfileDTO user = userProfileService.getUserProfile(userId);
        model.addAttribute("user", user);
        model.addAttribute("success", "Cập nhật thành công!");
        model.addAttribute("activePage", "profile");
        model.addAttribute("content", "dashboard/profile");
        model.addAttribute("title", "Hồ sơ cá nhân");

        return "layout/base";
    }
}
