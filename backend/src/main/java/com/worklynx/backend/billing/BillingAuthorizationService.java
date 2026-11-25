package com.worklynx.backend.billing;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.worklynx.backend.common.exception.ForbiddenException;
import com.worklynx.backend.common.exception.ResourceNotFoundException;
import com.worklynx.backend.organization.Organization;
import com.worklynx.backend.organization.OrganizationRepository;

@Service
public class BillingAuthorizationService {

  private final OrganizationRepository organizationRepository;

  public BillingAuthorizationService(
      OrganizationRepository organizationRepository) {
    this.organizationRepository = organizationRepository;
  }

  @Transactional(readOnly = true)
  public Organization validateOwnership(Long organizationId, Long userId) {

    Organization organization = organizationRepository
        .findById(organizationId)
        .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));

    if (!organization.getOwner().getId().equals(userId)) {
      throw new ForbiddenException("Only the organization owner can manage billing.");
    }

    return organization;
  }
}
