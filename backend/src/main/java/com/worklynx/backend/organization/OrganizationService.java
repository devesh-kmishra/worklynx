package com.worklynx.backend.organization;

import java.util.List;

import org.springframework.stereotype.Service;

import com.worklynx.backend.common.exception.BadRequestException;
import com.worklynx.backend.organization.dto.CreateOrganizationRequest;
import com.worklynx.backend.organization.dto.OrganizationResponse;
import com.worklynx.backend.security.UserPrincipal;
import com.worklynx.backend.user.User;
import com.worklynx.backend.user.UserRepository;

@Service
public class OrganizationService {

  private final OrganizationRepository organizationRepository;
  private final OrganizationMemberRepository memberRepository;
  private final UserRepository userRepository;

  public OrganizationService(
      OrganizationRepository organizationRepository,
      OrganizationMemberRepository memberRepository, UserRepository userRepository) {
    this.organizationRepository = organizationRepository;
    this.memberRepository = memberRepository;
    this.userRepository = userRepository;
  }

  public OrganizationResponse createOrganization(
      CreateOrganizationRequest request,
      UserPrincipal principal) {
    User user = userRepository.findById(principal.getUserId())
        .orElseThrow(() -> new BadRequestException("User not found"));

    // Create organization
    Organization organization = Organization.builder().name(request.getName()).owner(user).build();

    organizationRepository.save(organization);

    // Add membership as OWNER
    OrganizationMember member = OrganizationMember.builder().user(user).organization(organization)
        .role(OrganizationMember.Role.OWNER).build();

    memberRepository.save(member);

    return OrganizationResponse.builder().id(organization.getId()).name(organization.getName()).build();
  }

  public List<OrganizationResponse> getMyOrganizations(UserPrincipal principal) {

    List<OrganizationMember> memberships = memberRepository.findByUserId(principal.getUserId());

    return memberships.stream().map(member -> OrganizationResponse.builder().id(member.getOrganization().getId())
        .name(member.getOrganization().getName()).build()).toList();
  }
}
