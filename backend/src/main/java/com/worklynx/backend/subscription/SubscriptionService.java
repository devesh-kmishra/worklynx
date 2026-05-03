package com.worklynx.backend.subscription;

import org.springframework.stereotype.Service;

import com.worklynx.backend.common.exception.ForbiddenException;
import com.worklynx.backend.common.exception.ResourceNotFoundException;
import com.worklynx.backend.organization.OrganizationMemberRepository;
import com.worklynx.backend.project.ProjectRepository;
import com.worklynx.backend.task.TaskRepository;

@Service
public class SubscriptionService {

  private final SubscriptionRepository repository;
  private final FeatureAccessService featureAccessService;
  private final UsageLimitService usageLimitService;
  private final ProjectRepository projectRepository;
  private final OrganizationMemberRepository memberRepository;
  private final TaskRepository taskRepository;

  public SubscriptionService(
      SubscriptionRepository repository,
      FeatureAccessService featureAccessService,
      UsageLimitService usageLimitService,
      ProjectRepository projectRepository,
      OrganizationMemberRepository memberRepository,
      TaskRepository taskRepository) {
    this.repository = repository;
    this.featureAccessService = featureAccessService;
    this.usageLimitService = usageLimitService;
    this.projectRepository = projectRepository;
    this.memberRepository = memberRepository;
    this.taskRepository = taskRepository;
  }

  public Subscription getByOrg(Long orgId) {
    return repository.findByOrganizationId(orgId)
        .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));
  }

  public void checkFeature(Long orgId, Feature feature) {

    Subscription sub = getByOrg(orgId);

    if (!featureAccessService.isAllowed(feature, sub.getPlan())) {
      throw new ForbiddenException("Feature '" + feature + "' requires a higher plan");
    }
  }

  public void checkLimit(Long orgId, LimitType type) {

    Subscription sub = getByOrg(orgId);

    int limit = usageLimitService.getLimit(type, sub.getPlan());

    long currentCount = switch (type) {

      case PROJECTS_PER_ORG -> projectRepository.countByOrganizationId(orgId);

      case MEMBERS_PER_ORG -> memberRepository.countByOrganizationId(orgId);

      case TASKS_PER_ORG -> taskRepository.countByOrganizationId(orgId);
    };

    if (currentCount >= limit) {
      throw new ForbiddenException("Limit reached for " + type + ". Upgrade your plan.");
    }
  }
}
