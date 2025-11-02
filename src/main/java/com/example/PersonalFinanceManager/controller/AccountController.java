package com.example.PersonalFinanceManager.controller;

import com.example.PersonalFinanceManager.model.Account;
import com.example.PersonalFinanceManager.model.User;
import com.example.PersonalFinanceManager.service.AccountService;
import com.example.PersonalFinanceManager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/dashboard/accounts")
public class AccountController {
    @ModelAttribute
    public void addUserToModel(Model model) {
        userService.getUserById(userId).ifPresent(user -> model.addAttribute("user", user));
    }
    // 🧍‍♂️ User mặc định (tạm thời khi chưa có đăng nhập)
    private final Long userId = 1L;

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserService userService;

    // 🟢 Hiển thị danh sách tài khoản của user
    @GetMapping
    public String listAccounts(Model model,
                               @RequestParam(value = "success", required = false) String success,
                               @RequestParam(value = "error", required = false) String error) {
        try {
            // Lấy user hiện tại
            User user = userService.getUserById(userId)
                    .orElseThrow(() -> new IllegalStateException("Không tìm thấy user mặc định"));

//             ✅ (Tùy chọn) Cập nhật lại số dư thực tế theo lịch sử giao dịch
             accountService.recalculateAllBalances(userId);

            // Chỉ lấy các tài khoản thuộc user này
            List<Account> accounts = accountService.getAccountsByUserId(userId);

            // ✅ Tính tổng số dư
            double totalBalance = accounts.stream()
                    .mapToDouble(Account::getBalance)
                    .sum();

            // ✅ Truyền dữ liệu cho layout base
            model.addAttribute("accounts", accounts);
            model.addAttribute("newAccount", new Account());
            model.addAttribute("success", success);
            model.addAttribute("error", error);
            model.addAttribute("activePage", "accounts");
            model.addAttribute("totalBalance", totalBalance); // 👈 fix lỗi null ₫

            // ✅ Dùng layout/base.html và nạp fragment dashboard/accounts
            model.addAttribute("title", "Quản lý tài khoản");
            model.addAttribute("content", "dashboard/accounts");

            return "layout/base"; // ⬅️ load layout có Tailwind
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi tải danh sách tài khoản!");
            model.addAttribute("activePage", "accounts");
            model.addAttribute("title", "Lỗi");
            model.addAttribute("content", "dashboard/accounts");
            return "layout/base";
        }
    }

    // 🟢 Thêm tài khoản mới
    @PostMapping("/add")
    public String addAccount(@ModelAttribute("newAccount") Account account) {
        try {
            User user = userService.getUserById(userId)
                    .orElseThrow(() -> new IllegalStateException("Không tìm thấy user mặc định"));

            account.setUser(user);
            accountService.createAccount(account);
            return "redirect:/dashboard/accounts?success=Thêm ví thành công!";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/dashboard/accounts?error=Lỗi khi thêm ví!";
        }
    }

    // 🟢 Cập nhật tài khoản
    @PostMapping("/update/{id}")
    public String updateAccount(@PathVariable Long id,
                                @ModelAttribute Account account) {
        try {
            accountService.updateAccount(id, account);
            return "redirect:/dashboard/accounts?success=Cập nhật ví thành công!";
        } catch (RuntimeException e) {
            e.printStackTrace();
            return "redirect:/dashboard/accounts?error=" + e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/dashboard/accounts?error=Lỗi không xác định khi cập nhật!";
        }
    }

    // 🟢 Xóa tài khoản
    @GetMapping("/delete/{id}")
    public String deleteAccount(@PathVariable Long id) {
        try {
            accountService.deleteAccount(id);
            return "redirect:/dashboard/accounts?success=Đã xóa ví thành công!";
        } catch (RuntimeException e) {
            e.printStackTrace();
            return "redirect:/dashboard/accounts?error=" + e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/dashboard/accounts?error=Lỗi khi xóa ví!";
        }
    }
}
