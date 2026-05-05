package com.worklynx.backend.auth.oauth;

import java.util.Map;

public class MicrosoftOAuth2UserInfo extends OAuth2UserInfo {

  public MicrosoftOAuth2UserInfo(Map<String, Object> attributes) {
    super(attributes);
  }

  @Override
  public String getId() {
    return (String) attributes.get("id");
  }

  @Override
  public String getEmail() {
    return (String) attributes.get("mail");
  }

  @Override
  public String getName() {
    return (String) attributes.get("displayName");
  }
}
