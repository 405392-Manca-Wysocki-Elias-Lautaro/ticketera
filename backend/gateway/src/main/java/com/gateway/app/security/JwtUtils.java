package com.gateway.app.security;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

/**
 * JwtUtils para verificación RS256 (clave pública).
 * Usado por Gateway y microservicios consumidores.
 */
@Component
public class JwtUtils {

    private final PublicKey publicKey;

    public JwtUtils(@Value("${JWT_PUBLIC_KEY_PATH}") String publicKeyPath) {
        try {
            System.out.println("[JWT] Loading public key from: " + publicKeyPath);
            String key = new String(Files.readAllBytes(Paths.get(publicKeyPath)))
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] keyBytes = Base64.getDecoder().decode(key);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            this.publicKey = KeyFactory.getInstance("RSA").generatePublic(spec);
            System.out.println("[JWT] ✅ Public key loaded successfully");
        } catch (Exception e) {
            throw new RuntimeException("❌ Error cargando clave pública desde: " + publicKeyPath, e);
        }
    }

    public Claims validateAndParseClaims(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    public String getSubject(Claims claims) {
        return claims.getSubject();
    }

    public String getJti(Claims claims) {
        return claims.getId();
    }
}
