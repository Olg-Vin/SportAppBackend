package org.vinio.services;

import io.jsonwebtoken.Claims;
import jakarta.security.auth.message.AuthException;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.vinio.jwt.JwtProvider;
import org.vinio.jwt.JwtResponse;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {
    private final UserDetailsServ userDetailsServ;
    private final JwtProvider jwtProvider;
    private final Map<String, String> refreshStorage = new HashMap<>();
    //    Для хранения рефреш токена используется HashMap лишь для упрощения примера.
    //    Лучше использовать какое-нибудь постоянное хранилище, например Redis.
    @Autowired
    public AuthService(UserDetailsServ userDetailsServ, JwtProvider jwtProvider) {
        this.userDetailsServ = userDetailsServ;
        this.jwtProvider = jwtProvider;
    }

    public JwtResponse login(@NonNull String username) {
        final UserDetails userDetails = userDetailsServ.loadUserByUsername(username);
        final String accessToken = jwtProvider.generateAccessToken(userDetails);
        final String refreshToken = jwtProvider.generateRefreshToken(userDetails);
        refreshStorage.put(userDetails.getUsername(), refreshToken);
        return new JwtResponse(accessToken, refreshToken);
    }

    public JwtResponse getAccessToken(@NonNull String refreshToken) {
        if (jwtProvider.isRefreshTokenValid(refreshToken)) {
            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            final String login = claims.getSubject();
            final String saveRefreshToken = refreshStorage.get(login);
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                final UserDetails userDetails = userDetailsServ.loadUserByUsername(login);
                final String accessToken = jwtProvider.generateAccessToken(userDetails);
                return new JwtResponse(accessToken, refreshToken);
            }
        }
        return new JwtResponse(null, null);
    }

    /**
     * Позволяет обновить refresh токен.
     * */
    public JwtResponse refresh(@NonNull String refreshToken) throws AuthException {
        if (jwtProvider.isRefreshTokenValid(refreshToken)) {
            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            final String login = claims.getSubject();
            final String saveRefreshToken = refreshStorage.get(login);
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                final UserDetails userDetails = userDetailsServ.loadUserByUsername(login);
                final String accessToken = jwtProvider.generateAccessToken(userDetails);
                final String newRefreshToken = jwtProvider.generateRefreshToken(userDetails);
                refreshStorage.put(userDetails.getUsername(), newRefreshToken);
                return new JwtResponse(accessToken, newRefreshToken);
            }
        }
        throw new AuthException("Невалидный JWT токен");
    }
}
