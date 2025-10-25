package com.example.PersonalFinanceManager.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(UserRoleId.class)
public class UserRole {
    @Id
    private Long userId;

    @Id
    private Long roleId;
}
