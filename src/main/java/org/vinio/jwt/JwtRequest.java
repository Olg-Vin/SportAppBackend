package org.vinio.jwt;

import lombok.Data;


/**
 * oбъект, который клиент будет присылать
 * при регистрации и авторизации, чтобы получить JWT
 * */

@Data
public class JwtRequest {
    private String username;
    private String password;

    @Override
    public String toString() {
        return "JwtRequest{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}