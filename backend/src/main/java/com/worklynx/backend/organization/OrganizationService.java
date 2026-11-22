package com.worklynx.backend.organization;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.worklynx.backend.common.exception.ForbiddenException;
import com.worklynx.backend.common.exception.ResourceNotFoundException;
import com.worklynx.backend.organization.dto.CreateOrganizationRequest;
import com.worklynx.backend.organization.dto.OrganizationResponse;
import com.worklynx.backend.project.ProjectRepository;
import com.worklynx.backend.project.dto.ProjectResponse;
import com.worklynx.backend.security.UserPrincipal;
import com.worklynx.backend.subscription.LimitType;
import com.worklynx.backend.subscription.Subscription;
import com.worklynx.backend.subscription.SubscriptionRepository;
import com.worklynx.backend.subscription.UsageLimitService;
import com.worklynx.backend.task.TaskRepository;
import com.worklynx.backend.user.User;
import com.worklynx.backend.user.UserRepository;

@Service
public class OrganizationService {

  private final OrganizationRepository organizationRepository;
  private final OrganizationMemberRepository memberRepository;
  private final UserRepository userRepository;
  private final SubscriptionRepository subscriptionRepository;
  private final ProjectRepository projectRepository;
  private final TaskRepository taskRepository;
  private final UsageLimitService usageLimitService;

  public OrganizationService(
      OrganizationRepository organizationRepository,
      OrganizationMemberRepository memberRepository,
      UserRepository userRepository,
      SubscriptionRepository subscriptionRepository,
      ProjectRepository projectRepository,
      TaskRepository taskRepository,
      UsageLimitService usageLimitService) {
    this.organizationRepository = organizationRepository;
    this.memberRepository = memberRepository;
    this.userRepository = userRepository;
    this.subscriptionRepository = subscriptionRepository;
    this.projectRepository = projectRepository;
    this.taskRepository = taskRepository;
    this.usageLimitService = usageLimitService;
  }

  @Transactional
  public OrganizationResponse createOrganization(
      CreateOrganizationRequest request,
      UserPrincipal principal) {

    Long userId = principal.getUserId();

    if (organizationRepository.existsByOwnerId(userId)) {
      throw new ForbiddenException("You can only create one organization.");
    }

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    Organization org = Organization.builder()
        .name(request.getName())
        .owner(user)
        .build();

    organizationRepository.save(org);

    // Add membership as OWNER
    OrganizationMember member = OrganizationMember.builder()
        .user(user)
        .organization(org)
        .role(OrganizationMember.Role.OWNER)
        .build();

    memberRepository.save(member);

    // Create default subscription
    Subscription sub = Subscription.builder()
        .organization(org)
        .plan(Subscription.Plan.FREE)
        .status(Subscription.Status.ACTIVE)
        .build();

    subscriptionRepository.save(sub);

    return toOrganizationResponse(member);
  }

  public List<OrganizationResponse> getMyOrganizations(UserPrincipal principal) {

    List<OrganizationMember> memberships = memberRepository.findByUserId(principal.getUserId());

    return memberships.stream()
        .map(this::toOrganizationResponse)
        .toList();
  }

  private OrganizationResponse toOrganizationResponse(OrganizationMember membership) {

    Organization org = membership.getOrganization();

    Subscription subscription = subscriptionRepository.findByOrganizationId(org.getId())
        .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));

    Subscription.Plan plan = subscription.getPlan();

    List<ProjectResponse> projects = projectRepository
        .findByOrganizationId(org.getId())
        .stream()
        .map(project -> ProjectResponse.builder()
            .id(project.getId())
            .name(project.getName())
            .build())
        .toList();

    long projectCount = projectRepository.countByOrganizationId(org.getId());
    long memberCount = memberRepository.countByOrganizationId(org.getId());
    long taskCount = taskRepository.countByOrganizationId(org.getId());

    return OrganizationResponse.builder()
        .id(org.getId())
        .name(org.getName())
        .role(membership.getRole())
        .plan(plan)
        .subscriptionStatus(subscription.getStatus())
        .projectCount(projectCount)
        .projectLimit(usageLimitService.getLimit(LimitType.PROJECTS_PER_ORG, plan))
        .memberCount(memberCount)
        .memberLimit(usageLimitService.getLimit(LimitType.MEMBERS_PER_ORG, plan))
        .taskCount(taskCount)
        .taskLimit(usageLimitService.getLimit(LimitType.TASKS_PER_ORG, plan))
        .projects(projects)
        .build();
  }
}
