package com.example.PersonalFinanceManager.controller;

import com.example.PersonalFinanceManager.dto.RoleDTO;
import com.example.PersonalFinanceManager.model.Role;
import com.example.PersonalFinanceManager.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    // 🟢 Lấy tất cả roles
    @GetMapping
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        List<RoleDTO> roles = roleService.getAllRoles()
                .stream()
                .map(RoleDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(roles);
    }

    // 🟢 Lấy role theo ID
    @GetMapping("/{id}")
    public ResponseEntity<RoleDTO> getRoleById(@PathVariable Long id) {
        Role role = roleService.getRoleById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        return ResponseEntity.ok(new RoleDTO(role));
    }

    // 🟢 Tạo mới role
    @PostMapping
    public ResponseEntity<RoleDTO> createRole(@RequestBody Role role) {
        Role saved = roleService.createRole(role);
        return ResponseEntity.ok(new RoleDTO(saved));
    }

    // 🟢 Cập nhật role
    @PutMapping("/{id}")
    public ResponseEntity<RoleDTO> updateRole(@PathVariable Long id, @RequestBody Role role) {
        Role updated = roleService.updateRole(id, role);
        return ResponseEntity.ok(new RoleDTO(updated));
    }

    // 🟢 Xóa role
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}
