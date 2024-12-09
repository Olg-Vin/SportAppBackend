package org.vinio.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Объект для ответа
 * */
@Getter
@AllArgsConstructor
public class JwtResponse {
    private final String type = "Bearer";
    private String accessToken;
    private String refreshToken;
}