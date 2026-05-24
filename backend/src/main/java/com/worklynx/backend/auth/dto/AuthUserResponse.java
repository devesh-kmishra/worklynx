package com.worklynx.backend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthUserResponse {

  private final Long id;
  private final String name;
  private final String email;
}
