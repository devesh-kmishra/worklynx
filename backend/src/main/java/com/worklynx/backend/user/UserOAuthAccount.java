package com.worklynx.backend.user;

import com.worklynx.backend.auth.oauth.OAuthProvider;
import com.worklynx.backend.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_oauth_accounts", uniqueConstraints = {
    @UniqueConstraint(name = "uk_oauth_provider_id", columnNames = { "provider", "providerId" }),
    @UniqueConstraint(name = "uk_user_provider", columnNames = { "user_id", "provider" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserOAuthAccount extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private OAuthProvider provider;

  @Column(name = "provider_id", nullable = false)
  private String providerId;
}
