package com.worklynx.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.worklynx.backend.auth.oauth.CustomOAuth2UserService;
import com.worklynx.backend.auth.oauth.OAuth2SuccessHandler;

@Configuration
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtFilter;
  private final CustomOAuth2UserService customOAuth2UserService;
  private final OAuth2SuccessHandler oAuth2SuccessHandler;

  public SecurityConfig(JwtAuthenticationFilter jwtFilter,
      CustomOAuth2UserService customOAuth2UserService,
      OAuth2SuccessHandler oAuth2SuccessHandler) {
    this.jwtFilter = jwtFilter;
    this.customOAuth2UserService = customOAuth2UserService;
    this.oAuth2SuccessHandler = oAuth2SuccessHandler;
  }

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    http.csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth.requestMatchers("/auth/**").permitAll().anyRequest().authenticated())
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        .oauth2Login(oauth -> oauth.userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
            .successHandler(oAuth2SuccessHandler));

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
