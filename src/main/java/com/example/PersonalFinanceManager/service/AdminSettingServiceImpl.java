package com.example.PersonalFinanceManager.service;

import com.example.PersonalFinanceManager.model.AdminSetting;

import java.util.List;
import java.util.Optional;

public interface AdminSettingServiceImpl {
    AdminSetting createAdminSetting(AdminSetting adminSetting);
    Optional<AdminSetting> getAdminSettingById(Long id);
    List<AdminSetting> getAllAdminSettings();
    AdminSetting updateAdminSetting(Long id, AdminSetting adminSetting);
    void deleteAdminSetting(Long id);
}
