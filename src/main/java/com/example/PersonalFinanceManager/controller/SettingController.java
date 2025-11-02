package com.example.PersonalFinanceManager.controller;

import com.example.PersonalFinanceManager.model.AdminSetting;
import com.example.PersonalFinanceManager.model.UserPreference;
import com.example.PersonalFinanceManager.model.User;
import com.example.PersonalFinanceManager.service.AdminSettingService;
import com.example.PersonalFinanceManager.service.UserPreferenceService;
import com.example.PersonalFinanceManager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/settings")
public class SettingController {

    @Autowired
    private UserPreferenceService userPreferenceService;

    @Autowired
    private AdminSettingService adminSettingService;

    @Autowired
    private UserService userService;

    // 🟢 Trang hiển thị cài đặt (user + admin)
    @GetMapping
    public String viewSettings(Model model) {
        Long userId = 1L; // Giả định người dùng đang đăng nhập
        User user = userService.getUserById(userId).orElse(null);
        if (user == null) {
            model.addAttribute("error", "Không tìm thấy người dùng");
            return "error";
        }

        UserPreference preference = userPreferenceService.getByUserId(userId);

        // Lấy các admin setting
        AdminSetting siteName = adminSettingService.getByKey("site_name");
        AdminSetting maxTransaction = adminSettingService.getByKey("max_transaction_per_day");

        model.addAttribute("user", user);
        model.addAttribute("preference", preference);
        model.addAttribute("siteName", siteName.getSettingValue());
        model.addAttribute("maxTransaction", maxTransaction.getSettingValue());

        return "dashboard/settings"; // templates/dashboard/settings.html
    }

    // 🟢 Cập nhật user preference (theme, currency, notification)
    @PostMapping("/update/user")
    public String updateUserSettings(@ModelAttribute UserPreference updatedPref) {
        Long userId = 1L; // Giả định user login
        userPreferenceService.updateUserPreference(userId, updatedPref);
        return "redirect:/settings?successUser";
    }

    // 🟢 Cập nhật admin setting (site name, limits, ...)
    @PostMapping("/update/admin")
    public String updateAdminSettings(@RequestParam("siteName") String siteName,
                                      @RequestParam("maxTransaction") String maxTransaction) {

        // Cập nhật tên website
        AdminSetting site = adminSettingService.getByKey("site_name");
        site.setSettingValue(siteName);
        adminSettingService.updateAdminSetting(site.getId(), site);

        // Cập nhật giới hạn giao dịch/ngày
        AdminSetting max = adminSettingService.getByKey("max_transaction_per_day");
        max.setSettingValue(maxTransaction);
        adminSettingService.updateAdminSetting(max.getId(), max);

        return "redirect:/settings?successAdmin";
    }
}
