package com.worklynx.backend.comment;

import org.springframework.stereotype.Component;

import com.worklynx.backend.comment.dto.CommentEvent;
import com.worklynx.backend.websocket.RedisPublisher;

@Component
public class CommentEventPublisher {

  private final RedisPublisher redisPublisher;

  public CommentEventPublisher(RedisPublisher redisPublisher) {
    this.redisPublisher = redisPublisher;
  }

  public void publishComment(Comment comment) {

    Long orgId = comment.getTask().getOrganization() != null ? comment.getTask().getOrganization().getId() : null;

    if (orgId == null)
      return;

    CommentEvent event = CommentEvent.builder().type("COMMENT_CREATED").taskId(comment.getTask().getId())
        .content(comment.getContent()).userName(comment.getUser().getName()).orgId(orgId).build();

    redisPublisher.publish(event);
  }
}
