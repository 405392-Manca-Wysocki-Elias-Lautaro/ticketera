package com.event.app.repositories;

import com.event.app.entities.AreaPricingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AreaPricingRepository extends JpaRepository<AreaPricingEntity, UUID> {
    Optional<AreaPricingEntity> findByAreaId(UUID areaId);
    
    @Query("SELECT ap FROM AreaPricingEntity ap WHERE ap.areaId IN " +
           "(SELECT a.id FROM AreaEntity a WHERE a.eventId = :eventId)")
    List<AreaPricingEntity> findAllByEventId(UUID eventId);
}

