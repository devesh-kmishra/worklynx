package com.worklynx.backend.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import com.worklynx.backend.config.JwtConfig;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

  private final JwtConfig jwtConfig;

  public JwtService(JwtConfig jwtConfig) {
    this.jwtConfig = jwtConfig;
  }

  private SecretKey getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(jwtConfig.getSecret());

    return Keys.hmacShaKeyFor(keyBytes);
  }

  public String generateToken(Long userId, String email) {

    return Jwts.builder().subject(String.valueOf(userId)).claim("email", email).issuer("worklynx").issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + jwtConfig.getExpiration()))
        .signWith(getSigningKey(), Jwts.SIG.HS256)
        .compact();
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
  }

  public Long extractUserId(String token) {
    return Long.parseLong(extractAllClaims(token).getSubject());
  }

  public String extractEmail(String token) {
    return extractAllClaims(token).get("email", String.class);
  }

  public boolean isTokenValid(String token) {
    try {
      Claims claims = extractAllClaims(token);

      return claims.getExpiration().after(new Date());
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }
}
