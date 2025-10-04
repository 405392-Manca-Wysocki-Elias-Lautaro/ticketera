package com.auth.app.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.stereotype.Component;

import java.util.Date;

import javax.crypto.SecretKey;

import com.auth.app.entity.User;

@Component
public class TokenProvider {

    private final String jwtSecret = "miSuperClaveSecreta"; // en un env var
    private final long jwtAccessTokenExpirationMs = 15 * 60 * 1000; // 15 min
    private final long jwtRefreshTokenExpirationMs = 7 * 24 * 60 * 60 * 1000; // 7 días
    private final SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

    public String generateAccessToken(User user) {
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("role", user.getRole())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtAccessTokenExpirationMs))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtRefreshTokenExpirationMs))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }
}

