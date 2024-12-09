package org.vinio.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.vinio.entities.UserRole;
import org.vinio.entities.enums.Role;

import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    Optional<UserRole> findByRole(Role role);
}
