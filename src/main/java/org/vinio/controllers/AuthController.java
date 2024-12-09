package org.vinio.controllers;

import jakarta.security.auth.message.AuthException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vinio.entities.UserEntity;
import org.vinio.jwt.JwtRequest;
import org.vinio.jwt.JwtResponse;
import org.vinio.jwt.RefreshJwtRequest;
import org.vinio.services.AuthService;
import org.vinio.services.UserService;

@RestController
@RequestMapping("/api/auth")
@Log4j2
public class AuthController {
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    @Autowired
    public AuthController(AuthService authService, AuthenticationManager authenticationManager, UserService userService) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<JwtResponse> registration(@RequestBody JwtRequest registerRequest) {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(registerRequest.getUsername());
        userEntity.setPassword(registerRequest.getPassword());
        userService.saveUser(userEntity);

        final JwtResponse token = authService.login(registerRequest.getUsername());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest authRequest) {
        System.out.println(authRequest);
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        final JwtResponse token = authService.login(authRequest.getUsername());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/logout")
    public String logout() {
//        TODO механизм отзыва токена?
        return "all oke";
    }


    @PostMapping("/token")
    public ResponseEntity<JwtResponse> getNewAccessToken(@RequestBody RefreshJwtRequest request) {
        final JwtResponse token = authService.getAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> getNewRefreshToken(@RequestBody RefreshJwtRequest request) throws AuthException {
        final JwtResponse token = authService.refresh(request.getRefreshToken());
        return ResponseEntity.ok(token);
    }
}
