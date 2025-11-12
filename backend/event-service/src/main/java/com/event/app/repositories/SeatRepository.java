package com.event.app.repositories;

import com.event.app.entities.SeatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SeatRepository extends JpaRepository<SeatEntity, UUID> {
    List<SeatEntity> findByAreaId(UUID areaId);
    void deleteByAreaId(UUID areaId);
}

