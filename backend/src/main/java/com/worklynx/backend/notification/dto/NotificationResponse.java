package com.worklynx.backend.notification.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationResponse {

  private final Long id;
  private final String type;
  private final String message;
  private final boolean read;
  private final String createdAt;
}
