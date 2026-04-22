package com.worklynx.backend.invite;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.worklynx.backend.common.exception.BadRequestException;
import com.worklynx.backend.common.exception.ForbiddenException;
import com.worklynx.backend.common.exception.ResourceNotFoundException;
import com.worklynx.backend.invite.dto.CreateInviteRequest;
import com.worklynx.backend.invite.dto.InviteResponse;
import com.worklynx.backend.notification.NotificationService;
import com.worklynx.backend.organization.Organization;
import com.worklynx.backend.organization.OrganizationMember;
import com.worklynx.backend.organization.OrganizationMemberRepository;
import com.worklynx.backend.organization.OrganizationRepository;
import com.worklynx.backend.security.UserPrincipal;
import com.worklynx.backend.security.annotation.RequireRole;
import com.worklynx.backend.user.User;
import com.worklynx.backend.user.UserRepository;

@Service
public class InviteService {

  private final OrganizationRepository organizationRepository;
  private final OrganizationMemberRepository memberRepository;
  private final OrganizationInviteRepository inviteRepository;
  private final UserRepository userRepository;
  private final NotificationService notificationService;

  public InviteService(
      OrganizationRepository organizationRepository,
      OrganizationMemberRepository memberRepository,
      OrganizationInviteRepository inviteRepository,
      UserRepository userRepository,
      NotificationService notificationService) {
    this.organizationRepository = organizationRepository;
    this.memberRepository = memberRepository;
    this.inviteRepository = inviteRepository;
    this.userRepository = userRepository;
    this.notificationService = notificationService;
  }

  // SEND INVITE
  @RequireRole({ OrganizationMember.Role.ADMIN, OrganizationMember.Role.OWNER })
  public InviteResponse createInvite(
      Long orgId, CreateInviteRequest request, UserPrincipal principal) {
    Long userId = principal.getUserId();

    Organization org = organizationRepository.findById(orgId)
        .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));

    User inviter = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

    OrganizationMember.Role role;
    try {
      role = OrganizationMember.Role.valueOf(request.getRole());
    } catch (Exception e) {
      throw new BadRequestException("Invalid role");
    }

    // Prevent duplicate membership
    userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
      if (memberRepository.existsByUserIdAndOrganizationId(user.getId(), orgId)) {
        throw new BadRequestException("User already in organization");
      }
    });

    String token = UUID.randomUUID().toString();

    OrganizationInvite invite = OrganizationInvite.builder().email(request.getEmail()).token(token).organization(org)
        .invitedBy(inviter).role(role).expiresAt(Instant.now().plus(7, ChronoUnit.DAYS)).build();

    inviteRepository.save(invite);

    User invitee = userRepository.findByEmail(invite.getEmail())
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    notificationService.notify(invitee, "INVITE_CREATED",
        inviter.getName() + " invited you to the " + org.getName() + " organization", invite.getId());

    return InviteResponse.builder().email(invite.getEmail()).role(invite.getRole().name()).token(invite.getToken())
        .build();
  }

  // GET INVITE (VALIDATE)
  public InviteResponse getInvite(String token) {

    OrganizationInvite invite = inviteRepository.findByToken(token)
        .orElseThrow(() -> new ResourceNotFoundException("Invite not found"));

    if (invite.isAccepted()) {
      throw new BadRequestException("Invite already accepted");
    }

    if (invite.getExpiresAt().isBefore(Instant.now())) {
      throw new BadRequestException("Invite expired");
    }

    return InviteResponse.builder().email(invite.getEmail()).role(invite.getRole().name()).token(invite.getToken())
        .build();
  }

  // ACCEPT INVITE
  public void acceptInvite(String token, UserPrincipal principal) {

    OrganizationInvite invite = inviteRepository.findByToken(token)
        .orElseThrow(() -> new ResourceNotFoundException("Invite not found"));

    if (invite.isAccepted()) {
      throw new BadRequestException("Invite already accepted");
    }

    if (invite.getExpiresAt().isBefore(Instant.now())) {
      throw new BadRequestException("Invite expired");
    }

    User user = userRepository.findById(principal.getUserId())
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    if (!invite.getEmail().equalsIgnoreCase(user.getEmail())) {
      throw new ForbiddenException("This invite is not for your email");
    }

    OrganizationMember member = OrganizationMember.builder().user(user).organization(invite.getOrganization())
        .role(invite.getRole()).build();

    memberRepository.save(member);

    invite.setAccepted(true);
    inviteRepository.save(invite);

    notificationService.notify(invite.getInvitedBy(), "INVITE_ACCEPTED",
        user.getName() + " accepted your invite to the organization " + invite.getOrganization().getName(),
        invite.getId());
  }
}
