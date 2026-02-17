package com.event.app.repositories;

import com.event.app.entities.EventStaffEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventStaffRepository extends JpaRepository<EventStaffEntity, UUID> {
    List<EventStaffEntity> findByEventId(UUID eventId);
    List<EventStaffEntity> findByUserId(UUID userId);
    Optional<EventStaffEntity> findByEventIdAndUserId(UUID eventId, UUID userId);
    void deleteByEventIdAndUserId(UUID eventId, UUID userId);
}
