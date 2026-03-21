package com.worklynx.backend.organization;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.worklynx.backend.organization.dto.CreateOrganizationRequest;
import com.worklynx.backend.organization.dto.OrganizationResponse;
import com.worklynx.backend.security.UserPrincipal;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/organizations")
public class OrganizationController {

  private final OrganizationService organizationService;

  public OrganizationController(OrganizationService organizationService) {
    this.organizationService = organizationService;
  }

  @PostMapping
  public OrganizationResponse createOrganization(
      @RequestBody @Valid CreateOrganizationRequest request, @AuthenticationPrincipal UserPrincipal principal) {
    return organizationService.createOrganization(request, principal);
  }

  @GetMapping("/my")
  public List<OrganizationResponse> getMyOrganizations(
      @AuthenticationPrincipal UserPrincipal principal) {
    return organizationService.getMyOrganizations(principal);
  }
}
