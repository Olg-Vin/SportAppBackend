package org.vinio.services;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vinio.repositories.UserRepository;

import java.util.Collections;

/**
 * Объект UserDetails представляет аутентифицированного пользователя в рамках Spring Security и содержит такие детали,
 * как email пользователя, пароль, полномочия (роли).
 * */
@Service
@Log4j2
public class UserDetailsServ implements UserDetailsService {
    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServ(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Fetch user with username {} from DB", username);
        return  userRepository.findByEmail(username)
                .map(u -> {
                    log.info("user: {}", u);
                    return new User(
                            u.getEmail(),
                            u.getPassword(),
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + u.getRoles()))
                    );
                }).orElseThrow(() -> {
                    log.error("User not found in the DB");
                    return new UsernameNotFoundException("Пользователь " + username + " не найден");
                });
    }
}
