package com.worklynx.backend.comment;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.worklynx.backend.comment.dto.CommentResponse;
import com.worklynx.backend.comment.dto.CreateCommentRequest;
import com.worklynx.backend.security.UserPrincipal;

@RestController
@RequestMapping("/tasks/{taskId}/comments")
public class CommentController {

  private final CommentService service;

  public CommentController(CommentService service) {
    this.service = service;
  }

  @PostMapping
  public CommentResponse create(
      @PathVariable Long taskId,
      @RequestBody CreateCommentRequest request,
      @AuthenticationPrincipal UserPrincipal principal) {
    return service.createComment(taskId, request.getContent(), principal);
  }

  @GetMapping
  public List<CommentResponse> get(
      @PathVariable Long taskId,
      @AuthenticationPrincipal UserPrincipal principal) {
    return service.getComments(taskId, principal);
  }
}
