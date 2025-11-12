package com.event.app.repositories;

import com.event.app.entities.AreaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AreaRepository extends JpaRepository<AreaEntity, UUID> {
    List<AreaEntity> findByEventId(UUID eventId);
}

