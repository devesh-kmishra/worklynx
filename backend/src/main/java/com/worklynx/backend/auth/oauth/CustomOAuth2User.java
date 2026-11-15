package com.worklynx.backend.auth.oauth;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.worklynx.backend.user.User;

public class CustomOAuth2User implements OAuth2User {

  private final OAuth2User oAuth2User;
  private final User user;
  private final OAuthProvider provider;
  private final String providerId;
  private final String email;
  private final String name;
  private final boolean emailVerified;

  public CustomOAuth2User(
      OAuth2User oAuth2User,
      User user,
      OAuthProvider provider,
      String providerId,
      String email,
      String name,
      boolean emailVerified) {
    this.oAuth2User = oAuth2User;
    this.user = user;
    this.provider = provider;
    this.providerId = providerId;
    this.email = email;
    this.name = name;
    this.emailVerified = emailVerified;
  }

  @Override
  public Map<String, Object> getAttributes() {
    return oAuth2User.getAttributes();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return oAuth2User.getAuthorities();
  }

  @Override
  public String getName() {
    return providerId;
  }

  public User getUser() {
    return user;
  }

  public OAuthProvider getProvider() {
    return provider;
  }

  public String getProviderId() {
    return providerId;
  }

  public String getEmail() {
    return email;
  }

  public String getUserName() {
    return name;
  }

  public boolean isEmailVerified() {
    return emailVerified;
  }
}
