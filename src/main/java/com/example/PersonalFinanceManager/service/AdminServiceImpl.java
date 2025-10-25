package com.example.PersonalFinanceManager.service;

import com.example.PersonalFinanceManager.model.AdminSetting;

import java.util.List;
import java.util.Optional;

public interface AdminServiceImpl {
    List<AdminSetting> getAll();
    Optional<AdminSetting> getById(Long id);
    List<AdminSetting> getByUserId(Long userId);
    AdminSetting save(AdminSetting a);
    AdminSetting update(Long id, AdminSetting a);
    void delete(Long id);
}
