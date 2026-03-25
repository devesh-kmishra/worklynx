package com.worklynx.backend.task.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTaskRequest {

  private String title;
  private String description;

  // Optional
  private Long projectId;
  private Long assignedToId;
}
