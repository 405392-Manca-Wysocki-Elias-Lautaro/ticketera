package com.auth.app.services.domain.impl;

import java.time.OffsetDateTime;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auth.app.domain.entity.TrustedDevice;
import com.auth.app.domain.entity.User;
import com.auth.app.domain.model.UserModel;
import com.auth.app.domain.valueObjects.IpAddress;
import com.auth.app.domain.valueObjects.UserAgent;
import com.auth.app.repositories.TrustedDevicesRepository;
import com.auth.app.services.domain.TrustedDevicesService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TrustedDevicesServiceImpl implements TrustedDevicesService {

    private final TrustedDevicesRepository trustedDeviceRepository;
    private final ModelMapper modelMapper;

    @Override
    public boolean isNewDevice(UserModel user, IpAddress ipAddress, UserAgent userAgent) {

        Optional<TrustedDevice> existing = trustedDeviceRepository.findByUserIdAndIpAddressAndUserAgent(user.getId(), ipAddress, userAgent);

        if (existing.isPresent()) {
            TrustedDevice device = existing.get();
            device.setLastLogin(OffsetDateTime.now());
            trustedDeviceRepository.save(device);
            return false;
        }

        TrustedDevice newDevice = TrustedDevice.builder()
                .user(modelMapper.map(user, User.class))
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .createdAt(OffsetDateTime.now())
                .lastLogin(OffsetDateTime.now())
                .build();

        trustedDeviceRepository.save(newDevice);
        return true;
    }

    @Override
    @Transactional
    public void unregisterCurrentDevice(UserModel user, IpAddress ipAddress, UserAgent userAgent) {

        Optional<TrustedDevice> deviceOpt = trustedDeviceRepository
                .findByUserAndDeviceFingerprint(user.getId(), ipAddress, userAgent);

        if (deviceOpt.isPresent()) {
            TrustedDevice device = deviceOpt.get();
            device.setTrusted(false);
            device.setRevokedAt(OffsetDateTime.now());
            trustedDeviceRepository.save(device);

            log.info("Trusted device revoked for user {} [{} - {}]", user.getEmail(), ipAddress, userAgent);
        } else {
            log.warn("No trusted device found to revoke for user {} [{} - {}]", user.getEmail(), ipAddress, userAgent);
        }

    }

    @Override
    @Transactional
    public void unregisterAllExceptCurrent(UserModel user, IpAddress ipAddress, UserAgent userAgent) {
        int updated = trustedDeviceRepository.revokeAllExceptCurrent(
                user.getId(), ipAddress, userAgent, OffsetDateTime.now()
        );

        if (updated > 0) {
            log.info("Revoked {} trusted devices for user {} except the current one [{} - {}]",
                    updated, user.getEmail(), ipAddress, userAgent);
        } else {
            log.info("No other trusted devices found to revoke for user {} [{} - {}]",
                    user.getEmail(), ipAddress, userAgent);
        }
    }
}
