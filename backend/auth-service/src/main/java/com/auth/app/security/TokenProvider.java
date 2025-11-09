package com.auth.app.security;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth.app.domain.enums.LogAction;
import com.auth.app.domain.model.UserModel;
import com.auth.app.exception.exceptions.InvalidOrUnknownTokenException;
import com.auth.app.exception.exceptions.InvalidRefreshTokenException;
import com.auth.app.services.domain.AuditLogService;
import com.auth.app.services.domain.UserService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TokenProvider {

    private final UserService userService;
    private final AuditLogService auditLogService;
    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;
    private final Long accessTokenExpirationMs;

    public TokenProvider(
            UserService userService,
            AuditLogService auditLogService,
            @Value("${jwt.private-key}") String privateKeyPath,
            @Value("${jwt.public-key}") String publicKeyPath,
            @Value("${jwt.expiration.access}") Long jwtAccessTokenExpirationMs
    ) {
        this.userService = userService;
        this.auditLogService = auditLogService;
        this.privateKey = loadPrivateKey(privateKeyPath);
        this.publicKey = loadPublicKey(publicKeyPath);
        this.accessTokenExpirationMs = jwtAccessTokenExpirationMs;
    }

    // ------------------------------------------------------------
    // 🔐 Generate Access Token (RS256)
    // ------------------------------------------------------------
    public String generateAccessToken(UserModel user, UUID deviceId) {
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .setId(UUID.randomUUID().toString())
                .claim("email", user.getEmail())
                .claim("role", user.getRole().getCode())
                .claim("deviceId", deviceId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationMs))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    // ------------------------------------------------------------
    // 👤 Extract userId from token
    // ------------------------------------------------------------
    public UUID getUserIdFromToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token);

            return UUID.fromString(claimsJws.getBody().getSubject());

        } catch (ExpiredJwtException ex) {
            auditLogService.logAction(null, LogAction.ACCESS_TOKEN_EXPIRED, null, null);
            log.warn("[TOKEN] Access token expired: {}", ex.getMessage());
            throw new InvalidOrUnknownTokenException();

        } catch (JwtException | IllegalArgumentException ex) {
            auditLogService.logAction(null, LogAction.TOKEN_VALIDATION_FAILED, null, null);
            log.warn("[TOKEN] Invalid or tampered token: {}", ex.getMessage());
            throw new InvalidRefreshTokenException();
        }
    }

    // ------------------------------------------------------------
    // 📱 Extract deviceId
    // ------------------------------------------------------------
    public UUID getDeviceIdFromToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token);

            String deviceId = claimsJws.getBody().get("deviceId", String.class);
            return UUID.fromString(deviceId);

        } catch (ExpiredJwtException ex) {
            auditLogService.logAction(null, LogAction.ACCESS_TOKEN_EXPIRED, null, null);
            log.warn("[TOKEN] Access token expired: {}", ex.getMessage());
            throw new InvalidOrUnknownTokenException();

        } catch (JwtException | IllegalArgumentException ex) {
            auditLogService.logAction(null, LogAction.TOKEN_VALIDATION_FAILED, null, null);
            log.warn("[TOKEN] Invalid or tampered token: {}", ex.getMessage());
            throw new InvalidRefreshTokenException();
        }
    }

    // ------------------------------------------------------------
    // 🧍 Extract user from refresh token
    // ------------------------------------------------------------
    public UserModel extractUserFromRefreshToken(String token) {
        UUID userId = getUserIdFromToken(token);
        return userService.findById(userId);
    }

    // ------------------------------------------------------------
    // 🔍 Validate Access Token
    // ------------------------------------------------------------
    public boolean validateAccessToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token);
            return true;

        } catch (ExpiredJwtException ex) {
            auditLogService.logAction(null, LogAction.ACCESS_TOKEN_EXPIRED, null, null);
            log.warn("[TOKEN] Access token expired: {}", ex.getMessage());
            throw new InvalidOrUnknownTokenException();

        } catch (JwtException | IllegalArgumentException ex) {
            auditLogService.logAction(null, LogAction.TOKEN_VALIDATION_FAILED, null, null);
            log.warn("[TOKEN] Invalid access token: {}", ex.getMessage());
            throw new InvalidOrUnknownTokenException();
        }
    }

    public UserModel extractUserFromAuthorizationHeader(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            log.warn("[TOKEN] Missing or malformed Authorization header");
            auditLogService.logAction(null, LogAction.TOKEN_VALIDATION_FAILED, null, null);
            throw new InvalidOrUnknownTokenException();
        }

        String token = authorizationHeader.substring(7);
        validateAccessToken(token);

        UUID userId = getUserIdFromToken(token);
        return userService.findById(userId);
    }

    // ------------------------------------------------------------
    // 🗝️ Load keys from file system
    // ------------------------------------------------------------
    private RSAPrivateKey loadPrivateKey(String path) {
        try {
            String key = Files.readString(Path.of(path))
                    .replaceAll("-----BEGIN (.*)-----", "")
                    .replaceAll("-----END (.*)----", "")
                    .replaceAll("\\s", "");
            byte[] decoded = Base64.getDecoder().decode(key);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = kf.generatePrivate(keySpec);
            return (RSAPrivateKey) privateKey;
        } catch (Exception e) {
            throw new IllegalStateException("Unable to load private key from " + path, e);
        }
    }

    private RSAPublicKey loadPublicKey(String path) {
        try {
            String key = Files.readString(Path.of(path))
                    .replaceAll("-----BEGIN (.*)-----", "")
                    .replaceAll("-----END (.*)----", "")
                    .replaceAll("\\s", "");
            byte[] decoded = Base64.getDecoder().decode(key);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PublicKey publicKey = kf.generatePublic(keySpec);
            return (RSAPublicKey) publicKey;
        } catch (Exception e) {
            throw new IllegalStateException("Unable to load public key from " + path, e);
        }
    }

    public Long getAccessTokenExpirationMs() {
        return accessTokenExpirationMs;
    }
}
