package com.worklynx.backend.websocket;

import org.springframework.stereotype.Component;

import com.worklynx.backend.task.Task;
import com.worklynx.backend.websocket.dto.TaskEvent;

@Component
public class TaskEventPublisher {

  private final RedisPublisher redisPublisher;

  public TaskEventPublisher(RedisPublisher redisPublisher) {
    this.redisPublisher = redisPublisher;
  }

  public void publishTaskCreated(Task task) {

    if (task.getOrganization() == null)
      return;

    TaskEvent event = TaskEvent.builder().type("TASK_CREATED").taskId(task.getId()).title(task.getTitle())
        .status(task.getStatus().name()).orgId(task.getOrganization().getId()).build();

    redisPublisher.publish(event);
  }

  public void publishTaskUpdated(Task task) {

    if (task.getOrganization() == null)
      return;

    TaskEvent event = TaskEvent.builder().type("TASK_UPDATED").taskId(task.getId()).title(task.getTitle())
        .status(task.getStatus().name()).orgId(task.getOrganization().getId()).build();

    redisPublisher.publish(event);
  }
}
