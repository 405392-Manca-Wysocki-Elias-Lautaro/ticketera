package com.auth.app.repositories;

import com.auth.app.domain.entity.RefreshToken;
import com.auth.app.domain.entity.Role;
import com.auth.app.domain.entity.User;
import com.auth.app.domain.enums.RoleCode;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RefreshTokenRepositoryIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void shouldRevokeTokenByHash() {
        Role role = new Role();
        role.setCode(RoleCode.TEST_USER);

        role.setName("Test User");

        roleRepository.save(role);

        User user = new User();

        user.setFirstName("Test");

        user.setLastName("User");

        user.setEmail("test@example.com");

        user.setPasswordHash("password");

        user.setRole(role);

        userRepository.save(user);

        RefreshToken token = RefreshToken.builder()
                .user(user)
                .deviceId(UUID.randomUUID())
                .tokenHash("test-token-hash")
                .expiresAt(OffsetDateTime.now().plusDays(1))
                .build();

        refreshTokenRepository.save(token);

        refreshTokenRepository.revokeByTokenHash("test-token-hash");

        RefreshToken revokedToken = refreshTokenRepository.findByTokenHash("test-token-hash").orElseThrow();

        assertTrue(revokedToken.isRevoked());

    }

    @Test

    void shouldRevokeAllByUserId() {

        Role role = new Role();

        role.setCode(RoleCode.TEST_ADMIN);

        role.setName("Test Admin");

        roleRepository.save(role);

        User user = new User();

        user.setFirstName("Test");

        user.setLastName("User");

        user.setEmail("test2@example.com");

        user.setPasswordHash("password");

        user.setRole(role);

        userRepository.save(user);

        RefreshToken token1 = RefreshToken.builder()
                .user(user)
                .deviceId(UUID.randomUUID())
                .tokenHash("test-token-hash-1")
                .expiresAt(OffsetDateTime.now().plusDays(1))
                .build();

        refreshTokenRepository.save(token1);

        RefreshToken token2 = RefreshToken.builder()
                .user(user)
                .deviceId(UUID.randomUUID())
                .tokenHash("test-token-hash-2")
                .expiresAt(OffsetDateTime.now().plusDays(1))
                .build();

        refreshTokenRepository.save(token2);

        refreshTokenRepository.revokeAllByUserId(user.getId());

        RefreshToken revokedToken1 = refreshTokenRepository.findByTokenHash("test-token-hash-1").orElseThrow();

        assertTrue(revokedToken1.isRevoked());

        RefreshToken revokedToken2 = refreshTokenRepository.findByTokenHash("test-token-hash-2").orElseThrow();

        assertTrue(revokedToken2.isRevoked());

    }

    @Test

    void shouldRevokeByDeviceId() {

        Role role = new Role();

        role.setCode(RoleCode.TEST_STAFF);

        role.setName("Test Staff");

        roleRepository.save(role);

        User user = new User();

        user.setFirstName("Test");

        user.setLastName("User");

        user.setEmail("test3@example.com");

        user.setPasswordHash("password");

        user.setRole(role);

        userRepository.save(user);

        UUID deviceId = UUID.randomUUID();

        RefreshToken token = RefreshToken.builder()
                .user(user)
                .deviceId(deviceId)
                .tokenHash("test-token-hash-3")
                .expiresAt(OffsetDateTime.now().plusDays(1))
                .build();

        refreshTokenRepository.save(token);

        refreshTokenRepository.revokeByDeviceId(user.getId(), deviceId);

        RefreshToken revokedToken = refreshTokenRepository.findByTokenHash("test-token-hash-3").orElseThrow();

        assertTrue(revokedToken.isRevoked());

    }

}
