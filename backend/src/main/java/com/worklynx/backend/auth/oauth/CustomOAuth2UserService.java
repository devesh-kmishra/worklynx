package com.worklynx.backend.auth.oauth;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.worklynx.backend.user.User;
import com.worklynx.backend.user.UserOAuthAccount;
import com.worklynx.backend.user.UserOAuthAccountRepository;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

  private final UserOAuthAccountRepository userOAuthAccountRepository;
  private final GithubEmailService githubEmailService;

  public CustomOAuth2UserService(
      UserOAuthAccountRepository userOAuthAccountRepository,
      GithubEmailService githubEmailService) {
    this.userOAuthAccountRepository = userOAuthAccountRepository;
    this.githubEmailService = githubEmailService;
  }

  @Override
  public OAuth2User loadUser(OAuth2UserRequest request) {

    OAuth2User oAuthUser = super.loadUser(request);

    String registrationId = request.getClientRegistration()
        .getRegistrationId()
        .toUpperCase();

    OAuthProvider provider;

    try {
      provider = OAuthProvider.valueOf(registrationId);
    } catch (IllegalArgumentException exception) {
      throw new OAuth2AuthenticationException(
          new OAuth2Error("unsupported_provider"),
          "The OAuth provider " + registrationId + " is not supported.");
    }

    OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, oAuthUser.getAttributes());

    String providerId = userInfo.getId();

    if (providerId == null || providerId.isBlank()) {
      throw new OAuth2AuthenticationException(
          new OAuth2Error("invalid_user_info"),
          "The provider did not return a valid user ID.");
    }

    // Check whether this provider identity is already linked.
    UserOAuthAccount existingAccount = userOAuthAccountRepository
        .findByProviderAndProviderId(provider, providerId)
        .orElse(null);

    User existingUser = existingAccount != null
        ? existingAccount.getUser()
        : null;

    // An existing provider identity is sufficient for normal login.
    if (existingAccount != null) {
      return new CustomOAuth2User(
          oAuthUser,
          existingUser,
          provider,
          providerId,
          null,
          null,
          false);
    }

    // New provider identity. Retrieve its verified email.
    String email;
    boolean emailVerified;

    if (provider == OAuthProvider.GOOGLE) {
      email = userInfo.getEmail();
      emailVerified = userInfo instanceof GoogleOAuth2UserInfo googleInfo && googleInfo.isEmailVerified();

    } else {
      email = githubEmailService.getVerifiedEmail(request);
      emailVerified = email != null && !email.isBlank();
    }

    return new CustomOAuth2User(
        oAuthUser,
        null,
        provider,
        providerId,
        email,
        userInfo.getName(),
        emailVerified);
  }
}
