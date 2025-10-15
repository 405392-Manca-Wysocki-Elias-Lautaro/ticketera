package com.event.app.repositories;

import com.event.app.entities.VenueSeatEntity;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VenueSeatRepository extends JpaRepository<VenueSeatEntity, UUID> {
    List<VenueSeatEntity> findByVenueAreaId(UUID venueAreaId);
    void deleteByVenueAreaId(UUID venueAreaId);
}

