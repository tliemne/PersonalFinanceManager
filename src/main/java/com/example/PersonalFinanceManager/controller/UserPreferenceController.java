package com.example.PersonalFinanceManager.controller;

import com.example.PersonalFinanceManager.dto.UserPreferenceDTO;
import com.example.PersonalFinanceManager.model.User;
import com.example.PersonalFinanceManager.model.UserPreference;
import com.example.PersonalFinanceManager.service.UserPreferenceService;
import com.example.PersonalFinanceManager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user-preferences")
public class UserPreferenceController {

    @Autowired
    private UserPreferenceService userPreferenceService;

    @Autowired
    private UserService userService;

    // 🟢 Lấy tất cả
    @GetMapping
    public ResponseEntity<List<UserPreferenceDTO>> getAllUserPreferences() {
        List<UserPreferenceDTO> result = userPreferenceService.getAllUserPreferences()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    // 🟢 Lấy theo ID
    @GetMapping("/{id}")
    public ResponseEntity<UserPreferenceDTO> getUserPreferenceById(@PathVariable Long id) {
        UserPreference up = userPreferenceService.getUserPreferenceById(id)
                .orElseThrow(() -> new RuntimeException("UserPreference not found"));
        return ResponseEntity.ok(convertToDTO(up));
    }

    // 🟢 Tạo mới
    @PostMapping
    public ResponseEntity<UserPreferenceDTO> createUserPreference(@RequestBody UserPreferenceDTO dto) {
        User user = userService.getUserById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserPreference up = new UserPreference();
        up.setUser(user);
        up.setPreferredCurrency(dto.getPreferredCurrency());
        up.setTheme(UserPreference.Theme.valueOf(dto.getTheme()));
        up.setNotificationEnabled(dto.getNotificationEnabled());

        UserPreference saved = userPreferenceService.createUserPreference(up);
        return ResponseEntity.ok(convertToDTO(saved));
    }

    // 🟢 Cập nhật
    @PutMapping("/{id}")
    public ResponseEntity<UserPreferenceDTO> updateUserPreference(@PathVariable Long id, @RequestBody UserPreferenceDTO dto) {
        UserPreference up = new UserPreference();
        up.setPreferredCurrency(dto.getPreferredCurrency());
        up.setTheme(UserPreference.Theme.valueOf(dto.getTheme()));
        up.setNotificationEnabled(dto.getNotificationEnabled());

        UserPreference updated = userPreferenceService.updateUserPreference(id, up);
        return ResponseEntity.ok(convertToDTO(updated));
    }

    // 🟢 Xóa
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserPreference(@PathVariable Long id) {
        userPreferenceService.deleteUserPreference(id);
        return ResponseEntity.noContent().build();
    }

    // 🧠 Convert sang DTO để tránh vòng lặp vô hạn
    private UserPreferenceDTO convertToDTO(UserPreference up) {
        return new UserPreferenceDTO(
                up.getId(),
                up.getUser().getId(),
                up.getPreferredCurrency(),
                up.getTheme().name(),
                up.getNotificationEnabled()
        );
    }
}
