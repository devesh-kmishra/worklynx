package com.worklynx.backend.task.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTaskRequest {

  private String title;
  private String description;
  private String status;
  private Long assignedToId;
}
