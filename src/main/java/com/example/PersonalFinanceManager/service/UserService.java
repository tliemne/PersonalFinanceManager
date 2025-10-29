package com.example.PersonalFinanceManager.service;

import com.example.PersonalFinanceManager.model.User;
import com.example.PersonalFinanceManager.model.UserPreference;
import com.example.PersonalFinanceManager.repository.UserPreferenceRepository;
import com.example.PersonalFinanceManager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserServiceImpl {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private  UserPreferenceRepository userPreferenceRepository;
    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(Long id, User user) {
        return userRepository.findById(id).map(existing -> {
            existing.setFullName(user.getFullName());
            existing.setAvatarUrl(user.getAvatarUrl());
            existing.setIsActive(user.getIsActive());

            // Có thể kiểm tra username/email unique trước khi set
            existing.setUsername(user.getUsername());
            existing.setEmail(user.getEmail());

            return userRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setIsActive(false); // Soft delete
            userRepository.save(user);
        });
    }
    @Override
    public UserPreference getUserPreferenceByUserId(Long userId) {
        return userPreferenceRepository.findByUserId(userId);
    }
}

