package com.example.PersonalFinanceManager.service;

import com.example.PersonalFinanceManager.model.AdminSetting;
import com.example.PersonalFinanceManager.repository.AdminSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class AdminSettingService implements AdminSettingServiceImpl {
    @Autowired
    private AdminSettingRepository adminSettingRepository;
    @Override
    public AdminSetting createAdminSetting(AdminSetting adminSetting) {
        return adminSettingRepository.save(adminSetting);
    }

    @Override
    public Optional<AdminSetting> getAdminSettingById(Long id) {
        return adminSettingRepository.findById(id);
    }

    @Override
    public List<AdminSetting> getAllAdminSettings() {
        return adminSettingRepository.findAll();
    }

    @Override
    public AdminSetting updateAdminSetting(Long id, AdminSetting adminSetting) {
        return adminSettingRepository.findById(id).map(existing -> {
            existing.setSettingKey(existing.getSettingKey());
            existing.setSettingValue(existing.getSettingValue());
            return adminSettingRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("AdminSetting not found with id: " + id));

    }

    @Override
    public void deleteAdminSetting(Long id) {
        if(!adminSettingRepository.existsById(id))
        {
            throw new RuntimeException("AdminSetting not found with id : " +id);
        }
        adminSettingRepository.deleteById(id);
    }
}
