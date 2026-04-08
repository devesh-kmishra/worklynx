package com.worklynx.backend.task;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.worklynx.backend.security.UserPrincipal;
import com.worklynx.backend.task.dto.CreateTaskRequest;
import com.worklynx.backend.task.dto.TaskResponse;
import com.worklynx.backend.task.dto.UpdateTaskRequest;

import jakarta.validation.Valid;

@RestController
public class TaskController {

  private final TaskService taskService;

  public TaskController(TaskService taskService) {
    this.taskService = taskService;
  }

  // Personal
  @PostMapping("/tasks")
  public TaskResponse createPersonalTask(
      @RequestBody @Valid CreateTaskRequest request,
      @AuthenticationPrincipal UserPrincipal principal) {

    return taskService.createPersonalTask(request, principal);
  }

  @GetMapping("/tasks")
  public List<TaskResponse> getPersonalTasks(
      @AuthenticationPrincipal UserPrincipal principal) {

    return taskService.getPersonalTasks(principal);
  }

  // Org
  @PostMapping("/organizations/{orgId}/tasks")
  public TaskResponse createOrgTask(
      @PathVariable Long orgId,
      @RequestBody @Valid CreateTaskRequest request,
      @AuthenticationPrincipal UserPrincipal principal) {

    return taskService.createOrgTask(orgId, request, principal);
  }

  @GetMapping("/organizations/{orgId}/tasks")
  public List<TaskResponse> getOrgTasks(
      @PathVariable Long orgId,
      @AuthenticationPrincipal UserPrincipal principal) {

    return taskService.getOrgTasks(orgId, principal);
  }

  @PatchMapping("/tasks/{taskId}")
  public TaskResponse updateTask(
      @PathVariable Long taskId,
      @RequestBody UpdateTaskRequest request,
      @AuthenticationPrincipal UserPrincipal principal) {
    return taskService.updateTask(taskId, request, principal);
  }
}
