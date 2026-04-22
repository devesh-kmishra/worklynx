package com.worklynx.backend.notification;

import org.springframework.stereotype.Component;

import com.worklynx.backend.notification.dto.NotificationEvent;
import com.worklynx.backend.websocket.RedisPublisher;

@Component
public class NotificationEventPublisher {

  private final RedisPublisher redisPublisher;

  public NotificationEventPublisher(RedisPublisher redisPublisher) {
    this.redisPublisher = redisPublisher;
  }

  public void publish(Notification notification) {

    NotificationEvent event = NotificationEvent.builder().type("NOTIFICATION_CREATED")
        .userId(notification.getUser().getId()).message(notification.getMessage()).build();

    redisPublisher.publish(event);
  }
}
