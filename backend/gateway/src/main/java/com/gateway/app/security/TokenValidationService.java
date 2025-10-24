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
        if (token == null || token.isBlank()) {
            return Mono.error(new RuntimeException("Empty or null token"));
        }

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
                            return Mono.error(new RuntimeException("Token revoked (Gateway cache)"));
                        }
                        return checkRemoteAndCache(token, cacheKey);
                    })
                    .switchIfEmpty(Mono.defer(() -> checkRemoteAndCache(token, cacheKey)))
                    .doOnSuccess(result -> log.info("✅ Token successfully validated (cache or remote)"))
                    .doOnError(err -> log.warn("❌ Token validation failed: {}", err.getMessage()));

        } catch (JwtException e) {
            log.error("❌ Invalid JWT: {}", e.getMessage());
            return Mono.error(new RuntimeException("Invalid JWT: " + e.getMessage()));
        } catch (Exception e) {
            log.error("💥 Unexpected error during validation: {}", e.getMessage());
            return Mono.error(e);
        }
    }

    private Mono<Boolean> checkRemoteAndCache(String token, String cacheKey) {
        log.info("🌐 Validating token remotely with AuthService...");

        return webClientBuilder.build()
                .get()
                .uri(authServiceUrl + "/validate?token=" + token)
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return redisTemplate.opsForValue()
                                .set(cacheKey, "valid", CACHE_TTL)
                                .thenReturn(true);
                    }

                    return response.bodyToMono(String.class)
                            .defaultIfEmpty("<empty>")
                            .flatMap(body -> {
                                log.warn("❌ Token invalid ({}): {}", response.statusCode(), body);
                                return redisTemplate.opsForValue()
                                        .set(cacheKey, "revoked", CACHE_TTL)
                                        .then(Mono.error(new RuntimeException("Auth validation failed: " + response.statusCode())));
                            });
                })
                .doOnError(err -> log.error("💥 Communication error with AuthService: {}", err.getMessage()));
    }
}
