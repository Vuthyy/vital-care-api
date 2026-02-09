package com.num.kh.vitalcare.controller;

import com.num.kh.vitalcare.dto.response.AvailabilityResponseDto;
import com.num.kh.vitalcare.dto.response.UserResponseDto;
import com.num.kh.vitalcare.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping("/{userId}")
  public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long userId) {
    UserResponseDto user = userService.getUserById(userId);
    return ResponseEntity.ok(user);
  }

  @GetMapping("/username/{username}")
  public ResponseEntity<UserResponseDto> getUserByUsername(@PathVariable String username) {
    UserResponseDto user = userService.getUserByUsername(username);
    return ResponseEntity.ok(user);
  }

  @GetMapping("/email/{email}")
  public ResponseEntity<UserResponseDto> getUserByEmail(@PathVariable String email) {
    UserResponseDto user = userService.getUserByEmail(email);
    return ResponseEntity.ok(user);
  }

  @GetMapping("/check/username/{username}")
  public ResponseEntity<AvailabilityResponseDto> checkUsernameAvailability(
      @PathVariable String username) {
    boolean available = !userService.existsByUsername(username);
    return ResponseEntity.ok(new AvailabilityResponseDto(available));
  }

  @GetMapping("/check/email/{email}")
  public ResponseEntity<AvailabilityResponseDto> checkEmailAvailability(@PathVariable String email) {
    boolean available = !userService.existsByEmail(email);
    return ResponseEntity.ok(new AvailabilityResponseDto(available));
  }
}
