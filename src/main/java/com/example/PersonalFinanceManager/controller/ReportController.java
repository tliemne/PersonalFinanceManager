package com.example.PersonalFinanceManager.controller;

import com.example.PersonalFinanceManager.model.User;
import com.example.PersonalFinanceManager.repository.UserRepository;
import com.example.PersonalFinanceManager.service.ReportService;
import com.example.PersonalFinanceManager.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Map;

@Controller
@RequestMapping("/dashboard")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper; // ✅ thêm dòng này
    @Autowired
    private UserService userService;
    private final Long userId = 1L;
    @ModelAttribute
    public void addUserToModel(Model model) {
        userService.getUserById(userId).ifPresent(user -> model.addAttribute("user", user));
    }
    // 🔹 1️⃣ Báo cáo tổng hợp
    @GetMapping("/report")
    public String showReport(Model model) {
        User user = userRepository.findById(1L).orElseThrow();

        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();

        Map<String, Object> reportData = reportService.getReportData(user, startDate, endDate);

        model.addAttribute("reportData", reportData);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("activePage", "report");
        model.addAttribute("content", "dashboard/report");
        model.addAttribute("title", "Báo cáo tổng hợp");
        return "layout/base";
    }

    // 🔹 2️⃣ Xu hướng chi tiêu
    @GetMapping("/trends")
    public String showTrends(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Model model) {

        User user = userRepository.findById(1L).orElseThrow();

        LocalDate start = (startDate != null && !startDate.isEmpty())
                ? LocalDate.parse(startDate)
                : LocalDate.now().minusMonths(6);
        LocalDate end = (endDate != null && !endDate.isEmpty())
                ? LocalDate.parse(endDate)
                : LocalDate.now();

        Map<String, Object> trends = reportService.getSpendingTrends(user, start, end);

        model.addAttribute("trends", trends);
        model.addAttribute("startDate", start);
        model.addAttribute("endDate", end);
        model.addAttribute("activePage", "trends");
        model.addAttribute("content", "dashboard/trends");
        model.addAttribute("title", "Xu hướng chi tiêu");
        return "layout/base";
    }

    // 🔹 3️⃣ So sánh tháng / năm
    @GetMapping("/compare")
    public String showCompare(Model model) throws Exception {
        User user = userRepository.findById(1L).orElseThrow();

        Map<String, Object> compareData = reportService.getCompareReport(user);
        String compareDataJson = objectMapper.writeValueAsString(compareData); // ✅ chuyển sang JSON string

        model.addAttribute("compareDataJson", compareDataJson); // ✅ thêm JSON thật
        model.addAttribute("activePage", "compare");
        model.addAttribute("content", "dashboard/compare");
        model.addAttribute("title", "So sánh chi tiêu");
        return "layout/base";
    }
}
