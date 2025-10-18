package com.gateway.app.security;

import java.time.Duration;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TokenValidationService {

    private final JwtUtils jwtUtils;
    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final WebClient.Builder webClientBuilder;

    private static final Duration CACHE_TTL = Duration.ofMinutes(5);

    public Mono<Boolean> validateToken(String token) {
        try {
            var claims = jwtUtils.validateAndParseClaims(token);

            if (jwtUtils.isTokenExpired(claims))
                return Mono.error(new RuntimeException("Token expired"));

            String jti = jwtUtils.getJti(claims);
            if (jti == null) jti = token.substring(0, Math.min(token.length(), 10));

            String cacheKey = "token:" + jti;

            return redisTemplate.opsForValue().get(cacheKey)
                    .flatMap(status -> {
                        if ("valid".equals(status)) return Mono.just(true);
                        if ("revoked".equals(status)) return Mono.error(new RuntimeException("Token revoked"));
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
                .uri("http://auth:8080/api/auth/validate?token=" + token)
                .retrieve()
                .onStatus(status -> status.isError(),
                        response -> Mono.error(new RuntimeException("Auth validation failed")))
                .toBodilessEntity()
                .flatMap(res -> redisTemplate.opsForValue().set(cacheKey, "valid", CACHE_TTL))
                .thenReturn(true)
                .onErrorResume(ex ->
                        redisTemplate.opsForValue().set(cacheKey, "revoked", CACHE_TTL)
                                .then(Mono.error(ex))
                );
    }
}
