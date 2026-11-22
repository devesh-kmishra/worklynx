package com.worklynx.backend.invite;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.worklynx.backend.subscription.Feature;
import com.worklynx.backend.subscription.SubscriptionService;
import com.worklynx.backend.user.User;
import com.worklynx.backend.user.UserRepository;

@Service
public class InviteService {

  private final OrganizationRepository organizationRepository;
  private final OrganizationMemberRepository memberRepository;
  private final OrganizationInviteRepository inviteRepository;
  private final UserRepository userRepository;
  private final NotificationService notificationService;
  private final SubscriptionService subscriptionService;

  public InviteService(
      OrganizationRepository organizationRepository,
      OrganizationMemberRepository memberRepository,
      OrganizationInviteRepository inviteRepository,
      UserRepository userRepository,
      NotificationService notificationService,
      SubscriptionService subscriptionService) {
    this.organizationRepository = organizationRepository;
    this.memberRepository = memberRepository;
    this.inviteRepository = inviteRepository;
    this.userRepository = userRepository;
    this.notificationService = notificationService;
    this.subscriptionService = subscriptionService;
  }

  // SEND INVITE
  @Transactional
  @RequireRole({ OrganizationMember.Role.ADMIN, OrganizationMember.Role.OWNER })
  public InviteResponse createInvite(
      Long orgId,
      CreateInviteRequest request,
      UserPrincipal principal) {

    Organization org = organizationRepository
        .findByIdForUpdate(orgId)
        .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));

    Long userId = principal.getUserId();

    subscriptionService.checkFeature(orgId, Feature.INVITE_MEMBERS);
    subscriptionService.checkMemberCapacityForInvite(orgId);

    User inviter = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    User invitee = userRepository.findByEmailIgnoreCase(request.getEmail())
        .orElseThrow(() -> new ResourceNotFoundException("Worklynx account not found"));

    if (memberRepository.existsByUserIdAndOrganizationId(
        invitee.getId(), orgId)) {
      throw new BadRequestException("User is already a member of this organization");
    }

    Instant now = Instant.now();

    if (inviteRepository.existsByOrganizationIdAndEmailIgnoreCaseAndAcceptedFalseAndExpiresAtAfter(orgId,
        invitee.getEmail(), now)) {
      throw new BadRequestException("An active invitation already exists for this user");
    }

    OrganizationMember.Role role;

    try {
      role = OrganizationMember.Role.valueOf(request.getRole().toUpperCase());
    } catch (Exception e) {
      throw new BadRequestException("Invalid role");
    }

    if (role == OrganizationMember.Role.OWNER) {
      throw new BadRequestException("The OWNER role cannot be assigned through invitations");
    }

    String token = UUID.randomUUID().toString();

    OrganizationInvite invite = OrganizationInvite.builder()
        .email(invitee.getEmail())
        .token(token)
        .organization(org)
        .invitedBy(inviter)
        .role(role)
        .expiresAt(now.plus(7, ChronoUnit.DAYS))
        .build();

    inviteRepository.save(invite);

    notificationService.notify(
        invitee,
        "INVITE_CREATED",
        inviter.getName() + " invited you to the " + org.getName() + " organization",
        invite.getId());

    return InviteResponse.builder()
        .email(invite.getEmail())
        .role(invite.getRole().name())
        .token(invite.getToken())
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

    return InviteResponse.builder()
        .email(invite.getEmail())
        .role(invite.getRole().name())
        .token(invite.getToken())
        .build();
  }

  // ACCEPT INVITE
  @Transactional
  public void acceptInvite(String token, UserPrincipal principal) {

    OrganizationInvite invite = inviteRepository
        .findByTokenForUpdate(token)
        .orElseThrow(() -> new ResourceNotFoundException("Invite not found"));

    if (invite.isAccepted()) {
      throw new BadRequestException("Invite already accepted");
    }

    if (invite.getExpiresAt().isBefore(Instant.now())) {
      throw new BadRequestException("Invite expired");
    }

    Long orgId = invite.getOrganization().getId();

    Organization org = organizationRepository
        .findByIdForUpdate(orgId)
        .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));

    User user = userRepository.findById(principal.getUserId())
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    if (!invite.getEmail().equalsIgnoreCase(user.getEmail())) {
      throw new ForbiddenException("This invite is not for your email");
    }

    if (memberRepository.existsByUserIdAndOrganizationId(user.getId(), orgId)) {
      throw new BadRequestException("You are already a member of this organization");
    }

    subscriptionService.checkMemberCapacityForAcceptance(orgId, invite.getId());

    OrganizationMember member = OrganizationMember.builder()
        .user(user)
        .organization(org)
        .role(invite.getRole())
        .build();

    memberRepository.save(member);

    invite.setAccepted(true);

    notificationService.notify(
        invite.getInvitedBy(),
        "INVITE_ACCEPTED",
        user.getName() + " accepted your invite to the organization " + org.getName(),
        invite.getId());
  }
}
