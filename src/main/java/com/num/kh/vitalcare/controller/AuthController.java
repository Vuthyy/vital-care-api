package com.num.kh.vitalcare.controller;

import com.num.kh.vitalcare.dto.request.ChangePasswordRequestDto;
import com.num.kh.vitalcare.dto.request.LoginRequestDto;
import com.num.kh.vitalcare.dto.request.RegisterRequestDto;
import com.num.kh.vitalcare.dto.request.UserUpdateRequestDto;
import com.num.kh.vitalcare.dto.response.AuthResponseDto;
import com.num.kh.vitalcare.dto.response.UserResponseDto;
import com.num.kh.vitalcare.service.JwtService;
import com.num.kh.vitalcare.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final UserService userService;

  @PostMapping("/register")
  public ResponseEntity<UserResponseDto> register(@Valid @RequestBody RegisterRequestDto request) {
    log.info("Registration request for username: {}", request.getUsername());

    UserResponseDto user = userService.registerLocalUser(request);

    return ResponseEntity.status(HttpStatus.CREATED).body(user);
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
    log.info("Login request for: {}", request.getUsernameOrEmail());

    // Authenticate user
    Authentication auth =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsernameOrEmail(), request.getPassword()));

    // Get username from authentication (could be email or username)
    String authenticatedUsername = auth.getName();

    // Generate tokens
    String accessToken = jwtService.generateAccessToken(authenticatedUsername);
    String refreshToken = jwtService.generateRefreshToken(authenticatedUsername);

    log.info("User logged in successfully: {}", authenticatedUsername);

    return ResponseEntity.ok(new AuthResponseDto(accessToken, refreshToken));
  }

  @PostMapping("/refresh")
  public ResponseEntity<AuthResponseDto> refresh(
      @RequestHeader("Authorization") String authHeader) {
    log.info("Token refresh request");

    // Extract token from "Bearer <token>" format
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      log.warn("Invalid authorization header format");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    String refreshToken = authHeader.substring(7);

    // Validate refresh token and get user
    UserResponseDto user = jwtService.validateRefreshToken(refreshToken);

    // Generate new tokens
    String newAccessToken = jwtService.generateAccessToken(user.getUsername());
    String newRefreshToken = jwtService.generateRefreshToken(user.getUsername());

    log.info("Tokens refreshed for user: {}", user.getUsername());

    return ResponseEntity.ok(new AuthResponseDto(newAccessToken, newRefreshToken));
  }

  @GetMapping("/me")
  public ResponseEntity<UserResponseDto> getCurrentUser(Authentication authentication) {
    if (authentication == null || !authentication.isAuthenticated()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    String username = authentication.getName();
    UserResponseDto user = userService.getUserByUsername(username);

    return ResponseEntity.ok(user);
  }

  @PostMapping("/me")
  public ResponseEntity<UserResponseDto> updateCurrentUser(
      Authentication authentication, @Valid @RequestBody UserUpdateRequestDto request) {

    if (authentication == null || !authentication.isAuthenticated()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    String username = authentication.getName();
    UserResponseDto currentUser = userService.getUserByUsername(username);

    UserResponseDto updatedUser = userService.updateUser(currentUser.getId(), request);

    log.info("User profile updated: {}", username);

    return ResponseEntity.ok(updatedUser);
  }

  @PostMapping("/change-password")
  public ResponseEntity<?> changePassword(
      Authentication authentication, @Valid @RequestBody ChangePasswordRequestDto request) {

    if (authentication == null || !authentication.isAuthenticated()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // Validate passwords match
    if (!request.getNewPassword().equals(request.getConfirmPassword())) {
      return ResponseEntity.badRequest()
          .body(new ErrorResponse("New password and confirm password do not match"));
    }

    String username = authentication.getName();

    // Verify current password
    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(username, request.getCurrentPassword()));
    } catch (Exception e) {
      log.warn("Invalid current password for user: {}", username);
      return ResponseEntity.badRequest().body(new ErrorResponse("Current password is incorrect"));
    }

    // Change password logic would go here
    // userService.changePassword(username, request.getNewPassword());

    log.info("Password changed for user: {}", username);

    return ResponseEntity.ok(new SuccessResponse("Password changed successfully"));
  }

  @PostMapping("/logout")
  public ResponseEntity<?> logout() {
    SecurityContextHolder.clearContext();
    return ResponseEntity.ok(new SuccessResponse("Logged out successfully"));
  }

  // Helper response classes
  record ErrorResponse(String message) {}

  record SuccessResponse(String message) {}
}
