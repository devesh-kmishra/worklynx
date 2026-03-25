package com.worklynx.backend.project;

import java.util.List;

import org.springframework.stereotype.Service;

import com.worklynx.backend.organization.Organization;
import com.worklynx.backend.organization.OrganizationAccessService;
import com.worklynx.backend.organization.OrganizationRepository;
import com.worklynx.backend.project.dto.CreateProjectRequest;
import com.worklynx.backend.project.dto.ProjectResponse;
import com.worklynx.backend.security.UserPrincipal;
import com.worklynx.backend.user.User;
import com.worklynx.backend.user.UserRepository;

@Service
public class ProjectService {

  private final ProjectRepository projectRepository;
  private final OrganizationRepository organizationRepository;
  private final OrganizationAccessService accessService;
  private final UserRepository userRepository;

  public ProjectService(
      ProjectRepository projectRepository,
      OrganizationRepository organizationRepository,
      OrganizationAccessService accessService,
      UserRepository userRepository) {
    this.projectRepository = projectRepository;
    this.organizationRepository = organizationRepository;
    this.accessService = accessService;
    this.userRepository = userRepository;
  }

  public ProjectResponse createProject(
      Long orgId,
      CreateProjectRequest request,
      UserPrincipal principal) {

    accessService.validateMembership(principal.getUserId(), orgId);

    Organization organization = organizationRepository.findById(orgId).orElseThrow();

    User user = userRepository.findById(principal.getUserId()).orElseThrow();

    Project project = Project.builder().name(request.getName()).organization(organization).createdBy(user).build();

    projectRepository.save(project);

    return ProjectResponse.builder().id(project.getId()).name(project.getName()).build();
  }

  public List<ProjectResponse> getProjects(
      Long orgId,
      UserPrincipal principal) {

    accessService.validateMembership(principal.getUserId(), orgId);

    return projectRepository.findByOrganizationId(orgId).stream()
        .map(project -> ProjectResponse.builder().id(project.getId()).name(project.getName()).build()).toList();
  }
}
