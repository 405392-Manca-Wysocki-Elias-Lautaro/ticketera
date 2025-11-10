package com.auth.app.services.domain.impl;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

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
    public boolean isNewDevice(UserModel user, UUID deviceId, IpAddress ipAddress, UserAgent userAgent) {
        Optional<TrustedDevice> existing = trustedDeviceRepository.findByUserIdAndDeviceId(user.getId(), deviceId);
        if (existing.isPresent()) {
            TrustedDevice device = existing.get();
            device.setLastLogin(OffsetDateTime.now());
            trustedDeviceRepository.save(device);
            return false;
        }
        TrustedDevice newDevice = TrustedDevice.builder()
                .user(modelMapper.map(user, User.class))
                .deviceId(deviceId)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .createdAt(OffsetDateTime.now())
                .lastLogin(OffsetDateTime.now())
                .build();
        trustedDeviceRepository.save(newDevice);
        return true;
    }

    @Override
    public void unregisterCurrentDevice(UserModel user, UUID deviceId) {
        Optional<TrustedDevice> deviceOpt = trustedDeviceRepository.findByUserIdAndDeviceId(user.getId(), deviceId);
        if (deviceOpt.isPresent()) {
            TrustedDevice device = deviceOpt.get();
            device.setTrusted(false);
            device.setRevokedAt(OffsetDateTime.now());
            trustedDeviceRepository.save(device);
            log.info("Trusted device revoked for user {} [deviceId={}]", user.getEmail(), deviceId);
        } else {
            log.warn("No trusted device found to revoke for user {} [deviceId={}]", user.getEmail(), deviceId);
        }
    }

    @Override
    public void unregisterAllExceptCurrent(UserModel user, UUID currentDeviceId) {
        int updated = trustedDeviceRepository.revokeAllExceptCurrent(user.getId(), currentDeviceId, OffsetDateTime.now());
        if (updated > 0) {
            log.info("Revoked {} trusted devices for user {} except deviceId={}", updated, user.getEmail(), currentDeviceId);
        } else {
            log.info("No other trusted devices found to revoke for user {} [deviceId={}]", user.getEmail(), currentDeviceId);
        }
    }
}
