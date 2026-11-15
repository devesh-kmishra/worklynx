package com.worklynx.backend.auth.oauth;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.worklynx.backend.auth.dto.AccountConnectionsResponse;
import com.worklynx.backend.auth.dto.ConnectedProviderResponse;
import com.worklynx.backend.common.exception.ResourceNotFoundException;
import com.worklynx.backend.user.User;
import com.worklynx.backend.user.UserOAuthAccount;
import com.worklynx.backend.user.UserOAuthAccountRepository;
import com.worklynx.backend.user.UserRepository;

@Service
public class OAuthAccountService {

  private final UserRepository userRepository;
  private final UserOAuthAccountRepository userOAuthAccountRepository;

  public OAuthAccountService(
      UserRepository userRepository,
      UserOAuthAccountRepository userOAuthAccountRepository) {
    this.userRepository = userRepository;
    this.userOAuthAccountRepository = userOAuthAccountRepository;
  }

  @Transactional(readOnly = true)
  public AccountConnectionsResponse getConnections(Long userId) {

    User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

    boolean passwordLoginEnabled = user.getPassword() != null && !user.getPassword().isBlank();

    List<UserOAuthAccount> accounts = userOAuthAccountRepository.findAllByUserId(userId);

    boolean canDisconnectAnyProvider = passwordLoginEnabled || accounts.size() > 1;

    List<ConnectedProviderResponse> providers = accounts.stream()
        .map(account -> new ConnectedProviderResponse(
            account.getProvider(),
            canDisconnectAnyProvider))
        .toList();

    return new AccountConnectionsResponse(
        passwordLoginEnabled,
        providers);
  }

  @Transactional
  public void disconnectProvider(Long userId, OAuthProvider provider) {

    User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

    UserOAuthAccount account = userOAuthAccountRepository
        .findByUserIdAndProvider(userId, provider)
        .orElseThrow(() -> new ResourceNotFoundException("This provider is not connected to your account."));

    boolean passwordLoginEnabled = user.getPassword() != null && !user.getPassword().isBlank();

    long connectedProviderCount = userOAuthAccountRepository.countByUserId(userId);

    if (!passwordLoginEnabled && connectedProviderCount <= 1) {
      throw new com.worklynx.backend.common.exception.BadRequestException(
          "You cannot disconnect your only sign-in method. Set a password or connect another provider first.");
    }

    userOAuthAccountRepository.delete(account);
  }
}
