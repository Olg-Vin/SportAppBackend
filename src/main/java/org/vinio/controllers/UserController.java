package org.vinio.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.vinio.entities.UserEntity;
import org.vinio.jwt.JwtProvider;
import org.vinio.jwt.JwtResponse;
import org.vinio.services.AuthService;
import org.vinio.services.UserService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private AuthService authService;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserController(BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    // Получение списка всех пользователей
    @GetMapping
    public ResponseEntity<List<UserEntity>> getAllUsers() {
        List<UserEntity> users = userService.findAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    // Получение пользователя по ID
    @GetMapping("/{id}")
    public ResponseEntity<UserEntity> getUserById(@PathVariable Long id) {
        Optional<UserEntity> user = userService.findUserById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/data")
    public ResponseEntity<?> getUserData(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Извлекаем токен из заголовка Authorization (удаляем "Bearer " в начале)
            String token = authorizationHeader.replace("Bearer ", "");
            System.out.println("hhhhhhhh");
            System.out.println(token);

            // Извлекаем email пользователя из токена
            String email = jwtProvider.getAccessTokenUsername(token);

            // Получаем данные пользователя
            Optional<UserEntity> userOptional = userService.findUserByEmail(email);

            if (userOptional.isPresent()) {
                UserEntity user = userOptional.get();

                // Формируем ответ с нужными полями
                Map<String, Object> response = new HashMap<>();
                response.put("username", user.getName());
                response.put("email", user.getEmail());
                response.put("createdAt", user.getCreatedAt());
                response.put("updatedAt", user.getUpdatedAt());

                System.out.println("AAAAAAAAAAA");
                System.out.println(response);

                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Пользователь не найден");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Ошибка авторизации");
        }
    }



    // Добавление нового пользователя
    @PostMapping
    public ResponseEntity<UserEntity> createUser(@Valid @RequestBody UserEntity user) {
        UserEntity createdUser = userService.saveUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    // Обновление данных пользователя
    @PutMapping("/{id}")
    public ResponseEntity<UserEntity> updateUser(@PathVariable Long id, @Valid @RequestBody UserEntity user) {
        Optional<UserEntity> existingUser = userService.findUserById(id);
        if (existingUser.isPresent()) {
            user.setId(id); // Устанавливаем ID для обновления
            UserEntity updatedUser = userService.saveUser(user);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/updatePassword")
    public ResponseEntity<?> updatePassword(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody PasswordUpdateRequest passwordUpdateRequest) {

        // Извлекаем токен из заголовка Authorization (удаляем "Bearer " в начале)
        String token = authorizationHeader.replace("Bearer ", "");

        // Извлекаем email пользователя из токена
        String email = jwtProvider.getAccessTokenUsername(token);

        // Находим пользователя по email
        Optional<UserEntity> optionalUser = userService.findUserByEmail(email);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Пользователь не найден");
        }

        UserEntity user = optionalUser.get();

        // Проверяем старый пароль
        if (!passwordEncoder.matches(passwordUpdateRequest.getOldPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неверный старый пароль");
        }

        // Обновляем пароль
        user.setPassword(passwordEncoder.encode(passwordUpdateRequest.getNewPassword()));
        userService.saveUser(user); // Сохраняем обновленного пользователя

        return ResponseEntity.ok("Пароль успешно обновлён");
    }

    @PutMapping("/updateEmail")
    public ResponseEntity<?> updateEmail(@RequestHeader("Authorization") String authorizationHeader,
                                         @RequestBody EmailUpdateRequest emailUpdateRequest) {
        try {
            // Извлекаем токен
            String token = authorizationHeader.replace("Bearer ", "");
            String userEmail = jwtProvider.getAccessTokenUsername(token);

            // Получаем пользователя
            Optional<UserEntity> optionalUser = userService.findUserByEmail(userEmail);
            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Пользователь не найден");
            }

            UserEntity user = optionalUser.get();

            // Проверяем, что введённый старый email совпадает с текущим email пользователя
            if (!user.getEmail().equals(emailUpdateRequest.getOldEmail())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Старый email не совпадает");
            }

            // Обновляем email
            user.setEmail(emailUpdateRequest.getNewEmail());
            user.setUpdatedAt(LocalDateTime.now());
            userService.saveUser(user);

            // Генерируем новые access и refresh токены
            JwtResponse jwtResponse = authService.login(user.getEmail());

            return ResponseEntity.ok(jwtResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при обновлении email");
        }
    }


    @DeleteMapping("/deleteAccount")
    public ResponseEntity<?> deleteAccount(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Извлекаем токен
            String token = authorizationHeader.replace("Bearer ", "");
            String userEmail = jwtProvider.getAccessTokenUsername(token);

            // Получаем пользователя
            Optional<UserEntity> optionalUser = userService.findUserByEmail(userEmail);
            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Пользователь не найден");
            }

            UserEntity user = optionalUser.get();
            userService.deleteUser(user);

            return ResponseEntity.ok("Аккаунт удалён");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при удалении аккаунта");
        }
    }

}
