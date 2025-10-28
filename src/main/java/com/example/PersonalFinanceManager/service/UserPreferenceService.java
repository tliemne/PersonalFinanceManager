package com.example.PersonalFinanceManager.service;

import com.example.PersonalFinanceManager.model.UserPreference;
import com.example.PersonalFinanceManager.repository.UserPreferenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserPreferenceService implements UserPreferenceServiceImpl {
    @Autowired
    private UserPreferenceRepository userPreferenceRepository;
    @Override
    public UserPreference createUserPreference(UserPreference userPreference) {
        return userPreferenceRepository.save(userPreference);
    }

    @Override
    public Optional<UserPreference> getUserPreferenceById(Long id) {
        return userPreferenceRepository.findById(id);
    }

    @Override
    public List<UserPreference> getAllUserPreferences() {
        return userPreferenceRepository.findAll();
    }

    @Override
    public UserPreference updateUserPreference(Long id, UserPreference userPreference) {
        return userPreferenceRepository.findById(id).map(e->{
            e.setPreferredCurrency(userPreference.getPreferredCurrency());
            e.setTheme(userPreference.getTheme());
            e.setNotificationEnabled(userPreference.getNotificationEnabled());
            return userPreferenceRepository.save(e);
        }).orElseThrow(()-> new RuntimeException("not found"));
    }

    @Override
    public void deleteUserPreference(Long id) {
        if(!userPreferenceRepository.existsById(id))
        {
            throw new RuntimeException("not found");
        }
        userPreferenceRepository.deleteById(id);
    }
}
