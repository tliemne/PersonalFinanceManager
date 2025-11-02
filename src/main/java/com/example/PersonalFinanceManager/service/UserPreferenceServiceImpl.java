package com.example.PersonalFinanceManager.service;



import com.example.PersonalFinanceManager.model.UserPreference;

import java.util.List;
import java.util.Optional;

public interface UserPreferenceServiceImpl {
    UserPreference createUserPreference(UserPreference userPreference);
    Optional<UserPreference> getUserPreferenceById(Long id);
    List<UserPreference> getAllUserPreferences();
    UserPreference updateUserPreference(Long id , UserPreference userPreference);
    void deleteUserPreference(Long id);
    UserPreference getByUserId(Long userId);
}
