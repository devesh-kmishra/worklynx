package com.worklynx.backend.auth.oauth;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class OAuth2FailureHandler implements AuthenticationFailureHandler {

  @Override
  public void onAuthenticationFailure(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException exception)
      throws IOException {

    HttpSession session = request.getSession(false);

    boolean linking = session != null && session.getAttribute(OAuthLinkSession.USER_ID) != null;

    if (session != null) {
      session.removeAttribute(OAuthLinkSession.USER_ID);
    }

    if (linking) {
      response.sendRedirect("http://localhost:4200/settings/account?linkError=true");
      return;
    }

    response.sendRedirect("http://localhost:4200/login?oauthError=true");
  }
}
