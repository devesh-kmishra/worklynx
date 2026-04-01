package com.worklynx.backend.organization;

import org.springframework.stereotype.Service;

import com.worklynx.backend.common.exception.ForbiddenException;
import com.worklynx.backend.common.exception.ResourceNotFoundException;

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
      throw new ForbiddenException("User does not belong to this organization");
    }
  }

  public OrganizationMember getMembership(Long userId, Long orgId) {

    return memberRepository.findByUserIdAndOrganizationId(userId, orgId)
        .orElseThrow(() -> new ResourceNotFoundException("Membership not found"));
  }

  public void requireOwner(Long userId, Long orgId) {

    OrganizationMember member = getMembership(userId, orgId);

    if (member.getRole() != OrganizationMember.Role.OWNER) {
      throw new ForbiddenException("Only owner allowed");
    }
  }
}
