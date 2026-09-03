package com.worklynx.backend.task.dto;

import java.time.LocalDate;

import com.worklynx.backend.task.TaskPriority;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTaskRequest {

  private String title;
  private String description;
  private String status;
  private TaskPriority priority;
  private LocalDate dueDate;
  private Long assignedToId;
}
