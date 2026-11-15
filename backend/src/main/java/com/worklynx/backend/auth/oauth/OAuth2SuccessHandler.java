package com.worklynx.backend.auth.oauth;

import java.io.IOException;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.worklynx.backend.auth.AuthService;
import com.worklynx.backend.auth.dto.AuthResponse;
import com.worklynx.backend.security.CookieUtils;
import com.worklynx.backend.user.User;
import com.worklynx.backend.user.UserOAuthAccount;
import com.worklynx.backend.user.UserOAuthAccountRepository;
import com.worklynx.backend.user.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private static final String FRONTEND_URL = "http://localhost:4200";

  private final AuthService authService;
  private final UserRepository userRepository;
  private final UserOAuthAccountRepository userOAuthAccountRepository;

  public OAuth2SuccessHandler(
      AuthService authService,
      UserRepository userRepository,
      UserOAuthAccountRepository userOAuthAccountRepository) {
    this.authService = authService;
    this.userRepository = userRepository;
    this.userOAuthAccountRepository = userOAuthAccountRepository;
  }

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication) throws IOException {

    CustomOAuth2User principal = (CustomOAuth2User) authentication.getPrincipal();

    HttpSession session = request.getSession(false);

    Object linkingUserId = session != null
        ? session.getAttribute(OAuthLinkSession.USER_ID)
        : null;

    if (session != null) {
      session.removeAttribute(OAuthLinkSession.USER_ID);
    }

    // This OAuth operation was initiated from Account Settings.
    if (linkingUserId instanceof Number userId) {
      linkAccount(userId.longValue(), principal, request, response);
      return;
    }

    // Otherwise, this is a normal OAuth login.
    login(principal, request, response);
  }

  private void login(
      CustomOAuth2User principal,
      HttpServletRequest request,
      HttpServletResponse response) throws IOException {

    User user = principal.getUser();

    // A new provider identity; create account if allowed.
    if (user == null) {
      String email = principal.getEmail();

      if (email == null || email.isBlank() || !principal.isEmailVerified()) {
        redirect(request, response, "/login?oauthError=unverified_email");
        return;
      }

      if (userRepository.existsByEmail(email)) {
        redirect(request, response, "/login?oauthError=account_exists");
        return;
      }

      String name = principal.getUserName();

      if (name == null || name.isBlank()) {
        name = email.substring(0, email.indexOf('@'));
      }

      user = User.builder()
          .email(email)
          .name(name)
          .password(null)
          .emailVerified(true)
          .build();

      user = userRepository.save(user);

      UserOAuthAccount oAuthAccount = UserOAuthAccount.builder()
          .user(user)
          .provider(principal.getProvider())
          .providerId(principal.getProviderId())
          .build();

      userOAuthAccountRepository.save(oAuthAccount);
    }

    // Generate session tokens only for normal login.
    AuthResponse tokens = authService.generateTokens(user);

    response.addHeader("Set-Cookie", CookieUtils.createAccessTokenCookie(tokens.getAccessToken()).toString());

    response.addHeader("Set-Cookie", CookieUtils.createRefreshTokenCookie(tokens.getRefreshToken()).toString());

    redirect(request, response, "/tasks");
  }

  private void linkAccount(
      Long userId,
      CustomOAuth2User principal,
      HttpServletRequest request,
      HttpServletResponse response) throws IOException {

    Optional<User> optionalUser = userRepository.findById(userId);

    if (optionalUser.isEmpty()) {
      redirect(request, response, "/login?sessionExpired=true");
      return;
    }

    User user = optionalUser.get();

    // Is this provider identity already connected?
    Optional<UserOAuthAccount> existingAccount = userOAuthAccountRepository
        .findByProviderAndProviderId(principal.getProvider(), principal.getProviderId());

    if (existingAccount.isPresent()) {
      if (existingAccount.get().getUser().getId().equals(userId)) {
        redirect(request, response, "/settings/account?linked=true");
      } else {
        redirect(request, response, "/settings/account?linkError=identity_in_use");
      }

      return;
    }

    // Only one account per provider per user.
    if (userOAuthAccountRepository.existsByUserIdAndProvider(userId, principal.getProvider())) {

      redirect(request, response, "/settings/account?linkError=provider_already_linked");
      return;
    }

    // Avoid silently associating an email belonging to another user.
    String email = principal.getEmail();

    if (email != null && !email.isBlank() && principal.isEmailVerified()) {

      Optional<User> emailOwner = userRepository.findByEmail(email);

      if (emailOwner.isPresent() && !emailOwner.get().getId().equals(userId)) {
        redirect(request, response, "/settings/account?linkError=email_in_use");
        return;
      }
    }

    UserOAuthAccount oAuthAccount = UserOAuthAccount.builder()
        .user(user)
        .provider(principal.getProvider())
        .providerId(principal.getProviderId())
        .build();

    userOAuthAccountRepository.save(oAuthAccount);

    // Do not generate new tokens or replace the existing session.
    redirect(request, response, "/settings/account?linked=true");
  }

  private void redirect(
      HttpServletRequest request,
      HttpServletResponse response,
      String path) throws IOException {

    getRedirectStrategy().sendRedirect(request, response, FRONTEND_URL + path);
  }
}
