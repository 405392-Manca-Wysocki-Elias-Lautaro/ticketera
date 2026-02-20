package com.event.app.repositories;

import com.event.app.entities.EventEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, UUID>{
    List<EventEntity> findByActiveTrue();
    List<EventEntity> findByActiveTrueAndEndsAtAfter(LocalDateTime now);
    List<EventEntity> findByOrganizerIdAndActiveTrue(UUID organizerId);
    List<EventEntity> findByCategoryIdAndActiveTrue(UUID categoryId);
    List<EventEntity> findByStatusAndActiveTrue(String status);
    List<EventEntity> findByTitleContainingIgnoreCaseAndActiveTrue(String title);
    List<EventEntity> findByTitleContainingIgnoreCaseAndActiveTrueAndEndsAtAfter(String title, LocalDateTime now);
    List<EventEntity> findAllByStartsAtBetween(LocalDateTime start, LocalDateTime end);
}
