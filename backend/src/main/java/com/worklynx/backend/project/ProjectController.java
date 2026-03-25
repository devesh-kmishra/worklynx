package com.worklynx.backend.project;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.worklynx.backend.project.dto.CreateProjectRequest;
import com.worklynx.backend.project.dto.ProjectResponse;
import com.worklynx.backend.security.UserPrincipal;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/organizations/{orgId}/projects")
public class ProjectController {

  private final ProjectService projectService;

  public ProjectController(ProjectService projectService) {
    this.projectService = projectService;
  }

  @PostMapping
  public ProjectResponse createProject(
      @PathVariable Long orgId,
      @RequestBody @Valid CreateProjectRequest request,
      @AuthenticationPrincipal UserPrincipal principal) {
    return projectService.createProject(orgId, request, principal);
  }

  @GetMapping
  public List<ProjectResponse> getProjects(
      @PathVariable Long orgId,
      @AuthenticationPrincipal UserPrincipal principal) {
    return projectService.getProjects(orgId, principal);
  }
}
