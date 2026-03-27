package com.worklynx.backend.invite;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.worklynx.backend.invite.dto.CreateInviteRequest;
import com.worklynx.backend.invite.dto.InviteResponse;
import com.worklynx.backend.security.UserPrincipal;

import jakarta.validation.Valid;

@RestController
public class InviteController {

  private final InviteService inviteService;

  public InviteController(InviteService inviteService) {
    this.inviteService = inviteService;
  }

  @PostMapping("/organizations/{orgId}/invites")
  public InviteResponse createInvite(
      @PathVariable Long orgId,
      @RequestBody @Valid CreateInviteRequest request,
      @AuthenticationPrincipal UserPrincipal principal) {
    return inviteService.createInvite(orgId, request, principal);
  }

  @GetMapping("/invites/{token}")
  public InviteResponse getInvite(@PathVariable String token) {
    return inviteService.getInvite(token);
  }

  @PostMapping("/invites/{token}/accept")
  public void acceptInvite(
      @PathVariable String token,
      @AuthenticationPrincipal UserPrincipal principal) {
    inviteService.acceptInvite(token, principal);
  }
}
