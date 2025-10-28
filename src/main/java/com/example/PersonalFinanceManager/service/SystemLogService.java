package com.example.PersonalFinanceManager.service;

import com.example.PersonalFinanceManager.model.SystemLog;
import com.example.PersonalFinanceManager.repository.SystemLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SystemLogService implements SystemLogServiceImpl{
    @Autowired
    private SystemLogRepository systemLogRepository;
    @Override
    public SystemLog createSystemLog(SystemLog systemLog) {
        return systemLogRepository.save(systemLog);
    }

    @Override
    public Optional<SystemLog> getSystemLogById(Long id) {
        return systemLogRepository.findById(id);
    }

    @Override
    public List<SystemLog> getAllSystemLogs() {
        return systemLogRepository.findAll();
    }

    @Override
    public SystemLog updateSystemLog(Long id, SystemLog systemLog) {
        return systemLogRepository.findById(id).map(e-> {
            e.setAction(systemLog.getAction());
            e.setDescription(systemLog.getDescription());
            return systemLogRepository.save(e);
        }).orElseThrow(()-> new RuntimeException("not found"));
    }

    @Override
    public void deleteSystemLog(Long id) {
        if(!systemLogRepository.existsById(id))
        {
            throw new RuntimeException("not found");
        }
        systemLogRepository.deleteById(id);
    }
}
