package com.example.PersonalFinanceManager.repository;

import com.example.PersonalFinanceManager.model.AdminSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminSettingRepository extends JpaRepository<AdminSetting,Long> {
    Optional<AdminSetting> findBySettingKey(String key);
}
