package com.example.PersonalFinanceManager.dto;

import java.time.LocalDateTime;

public class UserProfileDTO {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String avatarUrl;
    private LocalDateTime createdAt;

    private long totalAccounts;
    private long totalTransactions;
    private long totalGoals;

    // Getters / Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public long getTotalAccounts() { return totalAccounts; }
    public void setTotalAccounts(long totalAccounts) { this.totalAccounts = totalAccounts; }

    public long getTotalTransactions() { return totalTransactions; }
    public void setTotalTransactions(long totalTransactions) { this.totalTransactions = totalTransactions; }

    public long getTotalGoals() { return totalGoals; }
    public void setTotalGoals(long totalGoals) { this.totalGoals = totalGoals; }
}
