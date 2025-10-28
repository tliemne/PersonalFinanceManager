package com.example.PersonalFinanceManager.controller;

import com.example.PersonalFinanceManager.dto.SystemLogDTO;
import com.example.PersonalFinanceManager.model.SystemLog;
import com.example.PersonalFinanceManager.model.User;
import com.example.PersonalFinanceManager.service.SystemLogService;
import com.example.PersonalFinanceManager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/systemlogs")
public class SystemLogController {

    @Autowired
    private SystemLogService systemLogService;

    @Autowired
    private UserService userService;

    // 🟢 1️⃣ Tạo log mới
    @PostMapping
    public ResponseEntity<SystemLogDTO> createSystemLog(@RequestBody SystemLogDTO dto) {
        User user = userService.getUserById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        SystemLog log = new SystemLog();
        log.setUser(user);
        log.setAction(dto.getAction());
        log.setDescription(dto.getDescription());

        SystemLog created = systemLogService.createSystemLog(log);
        return ResponseEntity.ok(convertToDTO(created));
    }

    // 🟡 2️⃣ Lấy tất cả logs
    @GetMapping
    public ResponseEntity<List<SystemLogDTO>> getAllSystemLogs() {
        List<SystemLogDTO> list = systemLogService.getAllSystemLogs()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    // 🔵 3️⃣ Lấy log theo ID
    @GetMapping("/{id}")
    public ResponseEntity<SystemLogDTO> getSystemLogById(@PathVariable Long id) {
        SystemLog log = systemLogService.getSystemLogById(id)
                .orElseThrow(() -> new RuntimeException("Log not found"));
        return ResponseEntity.ok(convertToDTO(log));
    }

    // 🟠 4️⃣ Cập nhật log
    @PutMapping("/{id}")
    public ResponseEntity<SystemLogDTO> updateSystemLog(@PathVariable Long id, @RequestBody SystemLogDTO dto) {
        SystemLog log = new SystemLog();
        log.setAction(dto.getAction());
        log.setDescription(dto.getDescription());

        SystemLog updated = systemLogService.updateSystemLog(id, log);
        return ResponseEntity.ok(convertToDTO(updated));
    }

    // 🔴 5️⃣ Xóa log
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSystemLog(@PathVariable Long id) {
        systemLogService.deleteSystemLog(id);
        return ResponseEntity.ok("Deleted successfully");
    }

    // 🔁 Chuyển Entity → DTO
    private SystemLogDTO convertToDTO(SystemLog log) {
        Long userId = log.getUser() != null ? log.getUser().getId() : null;
        return new SystemLogDTO(
                log.getId(),
                userId,
                log.getAction(),
                log.getDescription(),
                log.getCreatedAt()
        );
    }
}
