package com.event.app.repositories;

import com.event.app.entities.OccurrenceEntity;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OccurrenceRepository extends JpaRepository<OccurrenceEntity, UUID>{
    List<OccurrenceEntity> findByActiveTrue();
    List<OccurrenceEntity> findByEventIdAndActiveTrue(UUID eventId);
    List<OccurrenceEntity> findByVenueIdAndActiveTrue(UUID venueId);
    List<OccurrenceEntity> findByStatusAndActiveTrue(String status);
}

