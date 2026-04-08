package com.worklynx.backend.websocket;

import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.worklynx.backend.websocket.dto.TaskEvent;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Component
public class RedisSubscriber {

  private final SimpMessagingTemplate messagingTemplate;
  private final ObjectMapper objectMapper;

  public RedisSubscriber(
      SimpMessagingTemplate messagingTemplate,
      ObjectMapper objectMapper) {
    this.messagingTemplate = messagingTemplate;
    this.objectMapper = objectMapper;
  }

  public void onMessage(String message) {

    try {
      TaskEvent event = objectMapper.readValue(message, TaskEvent.class);

      messagingTemplate.convertAndSend("/topic/org/" + event.getOrgId(), event);
    } catch (MessagingException | JacksonException e) {
      throw new RuntimeException("Redis subscribe failed: " + e.getMessage());
    }
  }
}
