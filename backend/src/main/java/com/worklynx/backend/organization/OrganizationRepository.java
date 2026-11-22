package com.worklynx.backend.organization;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {

  List<Organization> findByOwnerId(Long ownerId);

  boolean existsByOwnerId(Long ownerId);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT o FROM Organization o WHERE o.id = :orgId")
  Optional<Organization> findByIdForUpdate(@Param("orgId") Long orgId);
}
