package com.worklynx.backend.task.dto;

import java.time.Instant;
import java.time.LocalDate;

import com.worklynx.backend.task.TaskPriority;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TaskResponse {

  private final Long id;
  private final String title;
  private final String description;
  private final String status;
  private final TaskPriority priority;
  private final LocalDate dueDate;

  private final Instant createdAt;
  private final Instant updatedAt;

  private final Long projectId;
  private final Long assignedToId;
}
