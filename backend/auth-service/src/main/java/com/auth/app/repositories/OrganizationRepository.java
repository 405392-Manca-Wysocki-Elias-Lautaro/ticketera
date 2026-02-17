package com.auth.app.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.auth.app.domain.entity.Organization;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, UUID> {
}
