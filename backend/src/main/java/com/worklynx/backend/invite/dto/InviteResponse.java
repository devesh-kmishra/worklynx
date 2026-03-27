package com.worklynx.backend.invite.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InviteResponse {

  private final String email;
  private final String role;
  private final String token;
}
