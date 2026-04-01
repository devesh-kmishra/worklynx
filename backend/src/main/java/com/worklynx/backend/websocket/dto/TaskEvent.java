package com.worklynx.backend.websocket.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TaskEvent {

  private final String type; // TASK_CREATED, TASK_UPDATED
  private final Long taskId;
  private final String title;
  private final String status;
  private final Long orgId;
}
