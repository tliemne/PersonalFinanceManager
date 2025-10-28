package com.example.PersonalFinanceManager.dto;

public class UserPreferenceDTO {
    private Long id;
    private Long userId;
    private String preferredCurrency;
    private String theme;  // LIGHT hoặc DARK
    private Boolean notificationEnabled;

    public UserPreferenceDTO() {}

    public UserPreferenceDTO(Long id, Long userId, String preferredCurrency, String theme, Boolean notificationEnabled) {
        this.id = id;
        this.userId = userId;
        this.preferredCurrency = preferredCurrency;
        this.theme = theme;
        this.notificationEnabled = notificationEnabled;
    }

    // Getters và Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getPreferredCurrency() { return preferredCurrency; }
    public void setPreferredCurrency(String preferredCurrency) { this.preferredCurrency = preferredCurrency; }

    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }

    public Boolean getNotificationEnabled() { return notificationEnabled; }
    public void setNotificationEnabled(Boolean notificationEnabled) { this.notificationEnabled = notificationEnabled; }
}
