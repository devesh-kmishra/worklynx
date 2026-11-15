package com.worklynx.backend.auth.oauth;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.worklynx.backend.common.exception.BadRequestException;
import com.worklynx.backend.security.UserPrincipal;
import com.worklynx.backend.user.UserOAuthAccountRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/auth/link")
public class OAuthAccountLinkController {

  private final UserOAuthAccountRepository userOAuthAccountRepository;

  public OAuthAccountLinkController(
      UserOAuthAccountRepository userOAuthAccountRepository) {
    this.userOAuthAccountRepository = userOAuthAccountRepository;
  }

  @PostMapping("/{provider}")
  public ResponseEntity<Map<String, String>> startLinking(
      @PathVariable String provider,
      @AuthenticationPrincipal UserPrincipal principal,
      HttpServletRequest request) {

    OAuthProvider oAuthProvider;

    try {
      oAuthProvider = OAuthProvider.valueOf(provider.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new BadRequestException("Unsupported OAuth provider");
    }

    if (userOAuthAccountRepository.existsByUserIdAndProvider(principal.getUserId(), oAuthProvider)) {

      throw new BadRequestException("Your account is already linked with this provider.");
    }

    HttpSession session = request.getSession(true);

    session.setAttribute(OAuthLinkSession.USER_ID, principal.getUserId());

    String authorizationPath = request.getContextPath() + "/oauth2/authorization/" + provider.toLowerCase();

    return ResponseEntity.ok(Map.of("authorizationPath", authorizationPath));
  }
}
