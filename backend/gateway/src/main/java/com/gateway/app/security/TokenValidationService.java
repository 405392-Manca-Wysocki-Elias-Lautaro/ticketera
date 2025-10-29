package com.gateway.app.security;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenValidationService {

    private final JwtUtils jwtUtils;
    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final WebClient.Builder webClientBuilder;

    @Value("${auth.service-url}")
    private String authServiceUrl;

    public Mono<Boolean> validateToken(String token) {
        if (token == null || token.isBlank()) {
            return Mono.error(new RuntimeException("Empty or null token"));
        }

        try {
            Claims claims = jwtUtils.validateAndParseClaims(token);

            if (jwtUtils.isTokenExpired(claims)) {
                return Mono.error(new RuntimeException("Token expired"));
            }

            long remainingMs = claims.getExpiration().getTime() - System.currentTimeMillis();
            Duration dynamicTTL = Duration.ofMillis(Math.min(remainingMs, Duration.ofMinutes(1).toMillis()));
            Duration ttl = dynamicTTL.isNegative() ? Duration.ofSeconds(10) : dynamicTTL;

            String jtiFromToken = jwtUtils.getJti(claims);
            final String resolvedJti = (jtiFromToken != null)
                    ? jtiFromToken
                    : token.substring(0, Math.min(token.length(), 10));

            String cacheKey = "token:" + resolvedJti;

            return redisTemplate.opsForValue().get(cacheKey)
                    .flatMap(status -> {
                        if ("valid".equals(status)) {
                            log.debug("[CACHE] Hit for {}", resolvedJti);
                            return Mono.just(true);
                        }
                        if ("revoked".equals(status)) {
                            log.debug("[CACHE] Revoked token {}", resolvedJti);
                            return Mono.error(new RuntimeException("Token revoked (Gateway cache)"));
                        }
                        return checkRemoteAndCache(token, cacheKey, ttl);
                    })
                    .switchIfEmpty(Mono.defer(() -> checkRemoteAndCache(token, cacheKey, ttl)))
                    .doOnSuccess(r -> log.info("✅ Token validated (cache or remote)"))
                    .doOnError(err -> log.warn("❌ Token validation failed: {}", err.getMessage()));

        } catch (JwtException e) {
            log.error("❌ Invalid JWT: {}", e.getMessage());
            return Mono.error(new RuntimeException("Invalid JWT: " + e.getMessage()));
        } catch (Exception e) {
            log.error("💥 Unexpected error during validation: {}", e.getMessage());
            return Mono.error(e);
        }
    }

    private Mono<Boolean> checkRemoteAndCache(String token, String cacheKey, Duration ttl) {
        log.info("🌐 Validating token remotely with AuthService...");

        return webClientBuilder.build()
                .get()
                .uri(authServiceUrl + "/validate?token=" + token)
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        log.debug("[REMOTE] Token valid, caching for {}", ttl);
                        return redisTemplate.opsForValue()
                                .set(cacheKey, "valid", ttl)
                                .thenReturn(true);
                    } else {
                        log.warn("[REMOTE] Token invalid with status {}", response.statusCode());
                        return redisTemplate.opsForValue()
                                .set(cacheKey, "revoked", ttl)
                                .then(Mono.error(new RuntimeException("Auth validation failed: " + response.statusCode())));
                    }
                })
                .doOnError(err -> log.error("💥 Communication error with AuthService: {}", err.getMessage()));
    }
}
