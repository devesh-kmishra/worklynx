package com.worklynx.backend.organization;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.worklynx.backend.user.User;

public interface OrganizationMemberRepository extends JpaRepository<OrganizationMember, Long> {

  Optional<OrganizationMember> findByUserIdAndOrganizationId(Long userId, Long organizationId);

  // reminder: Remove if not in use
  List<OrganizationMember> findByUser(User user);

  List<OrganizationMember> findByUserId(Long userId);

  List<OrganizationMember> findByOrganizationId(Long organizationId);

  boolean existsByUserIdAndOrganizationId(Long userId, Long organizationId);

  long countByOrganizationId(Long orgId);
}
