package com.worklynx.backend.task;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.worklynx.backend.activity.ActivityService;
import com.worklynx.backend.common.dto.PagedResponse;
import com.worklynx.backend.common.exception.BadRequestException;
import com.worklynx.backend.common.exception.ForbiddenException;
import com.worklynx.backend.common.exception.ResourceNotFoundException;
import com.worklynx.backend.organization.Organization;
import com.worklynx.backend.organization.OrganizationAccessService;
import com.worklynx.backend.organization.OrganizationRepository;
import com.worklynx.backend.project.Project;
import com.worklynx.backend.project.ProjectRepository;
import com.worklynx.backend.security.UserPrincipal;
import com.worklynx.backend.task.dto.CreateTaskRequest;
import com.worklynx.backend.task.dto.TaskFilterRequest;
import com.worklynx.backend.task.dto.TaskResponse;
import com.worklynx.backend.task.dto.UpdateTaskRequest;
import com.worklynx.backend.task.spec.TaskSpecification;
import com.worklynx.backend.user.User;
import com.worklynx.backend.user.UserRepository;
import com.worklynx.backend.websocket.TaskEventPublisher;

@Service
public class TaskService {

  private final TaskRepository taskRepository;
  private final OrganizationRepository organizationRepository;
  private final OrganizationAccessService accessService;
  private final ProjectRepository projectRepository;
  private final UserRepository userRepository;
  private final TaskEventPublisher eventPublisher;
  private final ActivityService activityService;

  public TaskService(
      TaskRepository taskRepository,
      OrganizationRepository organizationRepository,
      OrganizationAccessService accessService,
      ProjectRepository projectRepository,
      UserRepository userRepository,
      TaskEventPublisher eventPublisher,
      ActivityService activityService) {
    this.taskRepository = taskRepository;
    this.organizationRepository = organizationRepository;
    this.accessService = accessService;
    this.projectRepository = projectRepository;
    this.userRepository = userRepository;
    this.eventPublisher = eventPublisher;
    this.activityService = activityService;
  }

  public PagedResponse<TaskResponse> getPersonalTasks(TaskFilterRequest filter, int page, int size,
      UserPrincipal principal) {

    Specification<Task> spec = Specification.where(TaskSpecification.hasOrganization(null))
        .and(TaskSpecification.createdBy(principal.getUserId())).and(TaskSpecification.hasStatus(filter.getStatus()));

    Page<Task> result = taskRepository.findAll(spec, PageRequest.of(page, size, Sort.by("createdAt").descending()));

    return mapToPagedResponse(result);
  }

  public PagedResponse<TaskResponse> getOrgTasks(
      Long orgId, TaskFilterRequest filter, int page, int size, UserPrincipal principal) {

    accessService.validateMembership(principal.getUserId(), orgId);

    Long assignedToId = null;

    if ("me".equals(filter.getAssignedTo())) {
      assignedToId = principal.getUserId();
    }

    Specification<Task> spec = Specification.where(TaskSpecification.hasOrganization(orgId))
        .and(TaskSpecification.hasStatus(filter.getStatus())).and(TaskSpecification.assignedTo(assignedToId));

    Page<Task> result = taskRepository.findAll(spec, PageRequest.of(page, size, Sort.by("createdAt").descending()));

    return mapToPagedResponse(result);
  }

  // PERSONAL TASK
  public TaskResponse createPersonalTask(
      CreateTaskRequest request,
      UserPrincipal principal) {

    User user = userRepository.findById(principal.getUserId())
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    Task task = Task.builder().title(request.getTitle()).description(request.getDescription()).createdBy(user)
        .assignedTo(user).status(Task.Status.TODO).build();

    taskRepository.save(task);

    return mapToResponse(task);
  }

  // ORG TASK
  public TaskResponse createOrgTask(
      Long orgId, CreateTaskRequest request, UserPrincipal principal) {

    Long userId = principal.getUserId();

    accessService.validateMembership(userId, orgId);

    Organization org = organizationRepository.findById(orgId)
        .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));

    User creator = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

    // Project validation
    Project project = null;
    if (request.getProjectId() != null) {

      project = projectRepository.findByIdAndOrganizationId(request.getProjectId(), orgId)
          .orElseThrow(() -> new ResourceNotFoundException("Project not found in this organization"));

      if (!project.getOrganization().getId().equals(orgId)) {
        throw new BadRequestException("Project does not belong to this organization");
      }
    }

    // Assigned user validation
    User assignedTo = creator;
    if (request.getAssignedToId() != null) {

      accessService.validateMembership(request.getAssignedToId(), orgId);

      assignedTo = userRepository.findById(request.getAssignedToId())
          .orElseThrow(() -> new ResourceNotFoundException("Assigned user not found"));
    }

    Task task = Task.builder().title(request.getTitle()).description(request.getDescription()).organization(org)
        .project(project).createdBy(creator).assignedTo(assignedTo).status(Task.Status.TODO).build();

    taskRepository.save(task);

    activityService.log(creator, org, "TASK_CREATED", "TASK", task.getId(), Map.of("title", task.getTitle()));

    eventPublisher.publishTaskCreated(task);

    return mapToResponse(task);
  }

  public TaskResponse updateTask(
      Long taskId, UpdateTaskRequest request, UserPrincipal principal) {

    Task task = taskRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Task not found"));

    Long userId = principal.getUserId();

    if (task.getOrganization() == null) {

      if (!task.getCreatedBy().getId().equals(userId)) {
        throw new ForbiddenException("Not allowed to update this task");
      }

      applyUpdates(task, request, userId, null);

    } else {

      Long orgId = task.getOrganization().getId();

      accessService.validateMembership(userId, orgId);

      applyUpdates(task, request, userId, orgId);
    }

    taskRepository.save(task);

    activityService.log(task.getCreatedBy(), task.getOrganization(), "TASK_UPDATED", "TASK", task.getId(), request);

    eventPublisher.publishTaskUpdated(task);

    return mapToResponse(task);
  }

  private void applyUpdates(Task task, UpdateTaskRequest request, Long userId, Long orgId) {

    if (request.getTitle() != null) {
      task.setTitle(request.getTitle());
    }

    if (request.getDescription() != null) {
      task.setDescription(request.getDescription());
    }

    if (request.getStatus() != null) {
      task.setStatus(Task.Status.valueOf(request.getStatus()));
    }

    if (request.getAssignedToId() != null) {

      if (orgId != null) {
        accessService.validateMembership(userId, orgId);
      }

      User assigned = userRepository.findById(request.getAssignedToId())
          .orElseThrow(() -> new ResourceNotFoundException("User not found"));

      task.setAssignedTo(assigned);
    }
  }

  // MAPPER
  private TaskResponse mapToResponse(Task task) {

    return TaskResponse.builder().id(task.getId()).title(task.getTitle()).description(task.getDescription())
        .status(task.getStatus().name()).projectId(task.getProject() != null ? task.getProject().getId() : null)
        .assignedToId(task.getAssignedTo() != null ? task.getAssignedTo().getId() : null).build();
  }

  private PagedResponse<TaskResponse> mapToPagedResponse(Page<Task> page) {

    return PagedResponse.<TaskResponse>builder().content(page.getContent().stream().map(this::mapToResponse).toList())
        .page(page.getNumber()).size(page.getSize()).totalElements(page.getTotalElements())
        .totalPages(page.getTotalPages()).build();
  }
}