package org.vinio.entities;

import jakarta.persistence.*;
import lombok.Setter;
import org.vinio.entities.enums.Role;

import java.util.HashSet;
import java.util.Set;


/**
 * Таблица в бд, отвечающая за роли существующие в системе,
 * такие как обычный пользователь, админ или модератор.
 * Весь список ролей хранится в enum Role
 *
 * С пользователями связь м-м
 * */
@Entity
@Setter
@Table(name = "roles")
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "role", unique = true)
    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToMany(mappedBy = "roles")
    private Set<UserEntity> users = new HashSet<>();

    public UserRole(Role role) {
        this.role = role;
    }

    public UserRole() {}
    public String getRole() {
        return role.name();
    }

    @Override
    public String toString() {
        return role.name();
    }
}
