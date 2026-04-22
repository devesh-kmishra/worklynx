package com.worklynx.backend.notification;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.worklynx.backend.notification.dto.NotificationResponse;
import com.worklynx.backend.security.UserPrincipal;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

  private final NotificationService service;

  public NotificationController(NotificationService service) {
    this.service = service;
  }

  @GetMapping
  public List<NotificationResponse> get(
      @AuthenticationPrincipal UserPrincipal principal) {
    return service.getMyNotifications(principal.getUserId());
  }

  @PostMapping("/{id}/read")
  public void markAsRead(
      @PathVariable Long id,
      @AuthenticationPrincipal UserPrincipal principal) {
    service.markAsRead(id, principal.getUserId());
  }

  @GetMapping("/unread-count")
  public long count(
      @AuthenticationPrincipal UserPrincipal principal) {
    return service.getUnreadCount(principal.getUserId());
  }
}
