package com.example.PersonalFinanceManager.service;

import com.example.PersonalFinanceManager.model.Role;

import java.util.List;
import java.util.Optional;

public interface RoleServiceImpl {
    Role createRole(Role role);
    Optional<Role> getRoleById(Long id);
    List<Role> getAllRoles();
    Role updateRole(Long id, Role role);
    void deleteRole(Long id);
}
