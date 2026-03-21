package com.worklynx.backend.organization;

import org.springframework.stereotype.Service;

@Service
public class OrganizationAccessService {

  private final OrganizationMemberRepository memberRepository;

  public OrganizationAccessService(OrganizationMemberRepository memberRepository) {
    this.memberRepository = memberRepository;
  }

  // Validate user belongs to organization
  public void validateMembership(Long userId, Long orgId) {

    boolean exists = memberRepository.existsByUserIdAndOrganizationId(userId, orgId);

    if (!exists) {
      throw new RuntimeException("User does not belong to this organization");
    }
  }

  public OrganizationMember getMembership(Long userId, Long orgId) {

    return memberRepository.findByUserIdAndOrganizationId(userId, orgId)
        .orElseThrow(() -> new RuntimeException("Membership not found"));
  }

  public void requireAdminOrOwner(Long userId, Long orgId) {

    OrganizationMember member = getMembership(userId, orgId);

    if (member.getRole() != OrganizationMember.Role.ADMIN && member.getRole() != OrganizationMember.Role.OWNER) {

      throw new RuntimeException("Insufficient permissions");
    }
  }

  public void requireOwner(Long userId, Long orgId) {

    OrganizationMember member = getMembership(userId, orgId);

    if (member.getRole() != OrganizationMember.Role.OWNER) {
      throw new RuntimeException("Only owner allowed");
    }
  }
}
