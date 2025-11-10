package com.event.app.repositories;

import com.event.app.entities.OrganizerEntity;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizerRepository extends JpaRepository<OrganizerEntity, UUID>{
    List<OrganizerEntity> findByActiveTrue();
}
