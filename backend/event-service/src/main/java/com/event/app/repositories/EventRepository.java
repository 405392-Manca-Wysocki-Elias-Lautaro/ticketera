package com.event.app.repositories;

import com.event.app.entities.EventEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Long>{
    List<EventEntity> findByActiveTrue();
    List<EventEntity> findByOrganizerIdAndActiveTrue(Long organizerId);
    List<EventEntity> findByCategoryIdAndActiveTrue(Long categoryId);
    List<EventEntity> findByStatusAndActiveTrue(String status);
}
