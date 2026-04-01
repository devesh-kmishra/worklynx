package com.worklynx.backend.websocket;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.worklynx.backend.task.Task;
import com.worklynx.backend.websocket.dto.TaskEvent;

@Component
public class TaskEventPublisher {

  private final SimpMessagingTemplate messagingTemplate;

  public TaskEventPublisher(SimpMessagingTemplate messagingTemplate) {
    this.messagingTemplate = messagingTemplate;
  }

  public void publishTaskCreated(Task task) {

    if (task.getOrganization() == null)
      return;

    TaskEvent event = TaskEvent.builder().type("TASK_CREATED").taskId(task.getId()).title(task.getTitle())
        .status(task.getStatus().name()).orgId(task.getOrganization().getId()).build();

    messagingTemplate.convertAndSend("/topic/org/" + task.getOrganization().getId(), event);
  }

  public void publishTaskUpdated(Task task) {

    if (task.getOrganization() == null)
      return;

    TaskEvent event = TaskEvent.builder().type("TASK_UPDATED").taskId(task.getId()).title(task.getTitle())
        .status(task.getStatus().name()).orgId(task.getOrganization().getId()).build();

    messagingTemplate.convertAndSend("/topic/org/" + task.getOrganization().getId(), event);
  }
}
