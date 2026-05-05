package com.worklynx.backend.auth.oauth;

import java.util.Map;

public class OAuth2UserInfoFactory {

  public static OAuth2UserInfo getOAuth2UserInfo(
      String registrationId,
      Map<String, Object> attributes) {

    return switch (registrationId.toLowerCase()) {

      case "google" -> new GoogleOAuth2UserInfo(attributes);

      case "github" -> new GithubOAuth2UserInfo(attributes);

      case "microsoft" -> new MicrosoftOAuth2UserInfo(attributes);

      default -> throw new RuntimeException("Unsupported provider");
    };
  }
}
