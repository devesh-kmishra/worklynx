package com.worklynx.backend.task.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskFilterRequest {

  private String status;
  private String assignedTo;
}
