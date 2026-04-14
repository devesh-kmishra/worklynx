package com.worklynx.backend.websocket;

import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
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
      JsonNode node = objectMapper.readTree(message);

      String type = node.get("type").asString();
      Long orgId = node.get("orgId").asLong();

      switch (type) {
        case "TASK_CREATED", "TASK_UPDATED", "COMMENT_CREATED" ->
          messagingTemplate.convertAndSend("/topic/org/" + orgId, message);
        default -> {
        }
      }
    } catch (MessagingException | JacksonException e) {
      throw new RuntimeException("Redis subscribe failed: " + e.getMessage());
    }
  }
}
