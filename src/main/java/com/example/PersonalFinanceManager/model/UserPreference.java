package com.example.PersonalFinanceManager.model;

import jakarta.persistence.*;

@Entity
@Table(name = "user_preferences")
public class UserPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name="user_id", nullable=false, unique=true)
    private User user;

    @Column(length=10, nullable=false)
    private String preferredCurrency = "VND";

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private Theme theme = Theme.LIGHT;

    @Column(nullable=false)
    private Boolean notificationEnabled = true;

    public enum Theme {
        LIGHT, DARK
    }

    public UserPreference() {}

    // Getters và Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getPreferredCurrency() { return preferredCurrency; }
    public void setPreferredCurrency(String preferredCurrency) { this.preferredCurrency = preferredCurrency; }

    public Theme getTheme() { return theme; }
    public void setTheme(Theme theme) { this.theme = theme; }

    public Boolean getNotificationEnabled() { return notificationEnabled; }
    public void setNotificationEnabled(Boolean notificationEnabled) { this.notificationEnabled = notificationEnabled; }
}
