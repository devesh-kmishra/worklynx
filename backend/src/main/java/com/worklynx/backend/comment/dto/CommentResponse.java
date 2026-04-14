package com.worklynx.backend.comment.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentResponse {

  private final Long id;
  private final String content;
  private final String userName;
  private final String createdAt;
}
