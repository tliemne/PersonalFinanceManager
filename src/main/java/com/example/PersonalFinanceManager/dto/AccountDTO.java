package com.example.PersonalFinanceManager.dto;

import com.example.PersonalFinanceManager.model.Account;
import java.time.LocalDateTime;

public class AccountDTO {

    private Long id;
    private UserDTO user; // chứa toàn bộ thông tin user
    private String name;
    private Double balance;
    private String currency;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor map Account → AccountDTO
    public AccountDTO(Account account) {
        this.id = account.getId();
        this.user = new UserDTO(account.getUser()); // map User → UserDTO
        this.name = account.getName();
        this.balance = account.getBalance();
        this.currency = account.getCurrency();
        this.createdAt = account.getCreatedAt();
        this.updatedAt = account.getUpdatedAt();
    }

    // Getters và Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public UserDTO getUser() { return user; }
    public void setUser(UserDTO user) { this.user = user; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getBalance() { return balance; }
    public void setBalance(Double balance) { this.balance = balance; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
