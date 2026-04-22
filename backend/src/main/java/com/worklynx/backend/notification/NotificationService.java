package com.worklynx.backend.notification;

import java.util.List;

import org.springframework.stereotype.Service;

import com.worklynx.backend.common.exception.ResourceNotFoundException;
import com.worklynx.backend.notification.dto.NotificationResponse;
import com.worklynx.backend.user.User;

@Service
public class NotificationService {

  private final NotificationRepository repository;
  private final NotificationEventPublisher eventPublisher;

  public NotificationService(
      NotificationRepository repository,
      NotificationEventPublisher eventPublisher) {
    this.repository = repository;
    this.eventPublisher = eventPublisher;
  }

  public void notify(
      User user, String type, String message, Long entityId) {

    Notification notification = Notification.builder().user(user).type(type).message(message).entityId(entityId)
        .build();

    repository.save(notification);

    eventPublisher.publish(notification);
  }

  public List<NotificationResponse> getMyNotifications(Long userId) {

    return repository
        .findByUserIdOrderByCreatedAtDesc(userId).stream().map(n -> NotificationResponse.builder().id(n.getId())
            .type(n.getType()).message(n.getMessage()).read(n.isRead()).createdAt(n.getCreatedAt().toString()).build())
        .toList();
  }

  public void markAsRead(Long notificationId, Long userId) {

    Notification n = repository.findById(notificationId)
        .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

    if (!n.getUser().getId().equals(userId)) {
      throw new RuntimeException("Forbidden");
    }

    n.setRead(true);
    repository.save(n);
  }

  public long getUnreadCount(Long userId) {
    return repository.countByUserIdAndReadFalse(userId);
  }
}
