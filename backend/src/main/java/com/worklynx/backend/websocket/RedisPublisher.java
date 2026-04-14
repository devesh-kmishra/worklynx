package com.worklynx.backend.websocket;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Component
public class RedisPublisher {

  private final StringRedisTemplate redisTemplate;
  private final ObjectMapper objectMapper;

  public RedisPublisher(
      StringRedisTemplate redisTemplate,
      ObjectMapper objectMapper) {
    this.redisTemplate = redisTemplate;
    this.objectMapper = objectMapper;
  }

  public void publish(Object event) {

    if (event == null)
      return;

    try {
      String message = objectMapper.writeValueAsString(event);

      redisTemplate.convertAndSend("task-events", message);
    } catch (JacksonException e) {
      throw new RuntimeException("Redis publish failed: " + e.getMessage());
    }
  }
}
