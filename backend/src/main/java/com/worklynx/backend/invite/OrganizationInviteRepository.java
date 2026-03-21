package com.worklynx.backend.invite;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationInviteRepository extends JpaRepository<OrganizationInvite, Long> {

  Optional<OrganizationInvite> findByToken(String token);

  List<OrganizationInvite> findByOrganizationId(Long organizationId);

  List<OrganizationInvite> findByEmailAndAcceptedFalse(String email);
}
