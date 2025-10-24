package com.gateway.app.security;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

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

    private static final Duration CACHE_TTL = Duration.ofMinutes(5);

    public Mono<Boolean> validateToken(String token) {
        try {
            var claims = jwtUtils.validateAndParseClaims(token);

            if (jwtUtils.isTokenExpired(claims)) {
                return Mono.error(new RuntimeException("Token expired"));
            }

            String jti = jwtUtils.getJti(claims);
            if (jti == null) {
                jti = token.substring(0, Math.min(token.length(), 10));
            }

            String cacheKey = "token:" + jti;

            return redisTemplate.opsForValue().get(cacheKey)
                    .flatMap(status -> {
                        if ("valid".equals(status)) {
                            return Mono.just(true);
                        }
                        if ("revoked".equals(status)) {
                            return Mono.error(new RuntimeException("Token revoked Gateway"));
                        }
                        return checkRemoteAndCache(token, cacheKey);
                    })
                    .switchIfEmpty(checkRemoteAndCache(token, cacheKey));

        } catch (JwtException e) {
            return Mono.error(new RuntimeException("Invalid JWT: " + e.getMessage()));
        }
    }

    private Mono<Boolean> checkRemoteAndCache(String token, String cacheKey) {

        return webClientBuilder.build()
                .get()
                .uri(authServiceUrl + "/api/auth/validate?token=" + token)
                .exchangeToMono(response -> {
                    log.info("[AUTH VALIDATE] Status: {}", response.statusCode());

                    // Log headers
                    response.headers().asHttpHeaders()
                            .forEach((k, v) -> log.debug("[AUTH VALIDATE] Header {} = {}", k, v));

                    if (response.statusCode().is2xxSuccessful()) {
                        log.info("[AUTH VALIDATE] Token OK (200)");
                        return redisTemplate.opsForValue()
                                .set(cacheKey, "valid", CACHE_TTL)
                                .thenReturn(true);
                    }

                    // Loguear body de error
                    return response.bodyToMono(String.class)
                            .defaultIfEmpty("<empty>")
                            .flatMap(body -> {
                                log.error("[AUTH VALIDATE] Error {} - Body: {}", response.statusCode(), body);
                                return redisTemplate.opsForValue()
                                        .set(cacheKey, "revoked", CACHE_TTL)
                                        .then(Mono.error(new RuntimeException("Auth validation failed: " + response.statusCode())));
                            });
                });
    }

}
