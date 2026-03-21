package com.worklynx.backend.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.worklynx.backend.auth.dto.LoginRequest;
import com.worklynx.backend.auth.dto.RegisterRequest;
import com.worklynx.backend.security.JwtService;
import com.worklynx.backend.user.User;
import com.worklynx.backend.user.UserRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  public AuthController(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      JwtService jwtService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
  }

  @PostMapping("/register")
  public String register(@RequestBody @Valid RegisterRequest request) {

    User user = User.builder().email(request.getEmail()).name(request.getName())
        .password(passwordEncoder.encode(request.getPassword())).provider("LOCAL").build();

    userRepository.save(user);

    return jwtService.generateToken(user.getId(), user.getEmail());
  }

  @PostMapping("/login")
  public String login(@RequestBody @Valid LoginRequest request) {

    User user = userRepository.findByEmail(request.getEmail()).orElseThrow();

    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      throw new RuntimeException("Invalid credentials");
    }

    return jwtService.generateToken(user.getId(), user.getEmail());
  }
}
