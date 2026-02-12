package com.github.birulazena.ApiGateway.security.service;

import com.github.birulazena.ApiGateway.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    @Value("${security.jwt.secret}")
    private String jwtSecret;

    private SecretKey secretKey;

    @PostConstruct
    private void init() {
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public Long getUserId(String token) {
        return parseToken(token)
                .get("userId", Long.class);
    }

    public String getRole(String token) {
        return parseToken(token)
                .get("role", String.class);
    }

    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception ex) {
            throw new InvalidTokenException(ex.getMessage());
        }
    }

    public Map<String, Object> getDetails(String token) {
        Claims claims = parseToken(token);
        Long userId = claims.get("userId", Long.class);
        String role = claims.get("role", String.class);
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("role", role);
        return map;
    }

    public String generateAdminAccessToken() {
        return Jwts.builder()
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + 30 * 1000))
                .claim("userId", -1L)
                .claim("role", "ADMIN")
                .signWith(secretKey)
                .compact();
    }
}
