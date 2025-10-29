package com.example.PersonalFinanceManager.controller;

import com.example.PersonalFinanceManager.dto.BudgetDTO;
import com.example.PersonalFinanceManager.dto.TransactionDTO;
import com.example.PersonalFinanceManager.model.*;
import com.example.PersonalFinanceManager.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    private final Long userId = 1L; // ⚡ Tạm cố định user_id

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private BudgetService budgetService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private GoalService goalService;
    @Autowired
    private AccountService accountService;

    // 🏠 Dashboard chính
    @GetMapping("/dashboard")
    public String userDashboard(Model model) {
        List<Transaction> userTransactions = transactionService.getTransactionsByUserId(userId);

        double totalIncome = userTransactions.stream()
                .filter(t -> t.getTransactionType() == Transaction.TransactionType.INCOME)
                .mapToDouble(Transaction::getAmount)
                .sum();

        double totalExpense = userTransactions.stream()
                .filter(t -> t.getTransactionType() == Transaction.TransactionType.EXPENSE)
                .mapToDouble(Transaction::getAmount)
                .sum();

        double balance = totalIncome - totalExpense;

        // Giao dịch gần đây
        List<TransactionDTO> recentTransactions = userTransactions.stream()
                .sorted(Comparator.comparing(Transaction::getTransactionDate).reversed())
                .limit(5)
                .map(this::toDTO)
                .collect(Collectors.toList());

        model.addAttribute("totalIncome", totalIncome);
        model.addAttribute("totalExpense", totalExpense);
        model.addAttribute("balance", balance);
        model.addAttribute("transactions", recentTransactions);

        model.addAttribute("title", "Bảng điều khiển");
        model.addAttribute("pageTitle", "Tổng quan");
        model.addAttribute("content", "dashboard/dashboard");
        model.addAttribute("activePage", "dashboard");

        return "layout/base";
    }

    // 💵 QUẢN LÝ GIAO DỊCH
    // 💵 QUẢN LÝ GIAO DỊCH
    @GetMapping("/dashboard/transaction")
    public String transactionPage(Model model) {
        List<TransactionDTO> transactions = transactionService.getTransactionsByUserId(userId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        // ✅ Dùng service mới để lấy bản xóa mềm
        List<TransactionDTO> deletedTransactions = transactionService.getDeletedTransactions()
                .stream()
                .filter(t -> t.getUser().getId().equals(userId))
                .map(this::toDTO)
                .collect(Collectors.toList());

        model.addAttribute("transactions", transactions);
        model.addAttribute("deletedTransactions", deletedTransactions);
        model.addAttribute("accounts", accountService.getAllAccounts());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("transactionDto", new TransactionDTO());

        model.addAttribute("title", "Giao dịch");
        model.addAttribute("pageTitle", "Quản lý giao dịch");
        model.addAttribute("content", "dashboard/transaction");
        model.addAttribute("activePage", "transaction");

        return "layout/base";
    }

    // ➕ Lưu hoặc cập nhật giao dịch
    @PostMapping("/dashboard/transaction/save")
    public String saveTransaction(@ModelAttribute TransactionDTO dto) {
        Transaction transaction = new Transaction();

        // Gán thông tin cơ bản
        transaction.setUser(userService.getUserById(userId).orElse(null));
        transaction.setAccount(accountService.getAccountById(dto.getAccountId()).orElse(null));
        transaction.setCategory(categoryService.getCategoryById(dto.getCategoryId()).orElse(null));

        transaction.setAmount(dto.getAmount());
        transaction.setTransactionType(Transaction.TransactionType.valueOf(dto.getTransactionType()));
        transaction.setStatus(Transaction.TransactionStatus.valueOf(dto.getStatus()));
        transaction.setDescription(dto.getDescription());

        // ⚠️ Fix lỗi "transaction_date cannot be null"
        // Nếu người dùng không chọn ngày → tự gán ngày hiện tại
        if (dto.getTransactionDate() != null) {
            transaction.setTransactionDate(dto.getTransactionDate());
        } else {
            transaction.setTransactionDate(LocalDate.now());
        }

        // Tránh null pointer cho isDeleted
        transaction.setIsDeleted(dto.getIsDeleted() != null ? dto.getIsDeleted() : false);

        // ⚡ Nếu có ID → cập nhật, ngược lại → thêm mới
        if (dto.getId() != null) {
            transactionService.updateTransaction(dto.getId(), transaction);
        } else {
            transactionService.createTransaction(transaction);
        }

        return "redirect:/dashboard/transaction";
    }

    // ❌ Xóa mềm
    @GetMapping("/dashboard/transaction/delete/{id}")
    public String deleteTransaction(@PathVariable Long id) {
        // ✅ gọi đúng method trong service
        transactionService.deleteTransaction(id);
        return "redirect:/dashboard/transaction";
    }

    // ♻️ Khôi phục
    @GetMapping("/dashboard/transaction/restore/{id}")
    public String restoreTransaction(@PathVariable Long id) {
        transactionService.restoreTransaction(id);
        return "redirect:/dashboard/transaction";
    }

    // 🗑️ Xóa vĩnh viễn
    @GetMapping("/dashboard/transaction/permanent-delete/{id}")
    public String permanentDeleteTransaction(@PathVariable Long id) {
        transactionService.deleteById(id);
        return "redirect:/dashboard/transaction";
    }

    // 📊 Báo cáo
    @GetMapping("/dashboard/report")
    public String reportPage(Model model) {
        List<Transaction> transactions = transactionService.getTransactionsByUserId(userId);

        double totalIncome = transactions.stream()
                .filter(t -> t.getTransactionType() == Transaction.TransactionType.INCOME)
                .mapToDouble(Transaction::getAmount)
                .sum();

        double totalExpense = transactions.stream()
                .filter(t -> t.getTransactionType() == Transaction.TransactionType.EXPENSE)
                .mapToDouble(Transaction::getAmount)
                .sum();

        double balance = totalIncome - totalExpense;

        List<TransactionDTO> transactionDTOs = transactions.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        model.addAttribute("transactions", transactionDTOs);
        model.addAttribute("totalIncome", totalIncome);
        model.addAttribute("totalExpense", totalExpense);
        model.addAttribute("balance", balance);

        model.addAttribute("title", "Báo cáo tài chính");
        model.addAttribute("pageTitle", "Báo cáo");
        model.addAttribute("content", "dashboard/report");
        model.addAttribute("activePage", "report");
        return "layout/base";
    }
    // 💰 Ngân sách
    @GetMapping("/dashboard/budget") // ✅ Sửa lại đường dẫn
    public String budgetPage(Model model) {
        List<BudgetDTO> budgets = budgetService.getAllBudgets().stream().map(b -> {
            BudgetDTO dto = new BudgetDTO();

            dto.setId(b.getId());
            dto.setUserId(b.getUser() != null ? b.getUser().getId() : null);
            dto.setUserName(b.getUser() != null ? b.getUser().getFullName() : "Không xác định");

            dto.setCategoryId(b.getCategory() != null ? b.getCategory().getId() : null);
            dto.setCategoryName(b.getCategory() != null ? b.getCategory().getName() : "Không xác định");

            dto.setAmountLimit(b.getAmountLimit());
            dto.setUsedAmount(b.getUsedAmount());
            dto.setStartDate(b.getStartDate());
            dto.setEndDate(b.getEndDate());
            dto.setIsDeleted(b.getIsDeleted());

            double progress = (b.getAmountLimit() != null && b.getAmountLimit() > 0)
                    ? (b.getUsedAmount() / b.getAmountLimit()) * 100
                    : 0.0;
            dto.setProgress(progress);

            String status = (b.getEndDate() != null && b.getEndDate().isBefore(LocalDate.now()))
                    ? "Đã hết hạn"
                    : "Còn hiệu lực";
            dto.setStatus(status);

            return dto;
        }).collect(Collectors.toList());

        model.addAttribute("budgets", budgets);
        model.addAttribute("categories", categoryService.getAllCategories());

        model.addAttribute("title", "Ngân sách");
        model.addAttribute("pageTitle", "Quản lý ngân sách");
        model.addAttribute("content", "dashboard/budget");
        model.addAttribute("activePage", "budget");

        return "layout/base";
    }

    // ➕ Lưu hoặc cập nhật ngân sách
    @PostMapping("/dashboard/budget/save") // ✅ Sửa lại đường dẫn
    public String saveBudget(@ModelAttribute BudgetDTO dto) {
        if (dto.getId() != null) {
            budgetService.updateBudget(dto.getId(), dto);
        } else {
            Budget budget = new Budget();
            budget.setUser(userService.getUserById(userId).orElse(null));
            budget.setCategory(categoryService.getCategoryById(dto.getCategoryId()).orElse(null));
            budget.setAmountLimit(dto.getAmountLimit());
            budget.setUsedAmount(dto.getUsedAmount() != null ? dto.getUsedAmount() : 0.0);
            budget.setStartDate(dto.getStartDate());
            budget.setEndDate(dto.getEndDate());
            budget.setIsDeleted(false);
            budgetService.createBudget(budget);
        }

        return "redirect:/dashboard/budget"; // ✅ redirect đồng bộ
    }

    // ❌ Xóa ngân sách
    @GetMapping("/dashboard/budget/delete/{id}") // ✅ Sửa lại đường dẫn
    public String deleteBudget(@PathVariable Long id) {
        budgetService.deleteBudget(id);
        return "redirect:/dashboard/budget";
    }
    // 🗂️ Danh mục
    @GetMapping("/dashboard/category")
    public String categoryPage(Model model) {
        List<Category> incomeCategories = categoryService.getIncomeCategories();
        List<Category> expenseCategories = categoryService.getExpenseCategories();

        model.addAttribute("incomeCategories", incomeCategories);
        model.addAttribute("expenseCategories", expenseCategories);

        model.addAttribute("title", "Danh mục");
        model.addAttribute("pageTitle", "Danh mục của bạn");
        model.addAttribute("content", "dashboard/category");
        model.addAttribute("activePage", "category");

        return "layout/base";
    }
    @GetMapping("/dashboard/goal")
    public String goalPage(Model model) {
        List<Goal> goals = goalService.getGoalsByUserId(userId);

        model.addAttribute("goals", goals);
        model.addAttribute("title", "Mục tiêu tài chính");
        model.addAttribute("pageTitle", "Mục tiêu");
        model.addAttribute("content", "dashboard/goal");
        model.addAttribute("activePage", "goal");

        return "layout/base";
    }
    @GetMapping("/dashboard/settings")
    public String settingsPage(Model model) {
        // Giả sử tạm thời userId cố định là 1L
        User user = userService.getUserById(userId).orElse(null);
        UserPreference preferences = userService.getUserPreferenceByUserId(userId);

        // Gửi dữ liệu qua view
        model.addAttribute("user", user);
        model.addAttribute("preferences", preferences);

        model.addAttribute("title", "Cài đặt tài khoản");
        model.addAttribute("pageTitle", "Cài đặt");
        model.addAttribute("content", "dashboard/settings");
        model.addAttribute("activePage", "settings");

        return "layout/base";
    }
    private TransactionDTO toDTO(Transaction t) {
        String categoryName = (t.getCategory() != null) ? t.getCategory().getName() : "Khác";

        return new TransactionDTO(
                t.getId(),
                t.getUser().getId(),
                t.getAccount().getId(),
                t.getCategory() != null ? t.getCategory().getId() : null, // tránh null pointer
                categoryName, // ✅ thêm trường này để Thymeleaf hiển thị tên danh mục
                t.getAmount(),
                t.getTransactionType().name(),
                t.getStatus().name(),
                t.getIsDeleted(),
                t.getDescription(),
                t.getTransactionDate(),
                t.getCreatedAt(),
                t.getUpdatedAt()
        );
    }
    // 📈 Phân tích nhanh
    @GetMapping("/dashboard/analysis")
    public String quickAnalysis(Model model) {
        List<Transaction> transactions = transactionService.getTransactionsByUserId(userId);

        // Tổng thu / chi
        double totalIncome = transactions.stream()
                .filter(t -> t.getTransactionType() == Transaction.TransactionType.INCOME)
                .mapToDouble(Transaction::getAmount)
                .sum();

        double totalExpense = transactions.stream()
                .filter(t -> t.getTransactionType() == Transaction.TransactionType.EXPENSE)
                .mapToDouble(Transaction::getAmount)
                .sum();

        // Top 3 danh mục chi tiêu
        List<CategorySpending> topCategories = transactions.stream()
                .filter(t -> t.getTransactionType() == Transaction.TransactionType.EXPENSE)
                .collect(Collectors.groupingBy(
                        t -> t.getCategory() != null ? t.getCategory().getName() : "Khác",
                        Collectors.summingDouble(Transaction::getAmount)
                ))
                .entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(3)
                .map(e -> new CategorySpending(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        // Ngân sách gần chạm giới hạn
        List<Budget> nearLimitBudgets = budgetService.getAllBudgets().stream()
                .filter(b -> b.getAmountLimit() > 0)
                .filter(b -> (b.getUsedAmount() / b.getAmountLimit()) > 0.8)
                .collect(Collectors.toList());

        // 🧮 So sánh chi tiêu hôm nay với trung bình 7 ngày gần nhất
        double todayExpense = transactions.stream()
                .filter(t -> t.getTransactionType() == Transaction.TransactionType.EXPENSE)
                .filter(t -> t.getTransactionDate().isEqual(LocalDate.now()))
                .mapToDouble(Transaction::getAmount)
                .sum();

        double avgExpenseLast7Days = transactions.stream()
                .filter(t -> t.getTransactionType() == Transaction.TransactionType.EXPENSE)
                .filter(t -> !t.getTransactionDate().isBefore(LocalDate.now().minusDays(7)))
                .mapToDouble(Transaction::getAmount)
                .average()
                .orElse(0);

        String spendingAdvice;
        if (avgExpenseLast7Days > 0 && todayExpense > avgExpenseLast7Days * 1.2) {
            spendingAdvice = "💡 Chi tiêu của bạn hôm nay cao hơn trung bình tuần trước "
                    + String.format("%.0f%%", (todayExpense / avgExpenseLast7Days - 1) * 100) + ". Hãy cân nhắc lại nhé!";
        } else if (avgExpenseLast7Days > 0 && todayExpense < avgExpenseLast7Days * 0.8) {
            spendingAdvice = "🎉 Bạn đang chi tiêu tiết kiệm hơn bình thường! Tiếp tục phát huy nhé!";
        } else {
            spendingAdvice = "📊 Chi tiêu của bạn hôm nay ở mức ổn định so với tuần trước.";
        }

        // Gửi dữ liệu sang view
        model.addAttribute("totalIncome", totalIncome);
        model.addAttribute("totalExpense", totalExpense);
        model.addAttribute("topCategories", topCategories);
        model.addAttribute("nearLimitBudgets", nearLimitBudgets);
        model.addAttribute("spendingAdvice", spendingAdvice);

        model.addAttribute("title", "Phân tích nhanh");
        model.addAttribute("pageTitle", "Phân tích nhanh");
        model.addAttribute("content", "dashboard/analysis");
        model.addAttribute("activePage", "analysis");

        return "layout/base";
    }

    // ✅ Class phụ nhỏ để hiển thị danh mục chi tiêu
    private static class CategorySpending {
        private final String name;
        private final double totalAmount;

        public CategorySpending(String name, double totalAmount) {
            this.name = name;
            this.totalAmount = totalAmount;
        }

        public String getName() { return name; }
        public double getTotalAmount() { return totalAmount; }
    }
}
