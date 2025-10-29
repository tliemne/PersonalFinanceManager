package com.example.PersonalFinanceManager.controller;

import com.example.PersonalFinanceManager.dto.BudgetDTO;
import com.example.PersonalFinanceManager.dto.TransactionDTO;
import com.example.PersonalFinanceManager.model.*;
import com.example.PersonalFinanceManager.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    private final Long userId = 1L; // ⚡ Tạm cố định user_id (có thể thay bằng SecurityContext sau này)

    @Autowired private UserService userService;
    @Autowired private TransactionService transactionService;
    @Autowired private BudgetService budgetService;
    @Autowired private CategoryService categoryService;
    @Autowired private GoalService goalService;
    @Autowired private AccountService accountService;

    // 🏠 DASHBOARD CHÍNH
    @GetMapping("/dashboard")
    public String userDashboard(Model model) {
        List<Transaction> transactions = transactionService.getTransactionsByUserId(userId);
        if (transactions == null) transactions = new ArrayList<>();

        double totalIncome = calculateTotal(transactions, Transaction.TransactionType.INCOME);
        double totalExpense = calculateTotal(transactions, Transaction.TransactionType.EXPENSE);
        double balance = totalIncome - totalExpense;

        List<TransactionDTO> recentTransactions = transactions.stream()
                .filter(t -> t.getTransactionDate() != null)
                .sorted(Comparator.comparing(Transaction::getTransactionDate).reversed())
                .limit(5)
                .map(this::toDTO)
                .collect(Collectors.toList());

        model.addAttribute("totalIncome", totalIncome);
        model.addAttribute("totalExpense", totalExpense);
        model.addAttribute("balance", balance);
        model.addAttribute("transactions", recentTransactions);

        setViewAttributes(model, "Bảng điều khiển", "Tổng quan", "dashboard/dashboard", "dashboard");
        return "layout/base";
    }

    // 💵 QUẢN LÝ GIAO DỊCH
    @GetMapping("/dashboard/transaction")
    public String transactionPage(Model model) {
        List<TransactionDTO> transactions = transactionService.getTransactionsByUserId(userId)
                .stream().map(this::toDTO).collect(Collectors.toList());

        List<TransactionDTO> deletedTransactions = transactionService.getDeletedTransactions().stream()
                .filter(t -> t.getUser() != null && Objects.equals(t.getUser().getId(), userId))
                .map(this::toDTO)
                .collect(Collectors.toList());

        model.addAttribute("transactions", transactions);
        model.addAttribute("deletedTransactions", deletedTransactions);
        model.addAttribute("accounts", accountService.getAllAccounts());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("transactionDto", new TransactionDTO());

        setViewAttributes(model, "Giao dịch", "Quản lý giao dịch", "dashboard/transaction", "transaction");
        return "layout/base";
    }

    // ➕ LƯU / CẬP NHẬT GIAO DỊCH
    @PostMapping("/dashboard/transaction/save")
    public String saveTransaction(@ModelAttribute TransactionDTO dto) {
        if (dto == null) return "redirect:/dashboard/transaction";

        Transaction transaction = (dto.getId() != null)
                ? transactionService.getTransactionById(dto.getId()).orElse(new Transaction())
                : new Transaction();

        transaction.setUser(userService.getUserById(userId).orElse(null));
        transaction.setAccount(accountService.getAccountById(dto.getAccountId()).orElse(null));
        transaction.setCategory(categoryService.getCategoryById(dto.getCategoryId()).orElse(null));
        transaction.setAmount(Optional.ofNullable(dto.getAmount()).orElse(0.0));
        transaction.setTransactionType(Transaction.TransactionType.valueOf(dto.getTransactionType()));
        transaction.setStatus(Transaction.TransactionStatus.valueOf(dto.getStatus()));
        transaction.setDescription(dto.getDescription());
        transaction.setTransactionDate(Optional.ofNullable(dto.getTransactionDate()).orElse(LocalDate.now()));
        transaction.setIsDeleted(Optional.ofNullable(dto.getIsDeleted()).orElse(false));

        if (dto.getId() != null)
            transactionService.updateTransaction(dto.getId(), transaction);
        else
            transactionService.createTransaction(transaction);

        return "redirect:/dashboard/transaction";
    }

    // ❌ XÓA MỀM / ♻️ KHÔI PHỤC / 🗑️ XÓA VĨNH VIỄN
    @GetMapping("/dashboard/transaction/delete/{id}")
    public String deleteTransaction(@PathVariable Long id) {
        if (id != null) transactionService.deleteTransaction(id);
        return "redirect:/dashboard/transaction";
    }

    @GetMapping("/dashboard/transaction/restore/{id}")
    public String restoreTransaction(@PathVariable Long id) {
        if (id != null) transactionService.restoreTransaction(id);
        return "redirect:/dashboard/transaction";
    }

    @GetMapping("/dashboard/transaction/permanent-delete/{id}")
    public String permanentDeleteTransaction(@PathVariable Long id) {
        if (id != null) transactionService.deleteById(id);
        return "redirect:/dashboard/transaction";
    }

    // 📊 BÁO CÁO
    @GetMapping("/dashboard/report")
    public String reportPage(Model model) {
        List<Transaction> transactions = transactionService.getTransactionsByUserId(userId);
        if (transactions == null) transactions = new ArrayList<>();

        double totalIncome = calculateTotal(transactions, Transaction.TransactionType.INCOME);
        double totalExpense = calculateTotal(transactions, Transaction.TransactionType.EXPENSE);

        model.addAttribute("transactions", transactions.stream().map(this::toDTO).collect(Collectors.toList()));
        model.addAttribute("totalIncome", totalIncome);
        model.addAttribute("totalExpense", totalExpense);
        model.addAttribute("balance", totalIncome - totalExpense);

        setViewAttributes(model, "Báo cáo tài chính", "Báo cáo", "dashboard/report", "report");
        return "layout/base";
    }

    // 💰 NGÂN SÁCH
    @GetMapping("/dashboard/budget")
    public String budgetPage(Model model) {
        List<BudgetDTO> budgets = budgetService.getAllBudgets().stream()
                .map(this::mapToBudgetProgress)
                .collect(Collectors.toList());

        model.addAttribute("budgets", budgets);
        model.addAttribute("categories", categoryService.getAllCategories());
        setViewAttributes(model, "Ngân sách", "Quản lý ngân sách", "dashboard/budget", "budget");
        return "layout/base";
    }

    @PostMapping("/dashboard/budget/save")
    public String saveBudget(@ModelAttribute BudgetDTO dto) {
        if (dto == null) return "redirect:/dashboard/budget";

        if (dto.getId() != null) {
            budgetService.updateBudget(dto.getId(), dto);
        } else {
            Budget budget = new Budget();
            budget.setUser(userService.getUserById(userId).orElse(null));
            budget.setCategory(categoryService.getCategoryById(dto.getCategoryId()).orElse(null));
            budget.setAmountLimit(Optional.ofNullable(dto.getAmountLimit()).orElse(0.0));
            budget.setUsedAmount(Optional.ofNullable(dto.getUsedAmount()).orElse(0.0));
            budget.setStartDate(dto.getStartDate());
            budget.setEndDate(dto.getEndDate());
            budget.setIsDeleted(false);
            budgetService.createBudget(budget);
        }
        return "redirect:/dashboard/budget";
    }

    @GetMapping("/dashboard/budget/delete/{id}")
    public String deleteBudget(@PathVariable Long id) {
        if (id != null) budgetService.deleteBudget(id);
        return "redirect:/dashboard/budget";
    }

    // 🗂️ DANH MỤC
    @GetMapping("/dashboard/category")
    public String categoryPage(Model model) {
        model.addAttribute("incomeCategories", categoryService.getIncomeCategories());
        model.addAttribute("expenseCategories", categoryService.getExpenseCategories());
        setViewAttributes(model, "Danh mục", "Danh mục của bạn", "dashboard/category", "category");
        return "layout/base";
    }

    // 🎯 MỤC TIÊU
    @GetMapping("/dashboard/goal")
    public String goalPage(Model model) {
        model.addAttribute("goals", goalService.getGoalsByUserId(userId));
        setViewAttributes(model, "Mục tiêu tài chính", "Mục tiêu", "dashboard/goal", "goal");
        return "layout/base";
    }

    // ⚙️ CÀI ĐẶT
    @GetMapping("/dashboard/settings")
    public String settingsPage(Model model) {
        User user = userService.getUserById(userId).orElse(null);
        UserPreference preferences = userService.getUserPreferenceByUserId(userId);
        model.addAttribute("user", user);
        model.addAttribute("preferences", preferences);
        setViewAttributes(model, "Cài đặt tài khoản", "Cài đặt", "dashboard/settings", "settings");
        return "layout/base";
    }

    // 📈 PHÂN TÍCH NHANH
    @GetMapping("/dashboard/analysis")
    public String quickAnalysis(Model model) {
        List<Transaction> transactions = transactionService.getTransactionsByUserId(userId);
        if (transactions == null) transactions = new ArrayList<>();

        double totalIncome = calculateTotal(transactions, Transaction.TransactionType.INCOME);
        double totalExpense = calculateTotal(transactions, Transaction.TransactionType.EXPENSE);

        List<CategorySpending> topCategories = transactions.stream()
                .filter(t -> t.getTransactionType() == Transaction.TransactionType.EXPENSE)
                .filter(t -> t.getCategory() != null)
                .collect(Collectors.groupingBy(t -> t.getCategory().getName(), Collectors.summingDouble(Transaction::getAmount)))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(3)
                .map(e -> new CategorySpending(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        List<BudgetDTO> nearLimitBudgets = budgetService.getAllBudgets().stream()
                .map(this::mapToBudgetProgress)
                .filter(dto -> dto != null
                        && dto.getAmountLimit() != null
                        && dto.getAmountLimit() > 0
                        && dto.getProgress() != null
                        && !Double.isNaN(dto.getProgress())
                        && !Double.isInfinite(dto.getProgress()))
                .filter(dto -> dto.getProgress() >= 80.0)
                .peek(dto -> {
                    // ép lại cho chắc, tránh NaN lọt qua
                    if (Double.isNaN(dto.getActualProgress()) || Double.isInfinite(dto.getActualProgress())) {
                        dto.setActualProgress(0.0);
                    }
                    if (Double.isNaN(dto.getProgress()) || Double.isInfinite(dto.getProgress())) {
                        dto.setProgress(0.0);
                    }
                })
                .collect(Collectors.toList());

        double todayExpense = transactions.stream()
                .filter(t -> t.getTransactionType() == Transaction.TransactionType.EXPENSE)
                .filter(t -> LocalDate.now().equals(t.getTransactionDate()))
                .mapToDouble(Transaction::getAmount)
                .sum();

        double avgExpenseLast7Days = transactions.stream()
                .filter(t -> t.getTransactionType() == Transaction.TransactionType.EXPENSE)
                .filter(t -> !t.getTransactionDate().isBefore(LocalDate.now().minusDays(7)))
                .mapToDouble(Transaction::getAmount)
                .average().orElse(0.0);

        model.addAttribute("totalIncome", totalIncome);
        model.addAttribute("totalExpense", totalExpense);
        model.addAttribute("topCategories", topCategories);
        model.addAttribute("nearLimitBudgets", nearLimitBudgets);
        model.addAttribute("spendingAdvice", getSpendingAdvice(todayExpense, avgExpenseLast7Days, totalIncome, totalExpense));

        setViewAttributes(model, "Phân tích nhanh", "Phân tích nhanh", "dashboard/analysis", "analysis");
        return "layout/base";
    }

    // 🔹 Helper Methods
    private double calculateTotal(List<Transaction> list, Transaction.TransactionType type) {
        return list.stream()
                .filter(t -> t.getTransactionType() == type)
                .mapToDouble(t -> Optional.ofNullable(t.getAmount()).orElse(0.0))
                .sum();
    }

    private void setViewAttributes(Model model, String title, String pageTitle, String content, String active) {
        model.addAttribute("title", title);
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("content", content);
        model.addAttribute("activePage", active);
    }

    private TransactionDTO toDTO(Transaction t) {
        if (t == null || t.getUser() == null) return null;
        return new TransactionDTO(
                t.getId(),
                t.getUser().getId(),
                t.getAccount() != null ? t.getAccount().getId() : null,
                t.getCategory() != null ? t.getCategory().getId() : null,
                t.getCategory() != null ? t.getCategory().getName() : "Khác",
                Optional.ofNullable(t.getAmount()).orElse(0.0),
                t.getTransactionType().name(),
                t.getStatus().name(),
                Optional.ofNullable(t.getIsDeleted()).orElse(false),
                t.getDescription(),
                t.getTransactionDate(),
                t.getCreatedAt(),
                t.getUpdatedAt()
        );
    }

    private BudgetDTO mapToBudgetProgress(Budget b) {
        if (b == null) return new BudgetDTO();
        BudgetDTO dto = new BudgetDTO();

        dto.setId(b.getId());
        dto.setCategoryName(b.getCategory() != null ? b.getCategory().getName() : "Không xác định");
        dto.setAmountLimit(Optional.ofNullable(b.getAmountLimit()).orElse(0.0));
        dto.setStartDate(b.getStartDate());
        dto.setEndDate(b.getEndDate());

        List<Transaction> transactions = Optional.ofNullable(
                transactionService.getTransactionsByUserId(userId)
        ).orElse(new ArrayList<>());

        double totalSpent = transactions.stream()
                .filter(t -> t.getTransactionType() == Transaction.TransactionType.EXPENSE)
                .filter(t -> t.getCategory() != null && b.getCategory() != null)
                .filter(t -> Objects.equals(t.getCategory().getId(), b.getCategory().getId()))
                .filter(t -> t.getTransactionDate() != null)
                .filter(t -> {
                    if (b.getStartDate() == null && b.getEndDate() == null) return true;
                    if (b.getStartDate() == null) return !t.getTransactionDate().isAfter(b.getEndDate());
                    if (b.getEndDate() == null) return !t.getTransactionDate().isBefore(b.getStartDate());
                    return !t.getTransactionDate().isBefore(b.getStartDate()) &&
                            !t.getTransactionDate().isAfter(b.getEndDate());
                })
                .mapToDouble(t -> Optional.ofNullable(t.getAmount()).orElse(0.0))
                .sum();

        dto.setUsedAmount(totalSpent);

        // ✅ Chặn lỗi Infinity và NaN hoàn toàn
        double limit = Optional.ofNullable(dto.getAmountLimit()).filter(l -> l > 0).orElse(1.0);
        double actualProgress = 0.0;

        if (limit > 0.0) {
            actualProgress = (totalSpent / limit) * 100.0;
        }

        if (Double.isNaN(actualProgress) || Double.isInfinite(actualProgress) || actualProgress < 0) {
            actualProgress = 0.0;
        }

        // ✅ Nếu vượt quá 100% thì gán 100 nhưng không crash
        dto.setActualProgress(actualProgress);
        dto.setProgress(Math.min(actualProgress, 100.0));

        return dto;
    }


    private String getSpendingAdvice(double todayExpense, double avgExpenseLast7Days, double totalIncome, double totalExpense) {
        // ✅ Nếu chưa có dữ liệu
        if (totalIncome <= 0 && totalExpense <= 0) {
            return "📊 Chưa có dữ liệu chi tiêu để phân tích.";
        }

        // ✅ Nếu chi tiêu vượt thu nhập
        if (totalExpense > totalIncome) {
            double over = ((totalExpense - totalIncome) / totalIncome) * 100;
            return "⚠️ Cảnh báo: Chi tiêu của bạn đang vượt thu nhập khoảng " + String.format("%.0f%%", over);
        }

        // ✅ Nếu chi tiêu dưới 70% thu nhập
        if (totalExpense < totalIncome * 0.7) {
            return "🎉 Bạn đang chi tiêu rất tiết kiệm so với thu nhập!";
        }

        // ✅ Nếu chi tiêu gần bằng thu nhập
        if (totalExpense >= totalIncome * 0.9) {
            return "⚠️ Lưu ý: Chi tiêu của bạn đang tiến gần mức thu nhập!";
        }

        // ✅ Bình thường
        return "📊 Chi tiêu của bạn đang cân đối so với thu nhập.";
    }


    private record CategorySpending(String name, double totalAmount) {}
}
