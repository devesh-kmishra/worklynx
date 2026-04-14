package com.worklynx.backend.comment.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentEvent {

  private final String type;
  private final Long taskId;
  private final String content;
  private final String userName;
  private final Long orgId;
}
