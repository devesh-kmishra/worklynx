package com.worklynx.backend.auth;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.worklynx.backend.auth.dto.AuthResponse;
import com.worklynx.backend.auth.dto.LoginRequest;
import com.worklynx.backend.auth.dto.RegisterRequest;
import com.worklynx.backend.security.UserPrincipal;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/register")
  public AuthResponse register(@RequestBody @Valid RegisterRequest request) {

    return authService.register(request);
  }

  @PostMapping("/login")
  public AuthResponse login(@RequestBody @Valid LoginRequest request) {

    return authService.login(request);
  }

  @PostMapping("/refresh")
  public AuthResponse refresh(@RequestParam String refreshToken) {
    return authService.refresh(refreshToken);
  }

  @PostMapping("/logout")
  public void logout(@AuthenticationPrincipal UserPrincipal principal) {
    authService.logout(principal.getUserId());
  }
}
