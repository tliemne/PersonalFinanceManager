package com.example.PersonalFinanceManager.service;

import com.example.PersonalFinanceManager.model.Transaction;
import com.example.PersonalFinanceManager.model.User;
import com.example.PersonalFinanceManager.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private TransactionRepository transactionRepository;

    // 🔹 1️⃣ Báo cáo tổng hợp
    public Map<String, Object> getReportData(User user, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> report = new HashMap<>();

        List<Transaction> transactions = transactionRepository
                .findByUserAndTransactionDateBetweenAndIsDeletedFalse(user, startDate, endDate);

        double totalIncome = sumByType(transactions, Transaction.TransactionType.INCOME);
        double totalExpense = sumByType(transactions, Transaction.TransactionType.EXPENSE);

        Map<String, Double> expenseByCategory = transactions.stream()
                .filter(t -> t.getTransactionType() == Transaction.TransactionType.EXPENSE)
                .collect(Collectors.groupingBy(
                        t -> t.getCategory().getName(),
                        Collectors.summingDouble(Transaction::getAmount)
                ));

        report.put("totalIncome", totalIncome);
        report.put("totalExpense", totalExpense);
        report.put("balance", totalIncome - totalExpense);
        report.put("expenseByCategory", expenseByCategory);
        report.put("transactionCount", transactions.size());

        return report;
    }

    // 🔹 2️⃣ Xu hướng chi tiêu
    public Map<String, Object> getSpendingTrends(User user, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> trends = new HashMap<>();

        List<Transaction> transactions = transactionRepository
                .findByUserAndTransactionDateBetweenAndIsDeletedFalse(user, startDate, endDate);

        Map<LocalDate, Double> expenseByDay = transactions.stream()
                .filter(t -> t.getTransactionType() == Transaction.TransactionType.EXPENSE)
                .collect(Collectors.groupingBy(
                        Transaction::getTransactionDate,
                        TreeMap::new,
                        Collectors.summingDouble(Transaction::getAmount)
                ));

        Map<String, Double> expenseByCategory = transactions.stream()
                .filter(t -> t.getTransactionType() == Transaction.TransactionType.EXPENSE)
                .collect(Collectors.groupingBy(
                        t -> t.getCategory().getName(),
                        Collectors.summingDouble(Transaction::getAmount)
                ));

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM/yyyy");
        LocalDate startYear = LocalDate.now().minusMonths(11).withDayOfMonth(1);
        Map<String, Double> expenseByMonth = new LinkedHashMap<>();

        for (int i = 0; i < 12; i++) {
            YearMonth ym = YearMonth.from(startYear.plusMonths(i));
            LocalDate start = ym.atDay(1);
            LocalDate end = ym.atEndOfMonth();

            double monthExpense = sumTransactions(user, start, end, Transaction.TransactionType.EXPENSE);
            expenseByMonth.put(ym.format(fmt), monthExpense);
        }

        trends.put("expenseByDay", expenseByDay);
        trends.put("expenseByCategory", expenseByCategory);
        trends.put("expenseByMonth", expenseByMonth);

        return trends;
    }

    // 🔹 3️⃣ So sánh tháng / năm (bản sửa hoàn chỉnh)
    public Map<String, Object> getCompareReport(User user) {
        Map<String, Object> compare = new HashMap<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM/yyyy");

        // Lấy toàn bộ giao dịch (còn tồn tại)
        List<Transaction> transactions = transactionRepository.findByUserAndIsDeletedFalse(user);
        if (transactions.isEmpty()) {
            compare.put("incomeByMonth", Map.of());
            compare.put("expenseByMonth", Map.of());
            compare.put("incomeCompare", Map.of());
            compare.put("expenseCompare", Map.of());
            return compare;
        }

        // Xác định khoảng tháng có dữ liệu
        LocalDate minDate = transactions.stream()
                .map(Transaction::getTransactionDate)
                .min(LocalDate::compareTo)
                .orElse(LocalDate.now());
        LocalDate maxDate = transactions.stream()
                .map(Transaction::getTransactionDate)
                .max(LocalDate::compareTo)
                .orElse(LocalDate.now());

        YearMonth startYM = YearMonth.from(minDate);
        YearMonth endYM = YearMonth.from(maxDate);

        Map<String, Double> incomeByMonth = new LinkedHashMap<>();
        Map<String, Double> expenseByMonth = new LinkedHashMap<>();

        YearMonth ym = startYM;
        while (!ym.isAfter(endYM)) {
            LocalDate start = ym.atDay(1);
            LocalDate end = ym.atEndOfMonth();

            double totalIncome = transactions.stream()
                    .filter(t -> !t.getTransactionDate().isBefore(start) && !t.getTransactionDate().isAfter(end))
                    .filter(t -> t.getTransactionType() == Transaction.TransactionType.INCOME)
                    .mapToDouble(Transaction::getAmount)
                    .sum();

            double totalExpense = transactions.stream()
                    .filter(t -> !t.getTransactionDate().isBefore(start) && !t.getTransactionDate().isAfter(end))
                    .filter(t -> t.getTransactionType() == Transaction.TransactionType.EXPENSE)
                    .mapToDouble(Transaction::getAmount)
                    .sum();

            incomeByMonth.put(ym.format(fmt), totalIncome);
            expenseByMonth.put(ym.format(fmt), totalExpense);

            ym = ym.plusMonths(1);
        }

        // 🔸 So sánh tất cả các năm có trong DB
        Map<Integer, Double> incomeCompare = transactions.stream()
                .filter(t -> t.getTransactionType() == Transaction.TransactionType.INCOME)
                .collect(Collectors.groupingBy(
                        t -> t.getTransactionDate().getYear(),
                        TreeMap::new,
                        Collectors.summingDouble(Transaction::getAmount)
                ));

        Map<Integer, Double> expenseCompare = transactions.stream()
                .filter(t -> t.getTransactionType() == Transaction.TransactionType.EXPENSE)
                .collect(Collectors.groupingBy(
                        t -> t.getTransactionDate().getYear(),
                        TreeMap::new,
                        Collectors.summingDouble(Transaction::getAmount)
                ));

        compare.put("incomeByMonth", incomeByMonth);
        compare.put("expenseByMonth", expenseByMonth);
        compare.put("incomeCompare", incomeCompare);
        compare.put("expenseCompare", expenseCompare);

        return compare;
    }

    // ==============================
    // 🔸 Các hàm hỗ trợ
    // ==============================

    private double sumTransactions(User user, LocalDate start, LocalDate end, Transaction.TransactionType type) {
        return transactionRepository
                .findByUserAndTransactionDateBetweenAndIsDeletedFalse(user, start, end)
                .stream()
                .filter(t -> t.getTransactionType() == type)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    private double sumByType(List<Transaction> txs, Transaction.TransactionType type) {
        return txs.stream()
                .filter(t -> t.getTransactionType() == type)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }
}
