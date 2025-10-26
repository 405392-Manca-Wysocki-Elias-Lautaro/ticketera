package com.auth.app.repositories;

import com.auth.app.domain.entity.Role;
import com.auth.app.domain.entity.TrustedDevice;
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
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TrustedDevicesRepositoryIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private TrustedDevicesRepository trustedDevicesRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void shouldRevokeAllExceptCurrent() {
        Role role = new Role();
        role.setCode(RoleCode.TEST_OWNER);
        role.setName("Test Owner");
        roleRepository.save(role);

        User user = new User();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail("test4@example.com");
        user.setPasswordHash("password");
        user.setRole(role);
        userRepository.save(user);

        UUID currentDeviceId = UUID.randomUUID();
        TrustedDevice device1 = TrustedDevice.builder()
                .user(user)
                .deviceId(currentDeviceId)
                .build();
        trustedDevicesRepository.save(device1);

        TrustedDevice device2 = TrustedDevice.builder()
                .user(user)
                .deviceId(UUID.randomUUID())
                .build();
        trustedDevicesRepository.save(device2);

        trustedDevicesRepository.revokeAllExceptCurrent(user.getId(), currentDeviceId, OffsetDateTime.now());

        TrustedDevice revokedDevice = trustedDevicesRepository.findByUserIdAndDeviceId(user.getId(), device2.getDeviceId()).orElseThrow();
        assertFalse(revokedDevice.isTrusted());

        TrustedDevice currentDevice = trustedDevicesRepository.findByUserIdAndDeviceId(user.getId(), currentDeviceId).orElseThrow();
        assertTrue(currentDevice.isTrusted());
    }
}
