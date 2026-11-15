package com.worklynx.backend.auth.oauth;

import java.util.Map;

public class GithubOAuth2UserInfo extends OAuth2UserInfo {

  public GithubOAuth2UserInfo(Map<String, Object> attributes) {
    super(attributes);
  }

  @Override
  public String getId() {
    Object id = attributes.get("id");
    return id == null ? null : String.valueOf(id);
  }

  @Override
  public String getEmail() {
    Object email = attributes.get("email");
    return email instanceof String ? (String) email : null;
  }

  @Override
  public String getName() {
    Object name = attributes.get("name");

    if (name instanceof String value && !value.isBlank()) {
      return value;
    }

    Object login = attributes.get("login");
    return login instanceof String ? (String) login : null;
  }
}
