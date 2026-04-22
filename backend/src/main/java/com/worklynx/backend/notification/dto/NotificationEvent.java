package com.worklynx.backend.notification.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationEvent {

  private final String type;
  private final Long userId;
  private final String message;
}
