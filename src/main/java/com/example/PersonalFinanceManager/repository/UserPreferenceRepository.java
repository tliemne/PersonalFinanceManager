package com.example.PersonalFinanceManager.repository;

import com.example.PersonalFinanceManager.model.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPreferenceRepository extends JpaRepository<UserPreference, Long> {
    UserPreference findByUserId(Long userId);

}
