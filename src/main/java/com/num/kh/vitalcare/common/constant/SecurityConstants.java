package com.num.kh.vitalcare.common.constant;

public final class SecurityConstants {

  private SecurityConstants() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }

  public static final String AUTH_HEADER = "Authorization";
  public static final String TOKEN_PREFIX = "Bearer ";
  public static final String REFRESH_TOKEN_COOKIE = "refresh_token";

  // Token expiration times (in milliseconds)
  public static final long ACCESS_TOKEN_VALIDITY = 15 * 60 * 1000; // 15 minutes
  public static final long REFRESH_TOKEN_VALIDITY = 30 * 24 * 60 * 60 * 1000L; // 30 days

  // Roles
  public static final String ROLE_USER = "ROLE_USER";
  public static final String ROLE_ADMIN = "ROLE_ADMIN";
}
