package com.worklynx.backend.task.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TaskResponse {

  private final Long id;
  private final String title;
  private final String description;
  private final String status;

  private final Long projectId;
  private final Long assignedToId;
}
