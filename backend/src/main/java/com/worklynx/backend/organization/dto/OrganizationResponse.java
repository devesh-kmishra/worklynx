package com.worklynx.backend.organization.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrganizationResponse {

  private final Long id;
  private final String name;
}
