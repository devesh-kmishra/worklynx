package com.worklynx.backend.organization;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {

  List<Organization> findByOwnerId(Long ownerId);
}
