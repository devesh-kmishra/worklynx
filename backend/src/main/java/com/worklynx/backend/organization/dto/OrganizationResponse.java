package com.worklynx.backend.organization.dto;

import java.util.List;

import com.worklynx.backend.organization.OrganizationMember;
import com.worklynx.backend.project.dto.ProjectResponse;
import com.worklynx.backend.subscription.Subscription;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrganizationResponse {

  private final Long id;
  private final String name;

  private final OrganizationMember.Role role;

  private final Subscription.Plan plan;
  private final Subscription.Status subscriptionStatus;

  private final long projectCount;
  private final int projectLimit;

  private final long memberCount;
  private final int memberLimit;

  private final long taskCount;
  private final int taskLimit;

  private final List<ProjectResponse> projects;
}
