package com.worklynx.backend.comment;

import java.util.List;

import org.springframework.stereotype.Service;

import com.worklynx.backend.comment.dto.CommentResponse;
import com.worklynx.backend.common.exception.ForbiddenException;
import com.worklynx.backend.common.exception.ResourceNotFoundException;
import com.worklynx.backend.notification.NotificationService;
import com.worklynx.backend.organization.OrganizationAccessService;
import com.worklynx.backend.security.UserPrincipal;
import com.worklynx.backend.task.Task;
import com.worklynx.backend.task.TaskRepository;
import com.worklynx.backend.user.User;
import com.worklynx.backend.user.UserRepository;

@Service
public class CommentService {

  private final CommentRepository commentRepository;
  private final TaskRepository taskRepository;
  private final UserRepository userRepository;
  private final OrganizationAccessService accessService;
  private final CommentEventPublisher eventPublisher;
  private final NotificationService notificationService;

  public CommentService(
      CommentRepository commentRepository,
      TaskRepository taskRepository,
      UserRepository userRepository,
      OrganizationAccessService accessService,
      CommentEventPublisher eventPublisher,
      NotificationService notificationService) {
    this.commentRepository = commentRepository;
    this.taskRepository = taskRepository;
    this.userRepository = userRepository;
    this.accessService = accessService;
    this.eventPublisher = eventPublisher;
    this.notificationService = notificationService;
  }

  public CommentResponse createComment(
      Long taskId, String content, UserPrincipal principal) {

    Task task = taskRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Task not found"));

    Long userId = principal.getUserId();

    if (task.getOrganization() != null) {
      accessService.validateMembership(userId, task.getOrganization().getId());
    } else {
      if (!task.getCreatedBy().getId().equals(userId)) {
        throw new ForbiddenException("Not allowed");
      }
    }

    User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

    Comment comment = Comment.builder().task(task).user(user).content(content).build();

    commentRepository.save(comment);

    if (!task.getCreatedBy().getId().equals(userId)) {
      notificationService.notify(task.getCreatedBy(),
          "COMMENT_CREATED", user.getName() + " commented on your task", task.getId());
    }

    eventPublisher.publishComment(comment);

    return map(comment);
  }

  public List<CommentResponse> getComments(Long taskId, UserPrincipal principal) {

    Task task = taskRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Task not found"));

    Long userId = principal.getUserId();

    if (task.getOrganization() != null) {
      accessService.validateMembership(userId, task.getOrganization().getId());
    }

    return commentRepository.findByTaskIdOrderByCreatedAtAsc(taskId).stream().map(this::map).toList();
  }

  private CommentResponse map(Comment c) {
    return CommentResponse.builder().id(c.getId()).content(c.getContent()).userName(c.getUser().getName())
        .createdAt(c.getCreatedAt().toString()).build();
  }
}
