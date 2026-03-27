package com.worklynx.backend.auth;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.worklynx.backend.auth.dto.AuthResponse;
import com.worklynx.backend.auth.dto.LoginRequest;
import com.worklynx.backend.auth.dto.RegisterRequest;
import com.worklynx.backend.common.exception.BadRequestException;
import com.worklynx.backend.common.exception.ResourceNotFoundException;
import com.worklynx.backend.security.JwtService;
import com.worklynx.backend.user.User;
import com.worklynx.backend.user.UserRepository;

@Service
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final RefreshTokenRepository refreshTokenRepository;

  public AuthService(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      JwtService jwtService,
      RefreshTokenRepository refreshTokenRepository) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
    this.refreshTokenRepository = refreshTokenRepository;
  }

  public AuthResponse register(RegisterRequest request) {

    if (userRepository.existsByEmail(request.getEmail())) {
      throw new BadRequestException("Email already in use");
    }

    User user = User.builder().email(request.getEmail()).name(request.getName())
        .password(passwordEncoder.encode(request.getPassword())).provider("LOCAL").build();

    userRepository.save(user);

    return generateTokens(user);
  }

  public AuthResponse login(LoginRequest request) {

    User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      throw new BadRequestException("Invalid credentials");
    }

    return generateTokens(user);
  }

  public AuthResponse refresh(String refreshToken) {

    RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
        .orElseThrow(() -> new BadRequestException("Invalid refresh token"));

    if (token.getExpiresAt().isBefore(Instant.now())) {
      throw new BadRequestException("Refresh token expired");
    }

    User user = token.getUser();

    String newAccessToken = jwtService.generateToken(user.getId(), user.getEmail());

    return new AuthResponse(newAccessToken, refreshToken);
  }

  public void logout(Long userId) {
    refreshTokenRepository.deleteByUserId(userId);
  }

  private AuthResponse generateTokens(User user) {

    String accessToken = jwtService.generateToken(user.getId(), user.getEmail());

    String refreshToken = UUID.randomUUID().toString();

    RefreshToken refresh = RefreshToken.builder().token(refreshToken).user(user)
        .expiresAt(Instant.now().plus(30, ChronoUnit.DAYS)).build();

    refreshTokenRepository.save(refresh);

    return new AuthResponse(accessToken, refreshToken);
  }
}
