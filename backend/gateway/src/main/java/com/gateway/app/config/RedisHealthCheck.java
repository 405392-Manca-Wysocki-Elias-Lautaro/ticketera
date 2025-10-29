package com.gateway.app.config;

import java.time.Duration;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisHealthCheck {

    private final ReactiveRedisTemplate<String, String> redisTemplate;

    @PostConstruct
    public void checkRedis() {
        redisTemplate.opsForValue().set("gateway:test", "ok", Duration.ofMinutes(2))
            .then(redisTemplate.opsForValue().get("gateway:test"))
            .subscribe(
                value -> log.info("[REDIS HEALTH] ✅ Redis respondió correctamente con '{}'", value),
                error -> log.error("[REDIS HEALTH] ❌ Error accediendo a Redis: {}", error.getMessage())
            );
    }
}
