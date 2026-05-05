package com.worklynx.backend.auth.oauth;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.worklynx.backend.user.User;
import com.worklynx.backend.user.UserRepository;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

  private final UserRepository userRepository;

  public CustomOAuth2UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public OAuth2User loadUser(OAuth2UserRequest request) {

    OAuth2User oAuth2User = super.loadUser(request);

    String registrationId = request.getClientRegistration().getRegistrationId();

    OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, oAuth2User.getAttributes());

    String email = userInfo.getEmail();

    if (email == null) {
      throw new RuntimeException("Email not found from OAuth provider");
    }

    User user = userRepository.findByEmail(email).orElseGet(() -> createUser(userInfo, registrationId));

    return new CustomOAuth2User(oAuth2User, user);
  }

  private User createUser(OAuth2UserInfo userInfo, String provider) {

    User user = User.builder().email(userInfo.getEmail()).name(userInfo.getName()).provider(provider.toUpperCase())
        .providerId(userInfo.getId())
        .build();

    return userRepository.save(user);
  }
}
