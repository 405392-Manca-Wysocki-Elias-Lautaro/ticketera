package com.auth.app.services.domain.impl;

import java.time.OffsetDateTime;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auth.app.domain.entity.TrustedDevice;
import com.auth.app.domain.entity.User;
import com.auth.app.domain.model.UserModel;
import com.auth.app.repositories.TrustedDevicesRepository;
import com.auth.app.services.domain.TrustedDevicesService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class TrustedDevicesServiceImpl implements TrustedDevicesService {

    private final TrustedDevicesRepository trustedDeviceRepository;
    private final ModelMapper modelMapper;

    @Override
    public boolean isNewDevice(UserModel user, String ipAddress, String userAgent) {

        Optional<TrustedDevice> existing = trustedDeviceRepository.findByUserIdAndUserAgentAndIpAddress(user.getId(), userAgent, ipAddress);

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

}
