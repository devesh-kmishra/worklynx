package com.worklynx.backend.task.dto;

import java.time.LocalDate;

import com.worklynx.backend.task.Task;
import com.worklynx.backend.task.TaskPriority;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTaskRequest {

  @NotBlank
  @Size(max = 200)
  private String title;

  @Size(max = 5000)
  private String description;

  private Task.Status status;
  private TaskPriority priority;
  private LocalDate dueDate;

  // Optional
  private Long projectId;
  private Long assignedToId;
}
