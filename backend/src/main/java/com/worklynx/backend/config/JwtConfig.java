package com.worklynx.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {

  private String secret;
  private Integer expiration;

  public String getSecret() {
    return secret;
  }

  public Integer getExpiration() {
    return expiration;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }

  public void setExpiration(Integer expiration) {
    this.expiration = expiration;
  }
}
