package com.worklynx.backend.security;

import org.springframework.http.ResponseCookie;

public class CookieUtils {

  public static ResponseCookie createAccessTokenCookie(String token) {

    return ResponseCookie.from("accessToken", token).httpOnly(true)
        .secure(false) // true in production
        .path("/").maxAge(15 * 60)
        .sameSite("Lax").build();
  }

  public static ResponseCookie createRefreshTokenCookie(String token) {

    return ResponseCookie.from("refreshToken", token).httpOnly(true).secure(false).path("/").maxAge(30L * 24 * 60 * 60)
        .sameSite("Lax").build();
  }

  public static ResponseCookie deleteAccessTokenCookie() {

    return ResponseCookie.from("accessToken", "").httpOnly(true).secure(false).path("/").maxAge(0).sameSite("Lax")
        .build();
  }

  public static ResponseCookie deleteRefreshTokenCookie() {

    return ResponseCookie.from("refreshToken", "").httpOnly(true).secure(false).path("/").maxAge(0).sameSite("Lax")
        .build();
  }
}
