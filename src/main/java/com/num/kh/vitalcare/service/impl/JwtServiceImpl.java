package com.num.kh.vitalcare.service.impl;

import com.num.kh.vitalcare.config.properties.JwtProperties;
import com.num.kh.vitalcare.dto.response.UserResponseDto;
import com.num.kh.vitalcare.entity.UserEntity;
import com.num.kh.vitalcare.mapper.UserMapper;
import com.num.kh.vitalcare.repository.UserRepository;
import com.num.kh.vitalcare.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.function.Function;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final JwtProperties jwtProperties;

  private SecretKey getSigningKey() {
    byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  @Override
  public String generateAccessToken(String username) {
    log.debug("Generating access token for user: {}", username);

    Instant now = Instant.now();
    Instant expiration = now.plus(jwtProperties.getAccessExpMinutes(), ChronoUnit.MINUTES);

    return Jwts.builder()
        .subject(username)
        .claim("type", "access")
        .issuedAt(Date.from(now))
        .expiration(Date.from(expiration))
        .signWith(getSigningKey())
        .compact();
  }

  @Override
  public String generateRefreshToken(String username) {
    log.debug("Generating refresh token for user: {}", username);

    Instant now = Instant.now();
    Instant expiration = now.plus(jwtProperties.getRefreshExpDays(), ChronoUnit.DAYS);

    return Jwts.builder()
        .subject(username)
        .claim("type", "refresh")
        .issuedAt(Date.from(now))
        .expiration(Date.from(expiration))
        .signWith(getSigningKey())
        .compact();
  }

  @Override
  public UserResponseDto validateAccessToken(String token) {
    log.debug("Validating access token");

    Claims claims = extractAllClaims(token);
    String tokenType = claims.get("type", String.class);

    if (!"access".equals(tokenType)) {
      throw new BadCredentialsException("Invalid token type");
    }

    String username = claims.getSubject();
    return getUserResponseDto(username);
  }

  @Override
  public UserResponseDto validateRefreshToken(String token) {
    log.debug("Validating refresh token");

    Claims claims = extractAllClaims(token);
    String tokenType = claims.get("type", String.class);

    if (!"refresh".equals(tokenType)) {
      throw new BadCredentialsException("Invalid refresh token");
    }

    String username = claims.getSubject();
    return getUserResponseDto(username);
  }

  @Override
  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  @Override
  public boolean isTokenValid(String token) {
    try {
      extractAllClaims(token);
      return !isTokenExpired(token);
    } catch (ExpiredJwtException e) {
      log.warn("Token expired: {}", e.getMessage());
      return false;
    } catch (MalformedJwtException | SignatureException e) {
      log.warn("Invalid token: {}", e.getMessage());
      return false;
    } catch (Exception e) {
      log.error("Token validation error: {}", e.getMessage());
      return false;
    }
  }

  @Override
  public boolean isTokenExpired(String token) {
    try {
      Date expiration = extractExpiration(token);
      return expiration.before(new Date());
    } catch (ExpiredJwtException e) {
      return true;
    }
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    try {
      return Jwts.parser()
          .verifyWith(getSigningKey())
          .build()
          .parseSignedClaims(token)
          .getPayload();
    } catch (ExpiredJwtException e) {
      log.warn("Token expired: {}", e.getMessage());
      throw new BadCredentialsException("Token expired", e);
    } catch (MalformedJwtException e) {
      log.warn("Malformed token: {}", e.getMessage());
      throw new BadCredentialsException("Invalid token format", e);
    } catch (SignatureException e) {
      log.warn("Invalid token signature: {}", e.getMessage());
      throw new BadCredentialsException("Invalid token signature", e);
    } catch (Exception e) {
      log.error("Token parsing error: {}", e.getMessage());
      throw new BadCredentialsException("Token validation failed", e);
    }
  }

  private UserResponseDto getUserResponseDto(String username) {
    UserEntity userEntity =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new BadCredentialsException("User not found: " + username));

    return userMapper.to(userEntity);
  }
}
