package com.example.PersonalFinanceManager.service;

import com.example.PersonalFinanceManager.model.User;

import java.util.List;
import java.util.Optional;

public interface UserServiceImpl {
    List<User> getAllUsers();
    Optional<User> getUserById(Long id);
    User createUser(User user);
    User updateUser(Long id, User user);
    void deleteUser(Long id);
}
