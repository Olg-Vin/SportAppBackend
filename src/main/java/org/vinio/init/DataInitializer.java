package org.vinio.init;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.vinio.entities.Event;
import org.vinio.entities.UserEntity;
import org.vinio.entities.UserRole;
import org.vinio.entities.enums.Role;
import org.vinio.services.EventService;
import org.vinio.services.UserRoleService;
import org.vinio.services.UserService;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;
    private final UserRoleService userRoleService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EventService eventService;

    @Autowired
    public DataInitializer(UserService userService, UserRoleService userRoleService, BCryptPasswordEncoder bCryptPasswordEncoder, EventService eventService) {
        this.userService = userService;
        this.userRoleService = userRoleService;
        this.passwordEncoder = bCryptPasswordEncoder;
        this.eventService = eventService;
    }

    @Transactional
    @Override
    public void run(String... args) {
        // Создаем роли
        userRoleService.saveRole(new UserRole(Role.USER));
        userRoleService.saveRole(new UserRole(Role.ADMIN));

        // Получаем созданные роли
        UserRole userRole = userRoleService.findByRole(Role.USER);
        UserRole userAdmin = userRoleService.findByRole(Role.ADMIN);

        // Создаем пользователей
        UserEntity user1 = new UserEntity();
        user1.setName("Mimiler");
        user1.setEmail("testUser0@test.ru");
        user1.setPassword(passwordEncoder.encode("123456"));
        user1.setRoles(userRole);
        userService.saveUser(user1);

        UserEntity user2 = new UserEntity();
        user2.setEmail("testUser1@test.ru");
        user2.setPassword(passwordEncoder.encode("123456"));
        user2.setRoles(userRole);
        userService.saveUser(user2);

        UserEntity user3 = new UserEntity();
        user3.setEmail("testAdmin@test.ru");
        user3.setPassword(passwordEncoder.encode("123456"));
        user3.setRoles(userAdmin);
        userService.saveUser(user3);

        UserEntity user4 = new UserEntity();
        user4.setEmail("testUser2@test.ru");
        user4.setPassword(passwordEncoder.encode("123456"));
        user4.setRoles(userRole);
        userService.saveUser(user4);

        // Инициализация событий для пользователя
        createEvent(user1, "Jogging in the park", LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(1));
        createEvent(user2, "Cycling", LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(2).plusHours(2));
        createEvent(user3, "Meeting", LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(3).plusHours(1));
        createEvent(user4, "Yoga", LocalDateTime.now().plusDays(4), LocalDateTime.now().plusDays(4).plusHours(1));
        createEvent(user1, "Running", LocalDateTime.now().plusDays(5), LocalDateTime.now().plusDays(5).plusHours(2));
    }

    private void createEvent(UserEntity user, String title, LocalDateTime startTime, LocalDateTime endTime) {
        Event event = Event.builder()
                .user(user)
                .title(title)
                .startTime(startTime)
                .endTime(endTime)
                .status("Scheduled")
                .description("Event description here")
                .calories(200)  // Пример калорий
                .category("Exercise")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        eventService.saveEvent(event);  // Используем сервис для сохранения события
    }
}
