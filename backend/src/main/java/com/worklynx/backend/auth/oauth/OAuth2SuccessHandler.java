package com.worklynx.backend.auth.oauth;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.worklynx.backend.auth.AuthService;
import com.worklynx.backend.auth.dto.AuthResponse;
import com.worklynx.backend.user.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final AuthService authService;

  public OAuth2SuccessHandler(AuthService authService) {
    this.authService = authService;
  }

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication) throws IOException {

    CustomOAuth2User principal = (CustomOAuth2User) authentication.getPrincipal();

    User user = principal.getUser();

    AuthResponse tokens = authService.generateTokens(user);

    String redirectUrl = "http://localhost:3000/oauth-success" + "?accessToken=" + tokens.getAccessToken()
        + "&refreshToken=" + tokens.getRefreshToken();

    getRedirectStrategy().sendRedirect(request, response, redirectUrl);
  }
}
