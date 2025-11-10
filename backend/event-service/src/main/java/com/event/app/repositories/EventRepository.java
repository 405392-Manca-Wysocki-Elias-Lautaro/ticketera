package com.event.app.repositories;

import com.event.app.entities.EventEntity;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, UUID>{
    List<EventEntity> findByActiveTrue();
    List<EventEntity> findByOrganizerIdAndActiveTrue(UUID organizerId);
    List<EventEntity> findByCategoryIdAndActiveTrue(UUID categoryId);
    List<EventEntity> findByStatusAndActiveTrue(String status);
}
