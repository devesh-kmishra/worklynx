package com.worklynx.backend.activity;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.worklynx.backend.activity.dto.ActivityResponse;
import com.worklynx.backend.organization.OrganizationAccessService;
import com.worklynx.backend.security.UserPrincipal;

@RestController
@RequestMapping("/organizations/{orgId}/activities")
public class ActivityController {

  private final ActivityLogRepository repository;
  private final OrganizationAccessService accessService;

  public ActivityController(
      ActivityLogRepository repository,
      OrganizationAccessService accessService) {
    this.repository = repository;
    this.accessService = accessService;
  }

  @GetMapping
  public List<ActivityResponse> getActivities(
      @PathVariable Long orgId,
      @AuthenticationPrincipal UserPrincipal principal) {

    accessService.validateMembership(principal.getUserId(), orgId);

    return repository.findByOrganizationIdOrderByCreatedAtDesc(orgId).stream()
        .map(log -> ActivityResponse.builder().action(log.getAction()).entityType(log.getEntityType())
            .entityId(log.getEntityId()).metadata(log.getMetadata()).userName(log.getUser().getName())
            .createdAt(log.getCreatedAt().toString()).build())
        .toList();
  }
}
