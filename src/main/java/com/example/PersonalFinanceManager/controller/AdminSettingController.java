package com.example.PersonalFinanceManager.controller;

import com.example.PersonalFinanceManager.dto.AdminSettingDTO;
import com.example.PersonalFinanceManager.model.AdminSetting;
import com.example.PersonalFinanceManager.service.AdminSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin-settings")
@CrossOrigin(origins = "*")
public class AdminSettingController {

    @Autowired
    private AdminSettingService adminSettingService;


    @GetMapping
    public ResponseEntity<List<AdminSettingDTO>> getAllSettings() {
        List<AdminSettingDTO> dtos = adminSettingService.getAllAdminSettings()
                .stream()
                .map(AdminSettingDTO::new) // map entity → DTO
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }


    @GetMapping("/{id}")
    public ResponseEntity<AdminSettingDTO> getSettingById(@PathVariable Long id) {
        return adminSettingService.getAdminSettingById(id)
                .map(AdminSettingDTO::new)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PostMapping
    public ResponseEntity<AdminSettingDTO> createSetting(@RequestBody AdminSetting adminSetting) {
        try {
            AdminSetting created = adminSettingService.createAdminSetting(adminSetting);
            return ResponseEntity.ok(new AdminSettingDTO(created));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<AdminSettingDTO> updateSetting(@PathVariable Long id,
                                                         @RequestBody AdminSetting adminSetting) {
        try {
            AdminSetting updated = adminSettingService.updateAdminSetting(id, adminSetting);
            return ResponseEntity.ok(new AdminSettingDTO(updated));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSetting(@PathVariable Long id) {
        try {
            adminSettingService.deleteAdminSetting(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
