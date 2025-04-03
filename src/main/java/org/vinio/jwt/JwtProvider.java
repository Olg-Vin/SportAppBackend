package org.vinio.jwt;

import com.auth0.jwt.JWT;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.vinio.entities.enums.Role;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Класс для работы с jwt - их генерация и валидация. ||
 * имя пользователя у нас хранится в Subject
 * */
@Component
@Log4j2
public class JwtProvider {

    private final SecretKey jwtAccessSecret;
    private final SecretKey jwtRefreshSecret;
    private final Duration jwtAccessLiveTime;
    private final Duration jwtRefreshLiveTime;

    public JwtProvider(
            @Value("${jwt.secret.access}") String jwtAccessSecret,
            @Value("${jwt.secret.refresh}") String jwtRefreshSecret,
            @Value("${jwt.lifeTime.access}") Duration jwtAccessLiveTime,
            @Value("${jwt.lifeTime.refresh}") Duration jwtRefreshLiveTime) {
        this.jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret)); // создается секретный ключ для подписи JWT токенов
        this.jwtRefreshSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtRefreshSecret)); // создается секретный ключ для подписи JWT токенов
        this.jwtAccessLiveTime = jwtAccessLiveTime;
        this.jwtRefreshLiveTime = jwtRefreshLiveTime;
    }

    /**
     * Генерирует access токен ||
     *
     * Формирует токен авторизации?
     *
     * @param userDetails данные пользователя
     * @return токен
     * */
    public String generateAccessToken(@NonNull UserDetails userDetails) {
        final Date issuedDate = new Date(); // время создание токена
        final Date accessExpiration = new Date(issuedDate.getTime()+jwtAccessLiveTime.toMillis()); // дата истечения токена

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toList()));


        return Jwts
                .builder()
                .setClaims(claims)
                .setIssuedAt(issuedDate)
                .setExpiration(accessExpiration)
                .setSubject(userDetails.getUsername())
                .signWith(jwtAccessSecret)
                .compact();
    }

    /**
     * Генерирует refresh токен ||
     *
     * который нужен чтобы оставаться авторизованным, в нём указываются часто используемые данные,
     * чтобы реже опрашивать бек (такие как имя пользователя)
     *
     * @param userDetails данные пользователя
     * @return токен
     * */
    public String generateRefreshToken(@NonNull UserDetails userDetails) {
        final Date issuedDate = new Date(); // время создание токена
        final Date refreshExpiration = new Date(issuedDate.getTime()+jwtRefreshLiveTime.toMillis()); // дата истечения токена

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
//
        return Jwts
                .builder()
                .setClaims(claims)
                .setIssuedAt(issuedDate)
                .setExpiration(refreshExpiration)
                .setSubject(userDetails.getUsername())
                .signWith(jwtRefreshSecret)
                .compact();
    }

    public boolean isAccessTokenValid(@NonNull String accessToken) {
        return isTokenValid(accessToken, jwtAccessSecret);
    }

    public boolean isRefreshTokenValid(@NonNull String refreshToken) {
        return isTokenValid(refreshToken, jwtRefreshSecret);
    }

    private boolean isTokenValid(@NonNull String token, @NonNull Key secret) {
        log.info("go to validation token");
        try {
            log.info("subject: {}", JWT.decode(token).getSubject());
            Jwts.parserBuilder()
                    .setSigningKey(secret)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException expEx) {
            log.error("Token expired", expEx);
        } catch (UnsupportedJwtException unsEx) {
            log.error("Unsupported jwt", unsEx);
        } catch (MalformedJwtException mjEx) {
            log.error("Malformed jwt", mjEx);
        } catch (SignatureException sEx) {
            log.error("Invalid signature", sEx);
        } catch (Exception e) {
            log.error("invalid token", e);
        }
        log.info("sorry, its false");
        return false;
    }


    public String getAccessTokenUsername(String token){
        return getAccessClaims(token).getSubject();
    }
    public String getRefreshTokenUsername(String token){
        return getRefreshClaims(token).getSubject();
    }

    public List<Role> getAccessTokenRoles(String token){
        return getAccessClaims(token).get("roles", List.class);
    }
    public List<Role> getRefreshTokenRoles(String token){
        return getRefreshClaims(token).get("roles", List.class);
    }

    public Claims getAccessClaims(@NonNull String token) {
        return getClaims(token, jwtAccessSecret);
    }

    public Claims getRefreshClaims(@NonNull String token) {
        return getClaims(token, jwtRefreshSecret);
    }

    private Claims getClaims(@NonNull String token, @NonNull Key secret) {
        return Jwts.parserBuilder()
            .setSigningKey(secret)
            .build()
            .parseClaimsJws(token)
            .getBody();
    }
}