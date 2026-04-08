package com.worklynx.backend.activity;

import org.springframework.stereotype.Service;

import com.worklynx.backend.organization.Organization;
import com.worklynx.backend.user.User;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Service
public class ActivityService {

  private final ActivityLogRepository repository;
  private final ObjectMapper objectMapper;

  public ActivityService(
      ActivityLogRepository repository,
      ObjectMapper objectMapper) {
    this.repository = repository;
    this.objectMapper = objectMapper;
  }

  public void log(
      User user,
      Organization org,
      String action,
      String entityType,
      Long entityId,
      Object metadata) {

    String metadataJson = null;

    try {
      if (metadata != null) {
        metadataJson = objectMapper.writeValueAsString(metadata);
      }
    } catch (JacksonException e) {
      metadataJson = "{}";
    }

    ActivityLog log = ActivityLog.builder().user(user).organization(org).action(action).entityType(entityType)
        .entityId(entityId).metadata(metadataJson).build();

    repository.save(log);
  }
}
