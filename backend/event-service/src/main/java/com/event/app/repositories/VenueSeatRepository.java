package com.event.app.repositories;

import com.event.app.entities.VenueSeatEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VenueSeatRepository extends JpaRepository<VenueSeatEntity, Long> {
    List<VenueSeatEntity> findByVenueAreaId(Long venueAreaId);
    void deleteByVenueAreaId(Long venueAreaId);
}

