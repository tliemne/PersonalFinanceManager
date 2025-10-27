package com.example.PersonalFinanceManager.dto;

import com.example.PersonalFinanceManager.model.AdminSetting;

import java.time.LocalDateTime;

public class AdminSettingDTO {

    private Long id;
    private String settingKey;
    private String settingValue;
    private LocalDateTime updatedAt;

    public AdminSettingDTO(AdminSetting setting) {
        this.id = setting.getId();
        this.settingKey = setting.getSettingKey();
        this.settingValue = setting.getSettingValue();
        this.updatedAt = setting.getUpdatedAt();
    }

    // getters
    public Long getId() { return id; }
    public String getSettingKey() { return settingKey; }
    public String getSettingValue() { return settingValue; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
