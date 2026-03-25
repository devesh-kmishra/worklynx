package com.worklynx.backend.project.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProjectResponse {

  private final Long id;
  private final String name;
}
