package com.event.app.repositories;

import com.event.app.entities.VenueEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VenueRepository extends JpaRepository<VenueEntity, Long>{
    List<VenueEntity> findByActiveTrue();
    List<VenueEntity> findByOrganizerIdAndActiveTrue(Long organizerId);
}

