package com.worklynx.backend.activity.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ActivityResponse {

  private final String action;
  private final String entityType;
  private final Long entityId;
  private final String metadata;
  private final String userName;
  private final String createdAt;
}
