package com.worklynx.backend.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.worklynx.backend.auth.dto.AccountConnectionsResponse;
import com.worklynx.backend.auth.dto.AuthResponse;
import com.worklynx.backend.auth.dto.AuthUserResponse;
import com.worklynx.backend.auth.dto.LoginRequest;
import com.worklynx.backend.auth.dto.RegisterRequest;
import com.worklynx.backend.auth.oauth.OAuthAccountService;
import com.worklynx.backend.auth.oauth.OAuthProvider;
import com.worklynx.backend.common.exception.BadRequestException;
import com.worklynx.backend.security.CookieUtils;
import com.worklynx.backend.security.UserPrincipal;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthService authService;
  private final OAuthAccountService oAuthAccountService;

  public AuthController(
      AuthService authService, OAuthAccountService oAuthAccountService) {
    this.authService = authService;
    this.oAuthAccountService = oAuthAccountService;
  }

  @PostMapping("/register")
  public ResponseEntity<Void> register(
      @RequestBody @Valid RegisterRequest request,
      HttpServletResponse response) {

    AuthResponse tokens = authService.register(request);

    response.addHeader("Set-Cookie", CookieUtils.createAccessTokenCookie(tokens.getAccessToken()).toString());

    response.addHeader("Set-Cookie", CookieUtils.createRefreshTokenCookie(tokens.getRefreshToken()).toString());

    return ResponseEntity.ok().build();
  }

  @PostMapping("/login")
  public ResponseEntity<Void> login(
      @RequestBody @Valid LoginRequest request,
      HttpServletResponse response) {

    AuthResponse tokens = authService.login(request);

    response.addHeader("Set-Cookie", CookieUtils.createAccessTokenCookie(tokens.getAccessToken()).toString());

    response.addHeader("Set-Cookie", CookieUtils.createRefreshTokenCookie(tokens.getRefreshToken()).toString());

    return ResponseEntity.ok().build();
  }

  @PostMapping("/refresh")
  public ResponseEntity<Void> refresh(HttpServletRequest request,
      HttpServletResponse response) {

    String refreshToken = null;

    Cookie[] cookies = request.getCookies();

    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if ("refreshToken".equals(cookie.getName())) {
          refreshToken = cookie.getValue();
        }
      }
    }

    if (refreshToken == null) {
      throw new BadRequestException("Refresh token missing");
    }

    AuthResponse tokens = authService.refresh(refreshToken);

    response.addHeader("Set-Cookie", CookieUtils.createAccessTokenCookie(tokens.getAccessToken()).toString());

    response.addHeader("Set-Cookie", CookieUtils.createRefreshTokenCookie(tokens.getRefreshToken()).toString());

    return ResponseEntity.ok().build();
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout(
      @AuthenticationPrincipal UserPrincipal principal, HttpServletResponse response) {

    authService.logout(principal.getUserId());

    response.addHeader("Set-Cookie", CookieUtils.deleteAccessTokenCookie().toString());

    response.addHeader("Set-Cookie", CookieUtils.deleteRefreshTokenCookie().toString());

    return ResponseEntity.ok().build();
  }

  @GetMapping("/me")
  public AuthUserResponse me(
      @AuthenticationPrincipal UserPrincipal principal) {

    return authService.getCurrentUser(principal.getUserId());
  }

  @GetMapping("/connections")
  public AccountConnectionsResponse getConnections(
      @AuthenticationPrincipal UserPrincipal principal) {

    return oAuthAccountService.getConnections(principal.getUserId());
  }

  @DeleteMapping("/connections/{provider}")
  public ResponseEntity<Void> disconnectProvider(
      @PathVariable String provider,
      @AuthenticationPrincipal UserPrincipal principal) {

    OAuthProvider oAuthProvider;

    try {
      oAuthProvider = OAuthProvider.valueOf(provider.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new BadRequestException("Unsupported OAuth provider");
    }

    oAuthAccountService.disconnectProvider(
        principal.getUserId(),
        oAuthProvider);

    return ResponseEntity.noContent().build();
  }
}
