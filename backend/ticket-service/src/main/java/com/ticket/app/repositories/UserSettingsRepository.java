package com.ticket.app.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ticket.app.domain.entities.UserSettings;

@Repository
public interface UserSettingsRepository extends JpaRepository<UserSettings, UUID> {
    Optional<UserSettings> findByUserIdAndSettingKey(UUID userId, String settingKey);
    List<UserSettings> findByUserId(UUID userId);
}
