package com.worklynx.backend.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.worklynx.backend.auth.oauth.OAuthProvider;

public interface UserOAuthAccountRepository extends JpaRepository<UserOAuthAccount, Long> {

  @EntityGraph(attributePaths = "user")
  Optional<UserOAuthAccount> findByProviderAndProviderId(OAuthProvider provider, String providerId);

  List<UserOAuthAccount> findAllByUserId(Long userId);

  Optional<UserOAuthAccount> findByUserIdAndProvider(
      Long userId, OAuthProvider provider);

  boolean existsByUserIdAndProvider(Long userId, OAuthProvider provider);

  long countByUserId(Long userId);

  void deleteByUserIdAndProvider(Long userId, OAuthProvider provider);
}
