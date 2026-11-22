package com.worklynx.backend.invite;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

public interface OrganizationInviteRepository extends JpaRepository<OrganizationInvite, Long> {

  Optional<OrganizationInvite> findByToken(String token);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT i FROM OrganizationInvite i WHERE i.token = :token")
  Optional<OrganizationInvite> findByTokenForUpdate(@Param("token") String token);

  List<OrganizationInvite> findByOrganizationId(Long organizationId);

  List<OrganizationInvite> findByEmailIgnoreCaseAndAcceptedFalse(String email);

  boolean existsByOrganizationIdAndEmailIgnoreCaseAndAcceptedFalseAndExpiresAtAfter(
      Long organizationId,
      String email,
      Instant now);

  long countByOrganizationIdAndAcceptedFalseAndExpiresAtAfter(
      Long organizationId,
      Instant now);

  long countByOrganizationIdAndAcceptedFalseAndExpiresAtAfterAndIdNot(
      Long organizationId,
      Instant now,
      Long excludedInviteId);
}
