package com.example.PersonalFinanceManager.controller;

import com.example.PersonalFinanceManager.dto.CategoryDTO;
import com.example.PersonalFinanceManager.model.Category;
import com.example.PersonalFinanceManager.model.User;
import com.example.PersonalFinanceManager.service.CategoryService;
import com.example.PersonalFinanceManager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/dashboard/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    private final Long userId = 1L; // ⚡ Tạm thời hardcode cho test, sau thay bằng SecurityContext
    @ModelAttribute
    public void addUserToModel(Model model) {
        userService.getUserById(userId).ifPresent(user -> model.addAttribute("user", user));
    }
    // 🟢 Hiển thị trang danh mục
    @GetMapping
    public String showCategories(Model model) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ⚡ Dùng DTO thay vì Entity để tránh vòng lặp & dữ liệu thừa
        List<CategoryDTO> categories = categoryService.getCategoriesByUserIdDTO(userId);

        model.addAttribute("user", user);
        model.addAttribute("categories", categories);
        model.addAttribute("newCategory", new CategoryDTO());

        // 🧭 Layout setup
        model.addAttribute("title", "Danh mục của bạn");
        model.addAttribute("pageTitle", "Danh mục của bạn");
        model.addAttribute("content", "dashboard/category");
        model.addAttribute("activePage", "category");

        return "layout/base";
    }

    // 🟢 Thêm danh mục mới
    @PostMapping("/add")
    public String addCategory(@RequestParam String name,
                              @RequestParam("type") Category.CategoryType type,
                              RedirectAttributes redirectAttributes) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Category category = new Category();
        category.setUser(user);
        category.setName(name);
        category.setType(type);

        categoryService.createCategory(category);
        redirectAttributes.addFlashAttribute("success", "✅ Thêm danh mục thành công!");
        return "redirect:/dashboard/category";
    }

    // 🟡 Cập nhật danh mục
    @PostMapping("/update/{id}")
    public String updateCategory(@PathVariable Long id,
                                 @RequestParam String name,
                                 @RequestParam("type") Category.CategoryType type,
                                 RedirectAttributes redirectAttributes) {
        Category category = categoryService.getCategoryById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        category.setName(name);
        category.setType(type);
        categoryService.updateCategory(id, category);

        redirectAttributes.addFlashAttribute("success", "✏️ Cập nhật danh mục thành công!");
        return "redirect:/dashboard/category";
    }

    // 🔴 Xóa danh mục
    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("success", "🗑️ Xóa danh mục thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "⚠️ Không thể xóa danh mục (đang được sử dụng)");
        }
        return "redirect:/dashboard/category";
    }
}
