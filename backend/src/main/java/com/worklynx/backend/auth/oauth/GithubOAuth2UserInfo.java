package com.worklynx.backend.auth.oauth;

import java.util.Map;

public class GithubOAuth2UserInfo extends OAuth2UserInfo {

  public GithubOAuth2UserInfo(Map<String, Object> attributes) {
    super(attributes);
  }

  @Override
  public String getId() {
    return String.valueOf(attributes.get("id"));
  }

  @Override
  public String getEmail() {
    return (String) attributes.get("email");
  }

  @Override
  public String getName() {
    return (String) attributes.get("login");
  }
}
