package com.event.app.repositories;

import com.event.app.entities.CategoryEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, UUID>{
    List<CategoryEntity> findByActiveTrue();
    Optional<CategoryEntity> findByNameAndActiveTrue(String name);
}

