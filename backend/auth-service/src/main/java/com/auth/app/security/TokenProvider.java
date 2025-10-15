package com.auth.app.security;

import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth.app.domain.model.UserModel;
import com.auth.app.exception.exceptions.InvalidOrUnknownTokenException;
import com.auth.app.exception.exceptions.InvalidRefreshTokenException;
import com.auth.app.services.domain.UserService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TokenProvider {

    private final UserService userService;
    private final SecretKey key;
    private final Long accessTokenExpirationMs;

    public TokenProvider(
            UserService userService,
            @Value("${jwt.secret}") String jwtSecret,
            @Value("${jwt.expiration.access}") Long jwtAccessTokenExpirationMs
    ) {
        this.userService = userService;
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        this.accessTokenExpirationMs = jwtAccessTokenExpirationMs;
    }

    public String generateAccessToken(UserModel user) {
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("role", user.getRole().getCode())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationMs))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public UUID getUserIdFromToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            String subject = claimsJws.getBody().getSubject();
            return UUID.fromString(subject);

        } catch (JwtException | IllegalArgumentException ex) {
            throw new InvalidRefreshTokenException();
        }
    }

    public UserModel extractUserFromRefreshToken(String token) {
        UUID userId = getUserIdFromToken(token);
        return userService.findById(userId);
    }

    public UserModel extractUserFromAuthorizationHeader(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new InvalidOrUnknownTokenException();
        }

        String token = authorizationHeader.substring(7);

        validateAccessToken(token);

        UUID userId = getUserIdFromToken(token);

        return userService.findById(userId);
    }

    public boolean validateAccessToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            throw new InvalidOrUnknownTokenException();
        }
    }

}
