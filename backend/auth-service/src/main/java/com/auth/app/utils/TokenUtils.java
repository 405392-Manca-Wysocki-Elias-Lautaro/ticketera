package com.auth.app.utils;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import com.auth.app.exception.exceptions.InvalidRefreshTokenException;

public class TokenUtils {

    private static final SecureRandom RANDOM = new SecureRandom();

    // Genera el token "en claro" para enviar al cliente
    public static String generateToken() {
        byte[] randomBytes = new byte[64]; // 512 bits de entropía
        RANDOM.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    // Hashea el token antes de guardar en DB
    public static String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error hashing token", e);
        }
    }

    public static String extractRefreshToken(String authHeader, String refreshCookie) {
        // 🧩 Preferí siempre la cookie
        String refreshToken = null;

        if (refreshCookie != null && !refreshCookie.isBlank()) {
            // 🔹 En cookies los caracteres como "+" o "/" pueden venir URL-encoded (%2B, %2F)
            refreshToken = URLDecoder.decode(refreshCookie, StandardCharsets.UTF_8);
        } else if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // 🔹 Como fallback, usa el Authorization header
            refreshToken = authHeader.substring(7).trim();
        }

        if (refreshToken == null || refreshToken.isBlank()) {
            throw new InvalidRefreshTokenException();
        }

        return refreshToken;
    }

    public static String extractBearerToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new InvalidRefreshTokenException();
        }
        return authHeader.substring(7).trim();
    }

}
