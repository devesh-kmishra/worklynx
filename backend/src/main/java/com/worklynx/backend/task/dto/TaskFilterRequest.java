package com.worklynx.backend.task.dto;

import com.worklynx.backend.task.TaskSort;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskFilterRequest {

  private String status;
  private String assignedTo;
  private TaskSort sort = TaskSort.RECENTLY_CREATED;
}
