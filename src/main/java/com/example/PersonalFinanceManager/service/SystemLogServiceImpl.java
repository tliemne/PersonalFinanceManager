package com.example.PersonalFinanceManager.service;

import com.example.PersonalFinanceManager.model.SystemLog;

import java.util.List;
import java.util.Optional;

public interface SystemLogServiceImpl {
    SystemLog createSystemLog(SystemLog systemLog);
    Optional<SystemLog> getSystemLogById(Long id);
    List<SystemLog> getAllSystemLogs();
    SystemLog updateSystemLog(Long id, SystemLog systemLog);
    void deleteSystemLog(Long id);
}
