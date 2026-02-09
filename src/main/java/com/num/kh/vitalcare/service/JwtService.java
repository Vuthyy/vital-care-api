package com.num.kh.vitalcare.service;

import com.num.kh.vitalcare.dto.response.UserResponseDto;

public interface JwtService {

  String generateAccessToken(String username);

  String generateRefreshToken(String username);

  UserResponseDto validateAccessToken(String token);

  UserResponseDto validateRefreshToken(String token);

  String extractUsername(String token);

  boolean isTokenValid(String token);

  boolean isTokenExpired(String token);
}
