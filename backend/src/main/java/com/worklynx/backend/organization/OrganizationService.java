package com.worklynx.backend.organization;

import java.util.List;

import org.springframework.stereotype.Service;

import com.worklynx.backend.common.exception.ResourceNotFoundException;
import com.worklynx.backend.organization.dto.CreateOrganizationRequest;
import com.worklynx.backend.organization.dto.OrganizationResponse;
import com.worklynx.backend.security.UserPrincipal;
import com.worklynx.backend.subscription.Subscription;
import com.worklynx.backend.subscription.SubscriptionRepository;
import com.worklynx.backend.user.User;
import com.worklynx.backend.user.UserRepository;

@Service
public class OrganizationService {

  private final OrganizationRepository organizationRepository;
  private final OrganizationMemberRepository memberRepository;
  private final UserRepository userRepository;
  private final SubscriptionRepository subscriptionRepository;

  public OrganizationService(
      OrganizationRepository organizationRepository,
      OrganizationMemberRepository memberRepository, UserRepository userRepository,
      SubscriptionRepository subscriptionRepository) {
    this.organizationRepository = organizationRepository;
    this.memberRepository = memberRepository;
    this.userRepository = userRepository;
    this.subscriptionRepository = subscriptionRepository;
  }

  public OrganizationResponse createOrganization(
      CreateOrganizationRequest request,
      UserPrincipal principal) {

    User user = userRepository.findById(principal.getUserId())
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    // Create organization
    Organization org = Organization.builder().name(request.getName()).owner(user).build();

    organizationRepository.save(org);

    // Add membership as OWNER
    OrganizationMember member = OrganizationMember.builder().user(user).organization(org)
        .role(OrganizationMember.Role.OWNER).build();

    memberRepository.save(member);

    // Create default subscription
    Subscription sub = Subscription.builder().organization(org).plan(Subscription.Plan.FREE)
        .status(Subscription.Status.ACTIVE).build();

    subscriptionRepository.save(sub);

    return OrganizationResponse.builder().id(org.getId()).name(org.getName()).build();
  }

  public List<OrganizationResponse> getMyOrganizations(UserPrincipal principal) {

    List<OrganizationMember> memberships = memberRepository.findByUserId(principal.getUserId());

    return memberships.stream().map(member -> OrganizationResponse.builder().id(member.getOrganization().getId())
        .name(member.getOrganization().getName()).build()).toList();
  }
}
