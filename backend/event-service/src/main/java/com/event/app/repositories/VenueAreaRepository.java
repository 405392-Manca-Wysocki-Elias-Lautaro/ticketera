package com.event.app.repositories;

import com.event.app.entities.VenueAreaEntity;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VenueAreaRepository extends JpaRepository<VenueAreaEntity, UUID> {
    List<VenueAreaEntity> findByVenueId(UUID venueId);
    List<VenueAreaEntity> findByVenueIdOrderByPositionAsc(UUID venueId);
}

