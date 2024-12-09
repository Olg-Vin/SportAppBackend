package org.vinio.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vinio.entities.UserRole;
import org.vinio.entities.enums.Role;
import org.vinio.repositories.UserRoleRepository;

@Service
public class UserRoleService {
    private final UserRoleRepository userRoleRepository;

    @Autowired
    public UserRoleService(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }

    public UserRole saveRole(UserRole userRole){
        return userRoleRepository.save(userRole);
    }

    public UserRole findByRole(Role role){
        return userRoleRepository.findByRole(role).orElseThrow();
    }
}
